package com.github.LiquidPL.kochanowski.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.parse.Type;
import com.github.LiquidPL.kochanowski.ui.fragment.TimeTableDisplayFragment;
import com.github.LiquidPL.kochanowski.ui.fragment.TimeTableDisplayFragment.Group;
import com.github.LiquidPL.kochanowski.util.PrefUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity
    extends BaseActivity
    implements AdapterView.OnItemSelectedListener,
               SharedPreferences.OnSharedPreferenceChangeListener
{
    // widget handles //

    private Menu menu;

    // spinner for selecting days in a week
    private Spinner spinner;

    // an instance of DaySelectAdapter
    DaySelectAdapter daySelectAdapter;

    // handle to a fragment displaying timetables
    private TimeTableDisplayFragment displayFragment;

    // arguments for handling state persistence //
    public static final String ARG_DAY = "day";     // current day
    public static final String ARG_GROUP = "group"; // current group

    // variables containing data about the currently displayed timetable
    // this should persist activity reconstruction
    private String currentTableName = "";
    private int currentDay = -1;
    private int currentGroup = -1;

    private class GetDateHolderListTask
        extends AsyncTask<Void, Void, Void>
    {
        private Context context;

        private GetDateHolderListTask (Context context)
        {
            this.context = context;
        }

        @Override
        protected Void doInBackground (Void... params)
        {


            return null;
        }

        @Override
        protected void onPostExecute (Void aVoid)
        {
            super.onPostExecute (aVoid);
        }
    }

    /**
     * An adapter providing day selection to the spinner in MainActivity.
     * It displays all days from Monday to Friday in current week,
     * while marking one of them as 'Today'.
     */
    private class DaySelectAdapter extends BaseAdapter
    {
        private List<DateHolder> dates = new ArrayList<> ();

        public class DateHolder
        {
            // 'worded' day name (eg. Monday, Tuesday)
            String dayName;
            // self-explanatory (eg. April 10)
            String date;

            public DateHolder (String dayName, String date)
            {
                this.dayName = dayName;
                this.date = date;
            }
        }

        public DaySelectAdapter (Context context)
        {
            dates = getDates (context);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent)
        {
            return getCustomView (position, R.layout.spinner_item, parent);
        }

        @Override
        public View getDropDownView (int position, View convertView, ViewGroup parent)
        {
            return getCustomView (position, R.layout.spinner_item_dropdown, parent);
        }

        private View getCustomView (int position, int resource, ViewGroup parent)
        {
            // inflating the view
            View view = LayoutInflater.from (parent.getContext ()).inflate (resource, parent, false);

            // getting the widgets
            TextView dayName = (TextView) view.findViewById (R.id.day_name);
            TextView date = (TextView) view.findViewById (R.id.date);

            // setting the labels
            dayName.setText (dates.get (position).dayName);
            date.setText (dates.get (position).date);

            return view;
        }

        @Override
        public DateHolder getItem (int position)
        {
            return dates.get (position);
        }

        @Override
        public long getItemId (int position)
        {
            return position;
        }

        @Override
        public int getCount ()
        {
            return dates.size ();
        }

        private List<DateHolder> getDates (Context context)
        {
            List<DateHolder> dates = new ArrayList<> ();

            // getting a calendar instance
            Calendar cal = Calendar.getInstance ();

            // storing the current day of week so we can mark it later
            int today = cal.get (Calendar.DAY_OF_WEEK);

            // how many days we have to subtract to get to the beginning of the week
            int diff = -cal.get (Calendar.DAY_OF_WEEK) + cal.getFirstDayOfWeek ();
            cal.add (Calendar.DAY_OF_MONTH, diff);

            SimpleDateFormat format = new SimpleDateFormat (getString (R.string.date_format_month_worded));

            // main loop responsible for getting dates
            for (int i = 0; i < 6; i++)
            {
                if (cal.get (Calendar.DAY_OF_WEEK) == today
                        && today >= Calendar.MONDAY
                        && today <= Calendar.FRIDAY)
                {
                    dates.add (new DateHolder (
                            getString (R.string.day_name_today),
                            format.format (cal.getTime ())
                    ));

                    cal.add (Calendar.DAY_OF_MONTH, 1);
                    continue;
                }

                String dayName = "";

                switch (cal.get (Calendar.DAY_OF_WEEK))
                {
                    case Calendar.MONDAY:
                        dayName = getString (R.string.day_name_monday);
                        break;
                    case Calendar.TUESDAY:
                        dayName = getString (R.string.day_name_tuesday);
                        break;
                    case Calendar.WEDNESDAY:
                        dayName = getString (R.string.day_name_wednesday);
                        break;
                    case Calendar.THURSDAY:
                        dayName = getString (R.string.day_name_thursday);
                        break;
                    case Calendar.FRIDAY:
                        dayName = getString (R.string.day_name_friday);
                        break;
                }

                if (cal.get (Calendar.DAY_OF_WEEK) >= Calendar.MONDAY
                        && cal.get (Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY)
                {
                    dates.add (new DateHolder (
                            dayName,
                            format.format (cal.getTime ())
                    ));
                }

                cal.add (Calendar.DAY_OF_MONTH, 1);
            }

            return dates;
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        // setting the layout view
        setContentView (R.layout.activity_main);

        // get handles to widgets
        spinner = (Spinner) findViewById (R.id.main_activity_spinner);

        // getting needed preferences
        currentTableName = PrefUtils.getDefaultTableName (this);
        currentGroup = PrefUtils.getDefaultGroup (this);
        currentDay = Calendar.getInstance ().get (Calendar.DAY_OF_WEEK) - Calendar.MONDAY; // Calendar.MONDAY == 2

        // setting the current day to monday if it it outside of monday-friday
        if (currentDay > 4 || currentDay < 0) currentDay = 0;

        // getting data from savedInstanceState, if exists
        if (savedInstanceState != null)
        {
            currentDay = savedInstanceState.getInt (ARG_DAY);
            currentGroup = savedInstanceState.getInt (ARG_GROUP);
        }

        // registering the OnSharedPreferenceChangeListener
        // in case of change of the default class
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (this);
        prefs.registerOnSharedPreferenceChangeListener (this);
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        // creating the fragment, if there is need to
        if (displayFragment == null)
        {
            displayFragment = TimeTableDisplayFragment.newInstance (currentTableName,
                                                                    Type.CLASS,
                                                                    currentDay,
                                                                    currentGroup);

            // placing the fragment on the interface
            getSupportFragmentManager ().beginTransaction ().
                    replace (R.id.fragment_stub, displayFragment).commit ();

            // setting up the day select spinner
            daySelectAdapter = new DaySelectAdapter (this);
            spinner.setAdapter (daySelectAdapter);
            spinner.setOnItemSelectedListener (this);

            // setting spinners selection to current day
            spinner.setSelection (currentDay);
        }

        // hiding and displaying certain views depending on
        // if user have any timetables on the device
        if (PrefUtils.hasSyncedTimeTables (this))
        {
            getSupportActionBar ().setDisplayShowTitleEnabled (false);
            spinner.setVisibility (View.VISIBLE);
        }
        else
        {
            getSupportActionBar ().setDisplayShowTitleEnabled (true);
            spinner.setVisibility (View.GONE);
        }

        initGroupSwitcher ();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putInt (ARG_DAY, currentDay);
        outState.putInt (ARG_GROUP, currentGroup);

        // always call the super method at the end here,
        // as it handles the state saving itself
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
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_main, menu);
        this.menu = menu;

        // initializing the group switcher
        initGroupSwitcher ();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        int id = item.getItemId ();

        // changing the current group depending on the menu item selected
        switch (id)
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

        if (id == R.id.group_1 || id == R.id.group_2)
        {
            // changing the actual group displayed
            displayFragment.setGroup (currentGroup);

            // refreshing the group switcher state
            refreshGroupSwitcher ();
        }

        return super.onOptionsItemSelected (item);
    }

    /**
     * This method initializes the group switcher:
     * It gets the current selected group, and sets the menu items
     * responsible for switching groups to their appropriate states.
     */
    private void refreshGroupSwitcher ()
    {
        if (menu == null) return;

        switch (currentGroup)
        {
            case Group.GROUP_ONE:
                setGroupChecked (R.id.group_1, true);
                setGroupChecked (R.id.group_2, false);
                break;
            case Group.GROUP_TWO:
                setGroupChecked (R.id.group_1, false);
                setGroupChecked (R.id.group_2, true);
                break;
            case Group.GROUP_BOTH:
                setGroupChecked (R.id.group_1, true);
                setGroupChecked (R.id.group_2, true);
                break;
        }
    }

    private void initGroupSwitcher ()
    {
        refreshGroupSwitcher ();

        if (menu == null) return;

        // checking whether we have already sychronized timetables
        // and showing/hiding the group switcher
        if (PrefUtils.hasSyncedTimeTables (this))
        {
            menu.setGroupVisible (R.id.group_switch, true);
        }
        else
        {
            menu.setGroupVisible (R.id.group_switch, false);
        }
    }

    /**
     * This method changes a group menu item to a specified state.
     * @param id Identifier of the menu item.
     * @param checked State to which we want to set the item.
     */
    private void setGroupChecked (int id, boolean checked)
    {
        // getting the group menu item, so we can change it's checked status
        MenuItem item = menu.findItem (id);
        item.setChecked (checked);

        // changing the icons of the menu items, to indicate checked status
        if (checked)
        {
            if (id == R.id.group_1) item.setIcon (R.drawable.ic_group_1_white);
            if (id == R.id.group_2) item.setIcon (R.drawable.ic_group_2_white);
        }
        else
        {
            if (id == R.id.group_1) item.setIcon (R.drawable.ic_group_1_white_disabled);
            if (id == R.id.group_2) item.setIcon (R.drawable.ic_group_2_white_disabled);
        }
    }

    /**
     * Indicates that this activity handles the Today screen.
     */
    @Override
    protected int getSelfNavDrawerItem ()
    {
        return NAVDRAWER_ITEM_TODAY;
    }

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
    {
        currentDay = position;

        if (displayFragment != null)
        {
            displayFragment.setDay (currentDay);
            displayFragment.refresh ();
        }
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent)
    {

    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key)
    {
        // default group changed
        if (key.equals (PrefUtils.PREF_DEFAULT_GROUP))
        {
            currentGroup = PrefUtils.getDefaultGroup (this);
            displayFragment.setGroup (currentGroup);
            refreshGroupSwitcher ();
        }

        // timetable synchronization status has changed,
        // this is used to initialize the fragment
        // afted finishing synchronization and returning
        // to the activity
        if (key.equals (PrefUtils.PREF_TABLES_SYNCED))
        {
            currentTableName = PrefUtils.getDefaultTableName (this);

            displayFragment.setTableName (currentTableName);
            displayFragment.refresh ();
        }

        // the default timetable has changed
        if (key.equals (PrefUtils.PREF_DEFAULT_TABLE_NAME))
        {
            currentTableName = PrefUtils.getDefaultTableName (this);

            displayFragment.setTableName (currentTableName);
            displayFragment.refresh ();
        }
    }
}
