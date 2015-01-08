package com.github.LiquidPL.kochanowski.ui;


import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.ClassTable;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.LessonTable;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.TeacherTable;
import com.github.LiquidPL.kochanowski.util.DbUtils;
import com.liquid.kochanparser.TimeTableType;

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
    public class TimeTableListAdapter extends RecyclerView.Adapter<TimeTableListAdapter.ListViewHolder>
    {
        private Cursor cur;
        private SQLiteDatabase db;

        private int tableType;

        public class ListViewHolder extends RecyclerView.ViewHolder
        {
            TextView name;

            public ListViewHolder (View v)
            {
                super (v);

                name = (TextView) v.findViewById (R.id.timetable_name);
            }
        }

        public TimeTableListAdapter (int tableType)
        {
            db = DbUtils.getReadableDatabase ();
            this.tableType = tableType;

            switch (tableType)
            {
                case TimeTableType.CLASS:
                    cur = db.rawQuery ("SELECT * FROM classes ORDER BY longname ASC", null);
                    break;
                case TimeTableType.TEACHER:
                    cur = db.rawQuery ("SELECT * FROM teachers ORDER BY code ASC" ,null);
                    break;
                case TimeTableType.CLASSROOM:
                    cur = db.rawQuery ("SELECT DISTINCT classroom FROM lessons ORDER BY classroom ASC", null);
                    break;
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
            cur.moveToPosition (position);

            switch (tableType)
            {
                case TimeTableType.CLASS:
                    String shortname = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT));
                    String longname = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG));

                    holder.name.setText (longname + " (" + shortname + ")");
                    break;
                case TimeTableType.TEACHER:
                    String name = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_NAME));
                    String surname = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_SURNAME));
                    String code = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_CODE));

                    holder.name.setText (name + " " + surname + " (" + code + ")");
                    break;
                case TimeTableType.CLASSROOM:
                    String classroomName = cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM));

                    holder.name.setText (classroomName);
                    break;
            }
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

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "type";

    private int tableType;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ItemClickSupport clickSupport;

    private OnTimeTableSelectedListener listener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tableType
     * @return A new instance of fragment TimeTableListFragment.
     */
    public static TimeTableListFragment newInstance (int tableType)
    {
        TimeTableListFragment fragment = new TimeTableListFragment ();
        Bundle args = new Bundle ();

        args.putInt (ARG_TYPE, tableType);

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
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v =  inflater.inflate (R.layout.fragment_time_table_list, container, false);

        recyclerView = (RecyclerView) v.findViewById (R.id.timetable_list);
        layoutManager = new ListLayoutManager (this.getActivity (), TwoWayLayoutManager.Orientation.VERTICAL);
        recyclerView.setLayoutManager (layoutManager);

        adapter = new TimeTableListAdapter (tableType);

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
                    case TimeTableType.CLASS:
                        shortName = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT));
                        longName = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG));
                        break;
                    case TimeTableType.TEACHER:
                        shortName = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_CODE));
                        longName = cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_NAME)) +
                                " " + cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_SURNAME));
                        break;
                    case TimeTableType.CLASSROOM:
                        shortName = cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM));
                        break;
                }

                listener.onTimeTableSelected (shortName, longName, tableType);
            }
        });

        return v;
    }

    public void setFilter (int filter)
    {
        if (filter == tableType) return;

        tableType = filter;
        adapter = new TimeTableListAdapter (tableType);
        recyclerView.setAdapter (adapter);
    }

    public int getTableType ()
    {
        return tableType;
    }
}
