package com.github.LiquidPL.kochanowski.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.github.LiquidPL.kochanowski.R;
import com.liquid.kochanparser.TimeTableType;

public class TimeTableListActivity
        extends BaseActivity
        implements TimeTableListFragment.OnTimeTableSelectedListener
{
    public static final String ARG_TYPE = "type";

    private int tableType;

    private TimeTableListFragment listFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_timetable_list);

        tableType = TimeTableType.CLASS;

        listFragment = TimeTableListFragment.newInstance (tableType);

        getSupportFragmentManager ().beginTransaction ().
                replace (R.id.fragment_stub, listFragment).commit ();
    }

    @Override
    protected int getSelfNavDrawerItem ()
    {
        return NAVDRAWER_ITEM_BROWSE_TIMETABLES;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_timetable_list, menu);

        // setting the toolbar title to the filter selected on activity start
        getToolbar ().setTitle (getString (R.string.classes));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId ();

        switch (id)
        {
            case R.id.filter_class:
                item.setChecked (true);
                listFragment.setFilter (TimeTableType.CLASS);
                getToolbar ().setTitle (getString (R.string.classes));
                return true;
            case R.id.filter_teachers:
                item.setChecked (true);
                listFragment.setFilter (TimeTableType.TEACHER);
                getToolbar ().setTitle (getString (R.string.teachers));
                return true;
            case R.id.filter_classrooms:
                item.setChecked (true);
                listFragment.setFilter (TimeTableType.CLASSROOM);
                getToolbar ().setTitle (getString (R.string.classrooms));
                return true;
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onTimeTableSelected (String shortName, String longName, int tableType)
    {
        final Intent intent = new Intent (this, TimeTableTabActivity.class);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_TYPE, tableType);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_NAME_SHORT, shortName);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_NAME_LONG, longName);

        handler.postDelayed (new Runnable ()
        {
            @Override
            public void run ()
            {
                startActivity (intent);
            }
        }, ACTIVITY_LAUNCH_DELAY);
    }
}
