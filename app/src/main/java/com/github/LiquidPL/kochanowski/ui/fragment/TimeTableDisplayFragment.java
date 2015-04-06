package com.github.LiquidPL.kochanowski.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.*;
import com.github.LiquidPL.kochanowski.parse.Type;
import com.github.LiquidPL.kochanowski.ui.SyncActivity;
import com.github.LiquidPL.kochanowski.util.DbUtils;
import com.github.LiquidPL.kochanowski.util.PrefUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableDisplayFragment extends Fragment implements View.OnClickListener
{
    public class Group
    {
        // group type constants
        public static final int GROUP_ONE = 1;
        public static final int GROUP_TWO = 2;
        public static final int GROUP_BOTH = 0;
    }

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TIME_TABLE_NAME = "timetablename";
    private static final String ARG_TABLE_TYPE = "tabletype";
    private static final String ARG_DAY_ID     = "dayid";
    private static final String ARG_GROUP_ID   = "groupid";

    // the given fragments parameters
    private String timetableName;
    private int tableType;
    private int dayId;
    private int groupId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button syncButton;
    private TextView noTimeTablesAlert;

    private SQLiteDatabase db;


    /**
     * An AsyncTask loading a single lesson item from the database
     * and putting it into the {@link LessonListAdapter.LessonViewHolder}.
     *
     * It is called by the {@link LessonListAdapter#onCreateViewHolder}
     * when a view needs to be filled with data from the database.
     */
    private class LoadItemFromDatabaseTask extends AsyncTask<Integer, Void, List<String>>
    {
        private Cursor cur;

        private LessonListAdapter.LessonViewHolder viewHolder;

        private LoadItemFromDatabaseTask (Cursor cur,
                                          LessonListAdapter.LessonViewHolder viewHolder)
        {
            this.cur = cur;
            this.viewHolder = viewHolder;
        }

        @Override
        protected List<String> doInBackground (Integer... params)
        {
            List<String> data = new ArrayList<> ();

            // moving the cursor to the required position
            cur.moveToPosition (params[0]);

            // getting the subject name
            data.add (cur.getString (cur.getColumnIndexOrThrow (SubjectTable.COLUMN_NAME_SUBJECT_NAME)));

            // getting the lesson start and end time
            int hourId = cur.getInt (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_HOUR_ID));
            String startTime = cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_START_TIME));
            String endTime = cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_END_TIME));

            // the time strings are compliant with ISO 8601 standard,
            // so they have leading zeroes if hour < 10. we remove them here
            if (startTime.startsWith ("0")) startTime = startTime.substring (1);
            if (endTime.startsWith ("0")) endTime = endTime.substring (1);

            data.add (hourId + ". " + startTime + "-" + endTime);

            switch (tableType)
            {
                case Type.CLASS:
                    // getting the teacher name, surname, and id
                    data.add (cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_NAME)) + " " +
                              cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_SURNAME)) +
                              " (" + cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_CODE)) + ")");
                    // getting the classroom name
                    data.add (cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM)));
                    break;
                case Type.CLASSROOM:
                    // getting the teacher name, surname, and id
                    data.add (cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_NAME)) + " " +
                                      cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_SURNAME)) +
                                      " (" + cur.getString (cur.getColumnIndexOrThrow (TeacherTable.COLUMN_NAME_TEACHER_CODE)) + ")");
                    // getting the class name
                    data.add (cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG)) + " (" +
                              cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT)) + ")");
                    break;
                case Type.TEACHER:
                    // getting the class name
                    data.add (cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG)) + " (" +
                                      cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT)) + ")");
                    // getting the classroom name
                    data.add (cur.getString (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_CLASSROOM)));
                    break;
            }

            // adding the string containing the group name, if required
            if (cur.getInt (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_GROUP_ID)) != 0)
            {
                data.add (getString (R.string.lesson_list_group) +
                          cur.getInt (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_GROUP_ID)));
            }

            return data;
        }

        @Override
        protected void onPostExecute (List<String> strings)
        {
            super.onPostExecute (strings);

            viewHolder.subjectName.setText (strings.get (0));
            viewHolder.hour.setText (strings.get (1));
            viewHolder.teacherName.setText (strings.get (2));
            viewHolder.classroomName.setText (strings.get (3));
            if (strings.size () == 5)
            {
                viewHolder.groupName.setText (strings.get (4));
            }
            else
            {
                viewHolder.groupName.setText ("");
            }
        }
    }

    private class LessonListAdapter extends RecyclerView.Adapter<LessonListAdapter.LessonViewHolder>
    {
        SQLiteDatabase db;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder ();

        private Cursor cur;
        private Cursor oldCur;

        private String timetableName;
        private int tableType;
        private int dayId;
        private int groupId;

        private String selection;
        private String[] selectionArgs;
        private String orderBy;


        public class LessonViewHolder extends RecyclerView.ViewHolder
        {
            public TextView subjectName;
            public TextView teacherName;
            public TextView classroomName;
            public TextView groupName;
            public TextView hour;

            public LessonViewHolder (View v)
            {
                super (v);

                subjectName = (TextView) v.findViewById (R.id.subject_name);
                teacherName = (TextView) v.findViewById (R.id.teacher_name);
                classroomName = (TextView) v.findViewById (R.id.classroom_name);
                groupName = (TextView) v.findViewById (R.id.group_name);
                hour = (TextView) v.findViewById (R.id.hour_label);
            }
        }

        public LessonListAdapter (SQLiteDatabase db, String timetableName, int tableType, int dayId, int groupId)
        {
            this.db = db;
            this.timetableName = timetableName;
            this.tableType = tableType;
            this.dayId = dayId;
            this.groupId = groupId;

            cur = formCursor ();
        }

        private Cursor formCursor ()
        {
            queryBuilder.setTables (LessonTable.TABLE_NAME +
                                    " INNER JOIN " + SubjectTable.TABLE_NAME +
                                        " ON " + LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_SUBJECT_ID +
                                        "=" + SubjectTable.TABLE_NAME + "." + SubjectTable._ID +
                                    " INNER JOIN " + ClassTable.TABLE_NAME +
                                        " ON " + LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_CLASS_NAME_SHORT +
                                        "=" + ClassTable.TABLE_NAME + "." + ClassTable.COLUMN_NAME_NAME_SHORT +
                                    " INNER JOIN " + TeacherTable.TABLE_NAME +
                                        " ON " + LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_TEACHER_CODE +
                                        "=" + TeacherTable.TABLE_NAME + "." + TeacherTable.COLUMN_NAME_TEACHER_CODE);

            selection = LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_DAY + "=?";
            selectionArgs = new String[] {Integer.toString (dayId), timetableName};

            if (groupId != 0)
            {
                selection += " AND (" + LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_GROUP_ID + "=? OR " +
                                        LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_GROUP_ID + "=?)";
                selectionArgs = new String[] {Integer.toString (dayId), Integer.toString (groupId), "0", timetableName};
            }

            switch (tableType)
            {
                case Type.CLASS:
                    selection += " AND " + LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_CLASS_NAME_SHORT + "=?";
                    break;
                case Type.TEACHER:
                    selection += " AND " + LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_TEACHER_CODE + "=?";
                    break;
                case Type.CLASSROOM:
                    selection += " AND " + LessonTable.TABLE_NAME + "." + LessonTable.COLUMN_NAME_CLASSROOM + "=?";
                    break;
            }

            orderBy = "datetime (" + LessonTable.COLUMN_NAME_START_TIME + ") ASC";

            return queryBuilder.query (db, null, selection, selectionArgs, null, null, orderBy);
        }

        @Override
        public void onBindViewHolder (LessonViewHolder holder, int position)
        {
            // simply calling the LoadItemFromDatabaseTask which handles
            // reading lesson data from the database and inserting it
            // into a viewholder
            new LoadItemFromDatabaseTask (cur, holder).execute (position);
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

        public void setGroup (int groupId)
        {
            int previousGroup = this.groupId;
            this.groupId = groupId;

            oldCur = cur;
            cur = formCursor ();

            int mod = 0;
            oldCur.moveToFirst ();
            if (groupId != 0) do
            {
                int group = oldCur.getInt (oldCur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_GROUP_ID));
                if (group != 0 && group != groupId)
                {
                    notifyItemRemoved (oldCur.getPosition () - mod++);
                }
                oldCur.moveToNext ();
            }
            while (!oldCur.isAfterLast ());

            cur.moveToFirst ();
            if (previousGroup != 0) do
            {
                int group = cur.getInt (cur.getColumnIndexOrThrow (LessonTable.COLUMN_NAME_GROUP_ID));
                if ((groupId == 0 && group != previousGroup && group != 0) || (groupId != 0 && group == groupId))
                {
                    notifyItemInserted (cur.getPosition ());
                }
                cur.moveToNext ();
            }
            while (!cur.isAfterLast ());
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
     * @param timetableName Name of the timetable, corresponding to a column in the database
     * @param tableType The timetable type to display (class/classroom)
     * @param groupId The group to be displayed
     *
     * @return A new instance of fragment TimeTableDisplayFragment.
     */
    public static TimeTableDisplayFragment newInstance (String timetableName, int tableType, int dayId, int groupId)
    {
        TimeTableDisplayFragment fragment = new TimeTableDisplayFragment ();
        Bundle args = new Bundle ();

        args.putString (ARG_TIME_TABLE_NAME, timetableName);
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
            timetableName = getArguments ().getString (ARG_TIME_TABLE_NAME);
            tableType = getArguments ().getInt (ARG_TABLE_TYPE);
            dayId = getArguments ().getInt (ARG_DAY_ID);
            groupId = getArguments ().getInt (ARG_GROUP_ID);
        }

        db = DbUtils.getInstance ().openDatabase ();
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

        resetSyncAlertVisibility ();

        layoutManager = new LinearLayoutManager (getActivity ());
        recyclerView.setLayoutManager (layoutManager);
        recyclerView.setItemAnimator (new DefaultItemAnimator ());

        adapter = new LessonListAdapter (
                db,
                timetableName,
                tableType,
                dayId,
                groupId);

        recyclerView.setAdapter (adapter);

        return view;
    }

    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach (activity);
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy ();

        DbUtils.getInstance ().closeDatabase ();
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

    private void resetSyncAlertVisibility ()
    {
        if (PrefUtils.hasSyncedTimeTables (getActivity ()))
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
    }

    public void refresh ()
    {
        resetSyncAlertVisibility ();

        adapter = new LessonListAdapter (
                db,
                timetableName,
                tableType,
                dayId,
                groupId);

        recyclerView.setAdapter (adapter);
    }

    public void setGroup (int groupId) // 1,2, or 0 (both groups)
    {
        if (groupId == this.groupId) return;
        this.groupId = groupId;

        ((LessonListAdapter) adapter).setGroup (groupId);
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

    public void setTimetableName (String table)
    {
        this.timetableName = table;
    }

    public String getTimetableName ()
    {
        return timetableName;
    }

    public int getTableType ()
    {
        return tableType;
    }

    public int getDayId ()
    {
        return dayId;
    }

    public int getGroupId ()
    {
        return groupId;
    }
}
