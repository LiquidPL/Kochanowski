package com.liquid.kochanowski;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liquid.kochanparser.TimeTableType;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableDisplayFragment extends Fragment implements View.OnClickListener
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TABLE_NAME = "tablename";
    private static final String ARG_TABLE_TYPE = "tabletype";
    private static final String ARG_DAY_ID     = "dayid";
    private static final String ARG_GROUP_ID   = "groupid";

    // the given fragments parameters
    private String tableName;
    private int tableType;
    private int dayId;
    private int groupId;

    private Activity activity;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button syncButton;
    private TextView noTimeTablesAlert;

    private SharedPreferences prefs;

    private class LessonListAdapter extends RecyclerView.Adapter<LessonListAdapter.LessonViewHolder>
    {
        private Cursor cur;

        private Context context;

        private String tableName;
        private int tableType;
        private int dayId;
        private int groupId;

        private int resource;

        private int lastPosition = -1;

        public class LessonViewHolder extends RecyclerView.ViewHolder
        {
            public TextView subjectName;
            public TextView teacherName;
            public TextView classroomName;
            public TextView groupName;

            public RelativeLayout container;

            public LessonViewHolder (View v)
            {
                super (v);

                subjectName = (TextView) v.findViewById (R.id.subjectName);
                teacherName = (TextView) v.findViewById (R.id.teacherName);
                classroomName = (TextView) v.findViewById (R.id.classroomName);
                groupName = (TextView) v.findViewById (R.id.groupName);

                container = (RelativeLayout) v;
            }
        }

        public LessonListAdapter (int resource, SQLiteDatabase db, String tableName, int tableType, int dayId, int groupId, Context context)
        {
            this.tableName = tableName;
            this.tableType = tableType;
            this.dayId = dayId;
            this.groupId = groupId;
            this.context = context;

            this.resource = resource;

            String query = "SELECT * FROM " + TimeTableContract.LessonTable.TABLE_NAME +
                    " JOIN " + TimeTableContract.TeacherTable.TABLE_NAME +
                    " ON " + TimeTableContract.LessonTable.TABLE_NAME + "." + TimeTableContract.LessonTable.COLUMN_NAME_TEACHER_CODE +
                    "=" + TimeTableContract.TeacherTable.TABLE_NAME + "." + TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_CODE +
                    " JOIN " + TimeTableContract.ClassTable.TABLE_NAME +
                    " ON " + TimeTableContract.LessonTable.TABLE_NAME + "." + TimeTableContract.LessonTable.COLUMN_NAME_CLASS_NAME_SHORT +
                    "=" + TimeTableContract.ClassTable.TABLE_NAME + "." + TimeTableContract.ClassTable.COLUMN_NAME_NAME_SHORT;

            switch (tableType)
            {
                case TimeTableType.CLASS:
                    query += " WHERE " + TimeTableContract.LessonTable.TABLE_NAME + "." + TimeTableContract.LessonTable.COLUMN_NAME_CLASS_NAME_SHORT + "='" + tableName + "'";
                    break;
                case TimeTableType.TEACHER:
                    query += " WHERE " + TimeTableContract.LessonTable.TABLE_NAME + "." + TimeTableContract.LessonTable.COLUMN_NAME_TEACHER_CODE + "='" + tableName + "'";
                    break;
                case TimeTableType.CLASSROOM:
                    query += " WHERE " + TimeTableContract.LessonTable.COLUMN_NAME_CLASSROOM + "=" + tableName;
                    break;
            }

            query += " AND " + TimeTableContract.LessonTable.COLUMN_NAME_DAY + "=" + dayId;

            if (groupId != 0)
            {
                query += " AND (" + TimeTableContract.LessonTable.COLUMN_NAME_GROUP_ID + "=" + "0" +
                        " OR " + TimeTableContract.LessonTable.COLUMN_NAME_GROUP_ID + "=" + groupId + ")";
            }

            query += " ORDER BY " + TimeTableContract.LessonTable.COLUMN_NAME_HOUR_ID + " ASC";

            Log.i ("query", query);

            cur = db.rawQuery (query, null);
        }

        @Override
        public void onBindViewHolder (LessonViewHolder holder, int position)
        {
            cur.moveToPosition (position);

            String subject = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.LessonTable.COLUMN_NAME_SUBJECT));
            String classroom = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.LessonTable.COLUMN_NAME_CLASSROOM));
            String add = "";
            String add2 = "";
            String add3 = "";

            //holder.subjectName.setText (subject);
            //holder.classroomName.setText (classroom);

            switch (tableType)
            {
                case TimeTableType.CLASS:
                    add = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.LessonTable.COLUMN_NAME_TEACHER_CODE));
                    add2 = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_NAME));
                    add3 = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_SURNAME));

                    holder.subjectName.setText (subject);
                    holder.classroomName.setText (classroom);
                    holder.teacherName.setText (add2 + " " + add3 + " (" + add + ")");

                    //Log.i ("liquid", subject + " " + classroom + " " + add);

                    break;
                case TimeTableType.TEACHER:
                    add = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.LessonTable.COLUMN_NAME_CLASS_NAME_SHORT));
                    add2 = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.ClassTable.COLUMN_NAME_NAME_LONG));

                    holder.subjectName.setText (subject);
                    holder.classroomName.setText (classroom);
                    holder.teacherName.setText (add2 + " (" + add + ")");

                    break;
                case TimeTableType.CLASSROOM:
                    add = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.LessonTable.COLUMN_NAME_TEACHER_CODE));
                    add2 = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_NAME));
                    add3 = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_SURNAME));

                    String classname = cur.getString (cur.getColumnIndexOrThrow (TimeTableContract.LessonTable.COLUMN_NAME_CLASS_NAME_SHORT));

                    holder.subjectName.setText (subject);
                    holder.classroomName.setText (classname);
                    holder.teacherName.setText (add2 + " " + add3 + " (" + add + ")");



                    break;
            }

            int group = cur.getInt (cur.getColumnIndexOrThrow (TimeTableContract.LessonTable.COLUMN_NAME_GROUP_ID));
            if (group != 0)
            {
                holder.groupName.setText ("GRUPA " + group);
                holder.groupName.setVisibility (View.VISIBLE);
            }
        }

        @Override
        public LessonViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.lesson_item, parent, false);

            return new LessonViewHolder (v);
        }

        @Override
        public int getItemCount ()
        {
            return cur.getCount ();
        }
    }


    interface Clickable
    {
        void onSyncButtonClick (View v);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tableName Name of the timetable, corresponding to a column in the database
     * @param tableType The timetable type to display (class/teacher/classroom)
     * @param groupId The group to be displayed
     *
     * @return A new instance of fragment TimeTableDisplayFragment.
     */
    public static TimeTableDisplayFragment newInstance (String tableName, int tableType, int dayId, int groupId)
    {
        TimeTableDisplayFragment fragment = new TimeTableDisplayFragment ();
        Bundle args = new Bundle ();

        args.putString (ARG_TABLE_NAME, tableName);
        args.putInt (ARG_TABLE_TYPE, tableType);
        args.putInt (ARG_DAY_ID, dayId);
        args.putInt (ARG_GROUP_ID, groupId);

        fragment.setArguments (args);
        return fragment;
    }

    public TimeTableDisplayFragment ()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        if (getArguments () != null)
        {
            tableName = getArguments ().getString (ARG_TABLE_NAME);
            tableType = getArguments ().getInt (ARG_TABLE_TYPE);
            dayId = getArguments ().getInt (ARG_DAY_ID);
            groupId = getArguments ().getInt (ARG_GROUP_ID);
        }

        prefs = getActivity ().getSharedPreferences (getString (R.string.shared_prefs_name), Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate (R.layout.fragment_timetable_display, container, false);
        recyclerView = (RecyclerView) view.findViewById (R.id.lesson_list);

        syncButton = (Button) view.findViewById (R.id.button_sync);

        syncButton.setOnClickListener (this);

        noTimeTablesAlert = (TextView) view.findViewById (R.id.alert_no_timetables);

        if (prefs.getBoolean (getString (R.string.pref_timetables_synced), false))
        {
            recyclerView.setVisibility (View.VISIBLE);
            syncButton.setVisibility (View.INVISIBLE);
            noTimeTablesAlert.setVisibility (View.INVISIBLE);
        }
        else
        {
            syncButton.setVisibility (View.VISIBLE);
            noTimeTablesAlert.setVisibility (View.VISIBLE);
            recyclerView.setVisibility (View.INVISIBLE);
        }

        layoutManager = new LinearLayoutManager (activity);
        recyclerView.setLayoutManager (layoutManager);

        adapter = new LessonListAdapter (R.layout.lesson_item,
                KochanowskiMainActivity.getHelper ().getReadableDatabase (),
                tableName,
                tableType,
                dayId,
                groupId,
                this.getActivity ());

        recyclerView.setAdapter (adapter);

        return view;
    }

    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach (activity);
        this.activity = activity;
    }

    @Override
    public void onDetach ()
    {
        super.onDetach ();
    }

    public void setDay (int day)
    {
        this.dayId = day;
    }

    public void refresh ()
    {
        adapter = new LessonListAdapter (R.layout.lesson_item, KochanowskiMainActivity.getHelper ().getReadableDatabase (), tableName, tableType, dayId, groupId, this.getActivity ());
        recyclerView.setAdapter (adapter);
    }

    @Override
    public void onClick (View v)
    {
        switch (v.getId ())
        {
            case R.id.button_sync:
                Intent intent = new Intent (getActivity (), SyncActivity.class);
                startActivity (intent);
                break;
        }
    }
}
