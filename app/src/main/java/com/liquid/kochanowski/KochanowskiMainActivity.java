package com.liquid.kochanowski;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.liquid.kochanowski.db.DatabaseHelper;
import com.liquid.kochanowski.widget.ScrimInsetsFrameLayout;
import com.liquid.kochanparser.TimeTableType;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class KochanowskiMainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, TimeTableListFragment.OnTimeTableSelectedListener
{
    private SharedPreferences prefs;

    private Toolbar toolbar;
    private Spinner spinner;

    private DrawerLayout drawerLayout;

    private ScrimInsetsFrameLayout insetLayout;
    private RecyclerView drawerList;
    private RecyclerView.LayoutManager drawerLayoutManager;
    private NavDrawerAdapter drawerAdapter;
    private ItemClickSupport clickSupport;

    private ActionBarDrawerToggle toggle;

    private TimeTableDisplayFragment displayFragment;
    private TimeTableListFragment listFragment;

    static final int SCREEN_TODAY = 0;
    static final int SCREEN_CLASSES = 1;
    static final int SCREEN_TEACHERS = 2;
    static final int SCREEN_CLASSROOMS = 3;
    static final int SCREEN_DISPLAY = 4;

    static final String ARG_SCREEN = "screen";
    static final String ARG_TABLE = "table";
    static final String ARG_DAY = "day";
    static final String ARG_TYPE = "type";

    private int currentScreen = -1;
    private String currentTable = "";
    private int currentDay = -1;
    private int currentType = -1;

    private List <String> values;
    private TypedArray icons;

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

        private Context context;

        public DaySelectAdapter (Context context, int resource)
        {
            super (context, resource);

            this.context = context;
            this.resource = resource;
            this.days = getDays (context);
        }

        @Override
        public View getView (int position, View convertView, ViewGroup parent)
        {
            return getCustomView (resource, position, convertView, parent);
        }

        @Override
        public View getDropDownView (int position, View convertView, ViewGroup parent)
        {
            return getCustomView (R.layout.spinner_item_dropdown, position, convertView, parent);
        }

        private View getCustomView (int resource, int position, View convertView, ViewGroup parent)
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
        DatabaseHelper.initHelper (this);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_kochanowski_main);

        values = Arrays.asList (getResources ().getStringArray (R.array.drawer_names));
        icons = getResources ().obtainTypedArray (R.array.drawer_icons);

        toolbar = (Toolbar) findViewById (R.id.toolbar);
        spinner = (Spinner) findViewById (R.id.main_activity_spinner);

        drawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout);

        insetLayout = (ScrimInsetsFrameLayout) findViewById (R.id.inset_layout);
        drawerList = (RecyclerView) findViewById (R.id.left_drawer);
        drawerLayoutManager = new ListLayoutManager (this, TwoWayLayoutManager.Orientation.VERTICAL);
        drawerAdapter = new NavDrawerAdapter (R.layout.drawer_list_item, values, icons, this);
        clickSupport = ItemClickSupport.addTo (drawerList);

        toggle = new ActionBarDrawerToggle (
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        prefs = getSharedPreferences (getString (R.string.shared_prefs_name), MODE_PRIVATE);

        displayFragment = TimeTableDisplayFragment.newInstance (
                prefs.getString (getString (R.string.pref_table_name), ""),
                TimeTableType.CLASS,
                getCurrentDay (),
                0
        );

        Intent parentIntent = getIntent ();

        currentScreen = SCREEN_TODAY;
        currentTable = prefs.getString (getString (R.string.pref_table_name), "");
        currentDay = getCurrentDay ();
        currentType = TimeTableType.CLASS;

        if (savedInstanceState != null)
        {
            currentScreen = savedInstanceState.getInt (ARG_SCREEN);
            currentTable = savedInstanceState.getString (ARG_TABLE);
            currentDay = savedInstanceState.getInt (ARG_DAY);
            currentType = savedInstanceState.getInt (ARG_TYPE);
        }
        else
        {
            currentScreen = parentIntent.getIntExtra (ARG_SCREEN, SCREEN_TODAY);
            currentTable = parentIntent.getStringExtra (ARG_TABLE);
            if (currentTable == null)
                currentTable = prefs.getString (getString (R.string.pref_table_name), "");
            currentDay = parentIntent.getIntExtra (ARG_DAY, getCurrentDay ());
            currentType = parentIntent.getIntExtra (ARG_TYPE, TimeTableType.CLASS);
        }
    }

    protected void onPostCreate (Bundle savedInstanceState)
    {
        super.onPostCreate (savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState ();
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        if (toolbar != null)
        {
            setSupportActionBar (toolbar);
            toolbar.setNavigationIcon (R.drawable.ic_menu_white);
            toolbar.setTitleTextColor (getResources ().getColor (R.color.white_100));

            drawerLayout.setStatusBarBackgroundColor (getResources ().getColor (R.color.primary_dark));
            drawerLayout.setDrawerListener (toggle);

            drawerList.setLayoutManager (drawerLayoutManager);
            drawerList.setAdapter (drawerAdapter);

            clickSupport.setOnItemClickListener (new ItemClickSupport.OnItemClickListener ()
            {
                @Override
                public void onItemClick (RecyclerView recyclerView, View view, int position, long id)
                {
                    switch (position)
                    {
                        case SCREEN_TODAY:
                            selectScreen (SCREEN_TODAY, prefs.getString (getString (R.string.pref_table_name), ""), getCurrentDay (), TimeTableType.CLASS);
                            break;
                        case SCREEN_CLASSES:
                            selectScreen (SCREEN_CLASSES, "", -1, -1);
                            break;
                        case SCREEN_TEACHERS:
                            selectScreen (SCREEN_TEACHERS, "", -1, -1);
                            break;
                        case SCREEN_CLASSROOMS:
                            selectScreen (SCREEN_CLASSROOMS, "", -1, -1);
                            break;
                    }
                }
            });
        }

        DaySelectAdapter adapter = new DaySelectAdapter (this, R.layout.spinner_item);
        spinner.setAdapter (adapter);
        spinner.setOnItemSelectedListener (this);

        selectScreen (currentScreen, currentTable, currentDay, currentType);
    }

    @Override
    protected void onResumeFragments ()
    {
        super.onResumeFragments ();

        // pulling the default table name saved in shared preferences in case we just got back from a sync
        currentTable = prefs.getString (getString (R.string.pref_table_name), "");

        // reinitializing the fragment when returning to the activity
        selectScreen (currentScreen, currentTable, currentDay, currentType);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        outState.putInt (ARG_SCREEN, currentScreen);
        outState.putString (ARG_TABLE, currentTable);
        outState.putInt (ARG_DAY, currentDay);
        outState.putInt (ARG_TYPE, currentType);

        super.onSaveInstanceState (outState);
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged (newConfig);

        toggle.onConfigurationChanged (newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_kochanowski_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        if (toggle.onOptionsItemSelected (item))
        {
            return true;
        }

        switch (id)
        {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected (item);
    }

    private void selectScreen (int screenId, String table, int day, int type)
    {
        currentScreen = screenId;
        currentTable = table;
        currentDay = day;
        currentType = type;

        FragmentTransaction transaction = getSupportFragmentManager ().beginTransaction ();

        switch (currentScreen)
        {
            case SCREEN_TODAY:
                displayFragment = TimeTableDisplayFragment.newInstance (currentTable, currentType, currentDay, 0);
                transaction.replace (R.id.fragment_stub, displayFragment);

                getSupportActionBar ().setDisplayShowTitleEnabled (false);
                spinner.setVisibility (View.VISIBLE);
                spinner.setSelection (currentDay);
                break;
            case SCREEN_CLASSES:
                listFragment = TimeTableListFragment.newInstance (TimeTableType.CLASS);
                transaction.replace (R.id.fragment_stub, listFragment);

                getSupportActionBar ().setDisplayShowTitleEnabled (true);
                getSupportActionBar ().setTitle (getString (R.string.classes));
                spinner.setVisibility (View.GONE);
                break;
            case SCREEN_TEACHERS:
                listFragment = TimeTableListFragment.newInstance (TimeTableType.TEACHER);
                transaction.replace (R.id.fragment_stub, listFragment);

                getSupportActionBar ().setDisplayShowTitleEnabled (true);
                getSupportActionBar ().setTitle (getString (R.string.teachers));
                spinner.setVisibility (View.GONE);
                break;
            case SCREEN_CLASSROOMS:
                listFragment = TimeTableListFragment.newInstance (TimeTableType.CLASSROOM);
                transaction.replace (R.id.fragment_stub, listFragment);

                getSupportActionBar ().setDisplayShowTitleEnabled (true);
                getSupportActionBar ().setTitle (getString (R.string.classrooms));
                spinner.setVisibility (View.GONE);
                break;
            case SCREEN_DISPLAY:
                break;
        }
        transaction.commit ();

        drawerLayout.closeDrawer (insetLayout);
    }

    static public int getCurrentDay ()
    {
        int day = Calendar.getInstance ().get (Calendar.DAY_OF_WEEK) - 2;
        if (day > 4 || day < 0) day = 0;
        return day;
    }

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
    {
        if (currentScreen == SCREEN_TODAY)
        {
            currentDay = position;
            displayFragment.setDay (position);
            displayFragment.refresh ();
        }
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent)
    {

    }

    @Override
    public void onTimeTableSelected (String shortName, String longName, int tableType)
    {
        Intent intent = new Intent (this, TimeTableTabActivity.class);

        intent.putExtra (TimeTableTabActivity.ARG_TABLE_NAME_SHORT, shortName);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_NAME_LONG, longName);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_TYPE, tableType);

        startActivity (intent);
    }
}