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
import com.github.LiquidPL.kochanowski.db.TimeTableContract;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.ClassTable;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.LessonTable;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.TeacherTable;
import com.github.LiquidPL.kochanowski.parse.Type;
import com.github.LiquidPL.kochanowski.util.DbUtils;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableListFragment extends Fragment
{
    // the fragment initialization parameters
    private static final String ARG_TYPE = "type";
    private static final String ARG_QUERY = "query";

    private int tableType;
    private String searchQuery;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ItemClickSupport clickSupport;

    private OnTimeTableSelectedListener listener;

    private SQLiteDatabase db;

    private class LoadItemFromDatabaseTask extends AsyncTask<Integer, Void, String>
    {
        private Cursor cur;

        private TimeTableListAdapter.ListViewHolder viewHolder;

        private LoadItemFromDatabaseTask (Cursor cur,
                                          TimeTableListAdapter.ListViewHolder viewHolder)
        {
            this.cur = cur;
            this.viewHolder = viewHolder;
        }

        @Override
        protected String doInBackground (Integer... params)
        {
            // moving the cursor to the required position
            cur.moveToPosition (params[0]);

            String result = "";

            switch (tableType)
            {
                case Type.CLASS:
                    result = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG)) + " (" +
                             cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT)) + ")";
                    break;
                case Type.CLASSROOM:
                    result = cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM));
                    break;
                case Type.TEACHER:
                    result = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_NAME)) + " " +
                             cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_SURNAME)) + " (" +
                             cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_CODE)) + ")";
                    break;
            }

            return result;
        }

        @Override
        protected void onPostExecute (String s)
        {
            super.onPostExecute (s);

            viewHolder.name.setText (s);
        }
    }

    public class TimeTableListAdapter extends RecyclerView.Adapter<TimeTableListAdapter.ListViewHolder>
    {
        private SQLiteDatabase db;
        private Cursor cur;

        private int tableType;

        boolean distinct;
        String tableName;
        String[] columns;
        String selection;
        String orderBy;

        public class ListViewHolder extends RecyclerView.ViewHolder
        {
            TextView name;

            public ListViewHolder (View v)
            {
                super (v);

                name = (TextView) v.findViewById (R.id.timetable_name);
            }
        }

        public TimeTableListAdapter (SQLiteDatabase db, int tableType, String query)
        {
            this.db = db;
            this.tableType = tableType;

            cur = formCursor (tableType, query);
        }

        private Cursor formCursor (int type, String query)
        {
            switch (type)
            {
                case Type.CLASS:
                    distinct = false;
                    tableName = TimeTableContract.ClassTable.TABLE_NAME;
                    columns = null;
                    orderBy = TimeTableContract.ClassTable.COLUMN_NAME_NAME_LONG + " ASC";
                    break;
                case Type.TEACHER:
                    distinct = false;
                    tableName = TimeTableContract.TeacherTable.TABLE_NAME;
                    columns = null;
                    orderBy = TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_NAME + " ASC";
                    break;
                case Type.CLASSROOM:
                    distinct = true;
                    tableName = TimeTableContract.LessonTable.TABLE_NAME;
                    columns = new String[] {TimeTableContract.LessonTable.COLUMN_NAME_CLASSROOM};
                    orderBy = TimeTableContract.LessonTable.COLUMN_NAME_CLASSROOM + " ASC";
                    break;
            }

            if (query != null)
            {
                switch (tableType)
                {
                    case Type.CLASS:
                        selection = ClassTable.COLUMN_NAME_NAME_SHORT + " LIKE '%" + searchQuery + "%' OR " +
                                ClassTable.COLUMN_NAME_NAME_LONG + " LIKE '%" + searchQuery + "%'";
                        break;
                    case Type.TEACHER:
                        selection = TeacherTable.COLUMN_NAME_TEACHER_CODE + " LIKE '%" + searchQuery + "%' OR " +
                                TeacherTable.COLUMN_NAME_TEACHER_NAME + " LIKE '%" + searchQuery + "%' OR " +
                                TeacherTable.COLUMN_NAME_TEACHER_SURNAME + " LIKE '%" + searchQuery + "%'";
                        break;
                    case Type.CLASSROOM:
                        selection = LessonTable.COLUMN_NAME_CLASSROOM + " LIKE '%" + searchQuery + "%'";
                        break;
                }
            }

            return db.query (distinct, tableName, columns, selection, null, null, null, orderBy, null);
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
            new LoadItemFromDatabaseTask (cur, holder).execute (position);
        }

        @Override
        public int getItemCount ()
        {
            return cur.getCount ();
        }

        public Cursor getCursor ()
        {
            return cur;
        }
    }

    public interface OnTimeTableSelectedListener
    {
        public void onTimeTableSelected (String shortName, String longName, int tableType);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tableType
     * @return A new instance of fragment TimeTableListFragment.
     */
    public static TimeTableListFragment newInstance (int tableType, String query)
    {
        TimeTableListFragment fragment = new TimeTableListFragment ();
        Bundle args = new Bundle ();

        args.putInt (ARG_TYPE, tableType);
        args.putString (ARG_QUERY, query);

        fragment.setArguments (args);
        return fragment;
    }

    public TimeTableListFragment ()
    {
        // Required empty public constructor
    }

    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach (activity);

        try
        {
            listener = (OnTimeTableSelectedListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException (activity.toString () + " must implement OnTimeTableSelectListener");
        }
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        if (getArguments () != null)
        {
            tableType = getArguments ().getInt (ARG_TYPE);
            searchQuery = getArguments ().getString (ARG_QUERY);
        }

        db = DbUtils.getInstance ().openDatabase ();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v =  inflater.inflate (R.layout.fragment_time_table_list, container, false);

        recyclerView = (RecyclerView) v.findViewById (R.id.timetable_list);
        layoutManager = new LinearLayoutManager (getActivity ());
        recyclerView.setLayoutManager (layoutManager);

        adapter = new TimeTableListAdapter (db, tableType, searchQuery);

        recyclerView.setAdapter (adapter);

        clickSupport = ItemClickSupport.addTo (recyclerView);

        clickSupport.setOnItemClickListener (new ItemClickSupport.OnItemClickListener ()
        {
            @Override
            public void onItemClick (RecyclerView recyclerView, View view, int position, long id)
            {
                String shortName = "";
                String longName = "";

                Cursor cur = ((TimeTableListAdapter) adapter).getCursor ();
                cur.moveToPosition (position);

                switch (tableType)
                {
                    case Type.CLASS:
                        shortName = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT));
                        longName = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG));
                        break;
                    case Type.TEACHER:
                        shortName = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_CODE));
                        longName = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_NAME)) +
                                " " + cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_SURNAME));
                        break;
                    case Type.CLASSROOM:
                        shortName = cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM));
                        break;
                }

                listener.onTimeTableSelected (shortName, longName, tableType);
            }
        });

        return v;
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy ();

        DbUtils.getInstance ().closeDatabase ();
    }

    public void setFilter (int filter)
    {
        if (filter == tableType) return;

        tableType = filter;
        adapter = new TimeTableListAdapter (db, tableType, searchQuery);
        recyclerView.setAdapter (adapter);
    }

    public void performSearch (String query)
    {

    }

    public int getTableType ()
    {
        return tableType;
    }
}
