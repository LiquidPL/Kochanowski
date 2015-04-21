package com.github.LiquidPL.kochanowski.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.*;
import com.github.LiquidPL.kochanowski.parse.Type;
import com.github.LiquidPL.kochanowski.util.DbUtils;



import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableListFragment
        extends Fragment
{
    // the fragment initialization parameters
    private static final String ARG_TABLE_TYPE = "type";
    private static final String ARG_SEARCH_QUERY = "query";

    // the parameters initialized in newInstance ()
    private int tableType;
    private String searchQuery;

    // list containing names apppearing in the recycler view
    private List<String> names;
    // values corresponding to the names in list above,
    // passed to TimeTableDisplayFragment
    private List<String> values;

    // RecyclerView in which items are displayed
    private RecyclerView recyclerView;

    // LayoutManager managing the RecyclerView above
    private RecyclerView.LayoutManager layoutManager;

    // Listener to an activity which have created the fragment
    // and is responsible for receving the clicks
    private OnTimeTableSelectedListener listener;

    // Adapter providing items to the RecyclerView
    RecyclerView.Adapter adapter;

    public interface OnTimeTableSelectedListener
    {
        void onTimeTableSelected (String name, String value, int tableType);
    }

    private class LoadTablesIntoAdapterTask
            extends AsyncTask<Integer, Void, Void>
    {
        String searchQuery;

        private LoadTablesIntoAdapterTask (String searchQuery)
        {
            this.searchQuery = searchQuery;
        }

        @Override
        protected Void doInBackground (Integer... params)
        {
            int tableType = params[0];

            boolean distinct = false;
            String tableName = "";
            String columns[] = new String[] {};
            String selection = "";
            String orderBy = "";

            // setting up the database query
            switch (tableType)
            {
                case Type.CLASS:
                    distinct = false;
                    tableName = ClassTable.TABLE_NAME;
                    columns = null;
                    orderBy = ClassTable.COLUMN_NAME_NAME_SHORT + " ASC";
                    break;
                case Type.TEACHER:
                    distinct = false;
                    tableName = TeacherTable.TABLE_NAME;
                    columns = null;
                    orderBy = TeacherTable.COLUMN_NAME_TEACHER_SURNAME + " ASC";
                    break;
                case Type.CLASSROOM:
                    distinct = true;
                    tableName = LessonTable.TABLE_NAME;
                    columns = new String[] {LessonTable.COLUMN_NAME_CLASSROOM_NAME};
                    orderBy = LessonTable.COLUMN_NAME_CLASSROOM_NAME + " ASC";
                    break;
            }

            if (searchQuery != null)
            {
                switch (tableType)
                {
                    case Type.CLASS:
                        selection = ClassTable.COLUMN_NAME_NAME_SHORT + " LIKE '%" + searchQuery + "%' OR " +
                                ClassTable.COLUMN_NAME_NAME_LONG + " LIKE '%" + searchQuery + "%'";
                        break;
                    case Type.TEACHER:
                        selection = TeacherTable.COLUMN_NAME_TEACHER_ID + " LIKE '%" + searchQuery + "%' OR " +
                                TeacherTable.COLUMN_NAME_TEACHER_NAME + " LIKE '%" + searchQuery + "%' OR " +
                                TeacherTable.COLUMN_NAME_TEACHER_SURNAME + " LIKE '%" + searchQuery + "%'";
                        break;
                    case Type.CLASSROOM:
                        selection = LessonTable.COLUMN_NAME_CLASSROOM_NAME + " LIKE '%" + searchQuery + "%'";
                        break;
                }
            }

            // getting the database instance
            SQLiteDatabase db = DbUtils.getInstance ().openDatabase ();

            // performing the query
            Cursor cur = db.query (distinct, tableName, columns, selection, null, null, null, orderBy, null);

            // cleaning up the lists in case that we need to redo the query
            names = new ArrayList<> ();
            values = new ArrayList<> ();

            // reading the database
            cur.moveToFirst ();

            while (!cur.isAfterLast ())
            {
                switch (tableType)
                {
                    case Type.CLASS:
                        names.add (cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG)) + " (" +
                                           cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT)) + ")");

                        values.add (cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT)));
                        break;
                    case Type.TEACHER:
                        names.add (cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_NAME)) + " " +
                                           cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_SURNAME)) + " (" +
                                           cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_ID)) + ")");

                        values.add (cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_ID)));
                        break;
                    case Type.CLASSROOM:
                        names.add (cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM_NAME)));

                        values.add (cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM_NAME)));
                        break;
                }

                cur.moveToNext ();
            }

            // closing the database
            cur.close ();
            DbUtils.getInstance ().closeDatabase ();

            return null;
        }

        @Override
        protected void onPostExecute (Void aVoid)
        {
            super.onPostExecute (aVoid);

            adapter = new TimeTableListAdapter ();
            recyclerView.setAdapter (adapter);
        }
    }

    private class TimeTableListAdapter extends RecyclerView.Adapter<TimeTableListAdapter.ListViewHolder>
    {
        public class ListViewHolder
                extends RecyclerView.ViewHolder
                implements View.OnClickListener
        {
            // TextView widget containing the name of a timetable
            public TextView name;

            public ListViewHolder (View v)
            {
                super (v);

                // setting an ItemClickListener for the view
                v.setOnClickListener (this);

                name = (TextView) v.findViewById (R.id.timetable_name);
            }

            @Override
            public void onClick (View v)
            {
                // position of an item in a recycler view
                int position = getPosition ();

                listener.onTimeTableSelected (
                        names.get (position),
                        values.get (position),
                        tableType);
            }
        }

        @Override
        public ListViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.tablelist_item, parent, false);

            return new ListViewHolder (v);
        }

        @Override
        public void onBindViewHolder (ListViewHolder holder, int position)
        {
            holder.name.setText (names.get (position));
        }

        @Override
        public int getItemCount ()
        {
            return names.size ();
        }
    }

    /**
     * This factory method creates a new TimeTableListFragment
     * given the following parameters.
     *
     * @param tableType The timetable type (class, teacher, classroom)
     * @param searchQuery String to be searched in the database query.
     *                    If null, all items are shown.
     * @return A new instance of TimeTableListFragment.
     */
    public static TimeTableListFragment newInstance (int tableType, String searchQuery)
    {
        TimeTableListFragment fragment = new TimeTableListFragment ();

        Bundle args = new Bundle ();

        args.putInt (ARG_TABLE_TYPE, tableType);
        args.putString (ARG_SEARCH_QUERY, searchQuery);

        fragment.setArguments (args);

        return fragment;
    }

    public TimeTableListFragment ()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        if (getArguments () != null)
        {
            tableType = getArguments ().getInt (ARG_TABLE_TYPE);
            searchQuery = getArguments ().getString (ARG_SEARCH_QUERY);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        // inflate layout for this fragment
        View view = inflater.inflate (R.layout.fragment_time_table_list, container, false);

        // get instance of RecyclerView and set it up
        recyclerView = (RecyclerView) view.findViewById (R.id.timetable_list);
        layoutManager = new LinearLayoutManager (getActivity ());
        recyclerView.setLayoutManager (layoutManager);

        // loading items from database and attaching an adapter to the RecyclerView
        new LoadTablesIntoAdapterTask (searchQuery).execute (tableType);

        return view;
    }

    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach (activity);

        try
        {
            this.listener = ((OnTimeTableSelectedListener) activity);
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException (activity.getClass ().getSimpleName () +
                                          " must implement OnTimeTableSelectedListener");
        }
    }

    @Override
    public void onDetach ()
    {
        super.onDetach ();
    }

    public void setFilter (int tableType)
    {
        this.tableType = tableType;

        // rereading data from database and reattaching the adapter
        new LoadTablesIntoAdapterTask (searchQuery).execute (tableType);
    }
}
