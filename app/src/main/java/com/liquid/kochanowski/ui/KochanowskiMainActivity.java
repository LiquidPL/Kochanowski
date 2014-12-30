package com.liquid.kochanowski.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.liquid.kochanowski.R;
import com.liquid.kochanowski.util.DbUtils;
import com.liquid.kochanowski.util.PrefUtils;
import com.liquid.kochanowski.ui.TimeTableDisplayFragment.Group;
import com.liquid.kochanparser.TimeTableType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class KochanowskiMainActivity
        extends BaseActivity
        implements TimeTableListFragment.OnTimeTableSelectedListener,
                   AdapterView.OnItemSelectedListener,
                   SharedPreferences.OnSharedPreferenceChangeListener
{
    private SharedPreferences prefs;

    private Menu menu;
    private Spinner spinner;

    private TimeTableDisplayFragment displayFragment;
    @SuppressWarnings("FieldCanBeLocal")

    static final String ARG_DAY = "day";
    static final String ARG_GROUP = "group";

    private String currentTable = "";
    private int currentDay = -1;
    private int currentGroup = -1;

    private class DaySelectAdapter extends ArrayAdapter<DaySelectAdapter.DayDate>
    {
        public class DayDate
        {
            int id;

            String day;
            String date;

            public DayDate (int id, String day, String date)
            {
                this.id = id;
                this.day = day;
                this.date = date;
            }
        }

        private List<DayDate> days = new ArrayList<> ();
        private int resource;

        public DaySelectAdapter (Context context, int resource)
        {
            super (context, resource);

            this.resource = resource;
            this.days = getDays (context);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent)
        {
            return getCustomView (resource, position, parent);
        }

        @Override
        public View getDropDownView (int position, View convertView, ViewGroup parent)
        {
            return getCustomView (R.layout.spinner_item_dropdown, position, parent);
        }

        private View getCustomView (int resource, int position, ViewGroup parent)
        {
            View view = LayoutInflater.from (parent.getContext ()).inflate (resource, parent, false);

            TextView dayName = (TextView) view.findViewById (R.id.day_name);
            TextView date = (TextView) view.findViewById (R.id.date);

            dayName.setText (days.get (position).day);
            date.setText (days.get (position).date);

            return view;
        }

        @Override
        public int getCount ()
        {
            return days.size ();
        }

        private List <DayDate> getDays (Context context)
        {
            List <DayDate> days = new ArrayList<> ();

            Calendar cal = Calendar.getInstance ();
            int today = cal.get (Calendar.DAY_OF_WEEK);

            SimpleDateFormat format = new SimpleDateFormat ("dd MMMM");

            int diff = -cal.get (Calendar.DAY_OF_WEEK) + 2;
            cal.add (Calendar.DAY_OF_MONTH, diff);
            for (int i = 0; i < 5; i++)
            {
                if (cal.get (Calendar.DAY_OF_WEEK) == today)
                {
                    days.add (new DayDate (i, context.getResources ().getString (R.string.day_name_today), format.format (cal.getTime ())));
                    cal.add (Calendar.DAY_OF_MONTH, 1);
                    continue;
                }

                String day = "";

                switch (cal.get (Calendar.DAY_OF_WEEK))
                {
                    case 2:
                        day = context.getResources ().getString (R.string.day_name_0);
                        break;
                    case 3:
                        day = context.getResources ().getString (R.string.day_name_1);
                        break;
                    case 4:
                        day = context.getResources ().getString (R.string.day_name_2);
                        break;
                    case 5:
                        day = context.getResources ().getString (R.string.day_name_3);
                        break;
                    case 6:
                        day = context.getResources ().getString (R.string.day_name_4);
                        break;
                }

                days.add (new DayDate (i, day, format.format (cal.getTime ())));
                cal.add (Calendar.DAY_OF_MONTH, 1);
            }

            return days;
        }
    }

    public KochanowskiMainActivity ()
    {
        DbUtils.initHelper (this);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_kochanowski_main);

        spinner = (Spinner) findViewById (R.id.main_activity_spinner);

        currentTable = PrefUtils.getTableName (this);
        currentDay = getCurrentDay ();
        currentGroup = PrefUtils.getDefaultGroup (this);

        if (savedInstanceState != null)
        {
            currentDay = savedInstanceState.getInt (ARG_DAY);
            currentGroup = savedInstanceState.getInt (ARG_GROUP);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (this);
        prefs.registerOnSharedPreferenceChangeListener (this);
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        if (displayFragment == null)
        {
            displayFragment = TimeTableDisplayFragment.newInstance (currentTable,
                                                                    TimeTableType.CLASS, currentDay,
                                                                    currentGroup);
            getSupportFragmentManager ().beginTransaction ().
                    replace (R.id.fragment_stub, displayFragment).commit ();

            spinner.setAdapter (new DaySelectAdapter (this, R.layout.spinner_item));
            spinner.setOnItemSelectedListener (this);
            spinner.setSelection (currentDay);
        }

        if (PrefUtils.hasSyncedTimeTables (this))
        {
            getSupportActionBar ().setDisplayShowTitleEnabled (false);
            spinner.setVisibility (View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putInt (ARG_DAY, currentDay);
        outState.putInt (ARG_GROUP, currentGroup);

        super.onSaveInstanceState (outState);
    }

    @Override
    protected void onDestroy ()
    {
        super.onDestroy ();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (this);
        prefs.unregisterOnSharedPreferenceChangeListener (this);
    }

    @Override
    protected int getSelfNavDrawerItem ()
    {
        return NAVDRAWER_ITEM_TODAY;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_kochanowski_main, menu);
        this.menu = menu;

        setUpGroupSwitcher ();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        if (id == R.id.group_1 || id == R.id.group_2)
        {
            handleGroupSwitch (id);
        }

        return super.onOptionsItemSelected (item);
    }

    private void handleGroupSwitch (int itemId)
    {
        MenuItem item = menu.findItem (itemId);

        switch (itemId)
        {
            case R.id.group_1:
                if (currentGroup == Group.GROUP_BOTH) currentGroup = Group.GROUP_TWO;
                else if (currentGroup == Group.GROUP_ONE) currentGroup = Group.GROUP_TWO;
                else if (currentGroup == Group.GROUP_TWO) currentGroup = Group.GROUP_BOTH;

                break;
            case R.id.group_2:
                if (currentGroup == Group.GROUP_BOTH) currentGroup = Group.GROUP_ONE;
                else if (currentGroup == Group.GROUP_ONE) currentGroup = Group.GROUP_BOTH;
                else if (currentGroup == Group.GROUP_TWO) currentGroup = Group.GROUP_ONE;

                break;
        }
        displayFragment.setGroup (currentGroup);

        setUpGroupSwitcher ();
    }

    private void setMenuItemChecked (int itemId, boolean checked)
    {
        MenuItem item = menu.findItem (itemId);

        if (checked)
        {
            item.setChecked (true);

            if (itemId == R.id.group_1) item.setIcon (R.drawable.ic_group_1_white);
            if (itemId == R.id.group_2) item.setIcon (R.drawable.ic_group_2_white);
        }
        else
        {
            item.setChecked (false);

            if (itemId == R.id.group_1) item.setIcon (R.drawable.ic_group_1_white_disabled);
            if (itemId == R.id.group_2) item.setIcon (R.drawable.ic_group_2_white_disabled);
        }
    }

    private void setUpGroupSwitcher ()
    {
        if (menu != null) switch (currentGroup)
        {
            case Group.GROUP_BOTH:
                setMenuItemChecked (R.id.group_1, true);
                setMenuItemChecked (R.id.group_2, true);
                break;
            case Group.GROUP_ONE:
                setMenuItemChecked (R.id.group_1, true);
                setMenuItemChecked (R.id.group_2, false);
                break;
            case Group.GROUP_TWO:
                setMenuItemChecked (R.id.group_1, false);
                setMenuItemChecked (R.id.group_2, true);
                break;
        }
    }

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
    {
        currentDay = position;
        displayFragment.setDay (currentDay);
        displayFragment.refresh ();
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent)
    {

    }

    @Override
    public void onTimeTableSelected (String shortName, String longName, int tableType)
    {

    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key)
    {
        if (key.equals (PrefUtils.PREF_DEFAULT_GROUP))
        {
            currentGroup = PrefUtils.getDefaultGroup (this);
            setUpGroupSwitcher ();
        }

        if (key.equals (PrefUtils.PREF_TABLE_NAME))
        {
            currentTable = PrefUtils.getTableName (this);
            displayFragment.setTableName (currentTable);
            displayFragment.refresh ();
        }
    }

    static public int getCurrentDay ()
    {
        int day = Calendar.getInstance ().get (Calendar.DAY_OF_WEEK) - 2;
        if (day > 4 || day < 0) day = 0;
        return day;
    }
}