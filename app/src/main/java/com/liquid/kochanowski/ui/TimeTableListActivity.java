package com.liquid.kochanowski.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.liquid.kochanowski.R;
import com.liquid.kochanparser.TimeTableType;

public class TimeTableListActivity
        extends BaseActivity
        implements TimeTableListFragment.OnTimeTableSelectedListener
{
    public static final String ARG_TYPE = "type";

    private int listType;
    private int tableType;

    private TimeTableListFragment listFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_timetable_list);

        listType = getIntent ().getIntExtra (ARG_TYPE, NAVDRAWER_ITEM_INVALID);

        switch (listType)
        {
            case NAVDRAWER_ITEM_CLASSES:
                tableType = TimeTableType.CLASS;

                getSupportActionBar ().setTitle (getString (R.string.classes));
                break;
            case NAVDRAWER_ITEM_TEACHERS:
                tableType = TimeTableType.TEACHER;

                getSupportActionBar ().setTitle (getString (R.string.teachers));
                break;
            case NAVDRAWER_ITEM_CLASSROOMS:
                tableType = TimeTableType.CLASSROOM;

                getSupportActionBar ().setTitle (getString (R.string.classrooms));
                break;
        }

        listFragment = TimeTableListFragment.newInstance (tableType);

        getSupportFragmentManager ().beginTransaction ().
                replace (R.id.fragment_stub, listFragment).commit ();
    }

    @Override
    protected int getSelfNavDrawerItem ()
    {
        return listType;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_timetable_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

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
