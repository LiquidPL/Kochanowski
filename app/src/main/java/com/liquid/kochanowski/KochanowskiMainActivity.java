package com.liquid.kochanowski;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.liquid.kochanparser.TimeTableType;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class KochanowskiMainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, TimeTableListFragment.OnTimeTableSelectedListener
{
    private static TimeTableDbHelper helper;

    private SharedPreferences prefs;

    private Toolbar toolbar;
    private Spinner spinner;

    private DrawerLayout drawerLayout;

    private RecyclerView drawerList;
    private RecyclerView.LayoutManager drawerLayoutManager;
    private NavDrawerAdapter drawerAdapter;
    private ItemClickSupport clickSupport;

    private ActionBarDrawerToggle toggle;

    private TimeTableDisplayFragment fragment;

    private static final int SCREEN_TODAY = 0;
    private static final int SCREEN_CLASSES = 1;
    private static final int SCREEN_TEACHERS = 2;
    private static final int SCREEN_CLASSROOMS = 3;
    private static final int SCREEN_DISPLAY = 4;

    private List <String> values = new ArrayList <> ();
    private List <Integer> icons = new ArrayList <> ();

    private class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.DrawerViewHolder>
    {
        private List <String> values;
        private List <Integer> icons;

        private int resource;

        public class DrawerViewHolder extends RecyclerView.ViewHolder
        {
            TextView name;
            ImageView icon;

            public DrawerViewHolder (View v)
            {
                super (v);

                name = (TextView) v.findViewById (R.id.text);
                icon = (ImageView) v.findViewById (R.id.item_icon);
            }
        }

        public NavDrawerAdapter (int resource, List <String> values, List<Integer> icons)
        {
            this.resource = resource;
            this.values = values;
            this.icons = icons;
        }

        @Override
        public DrawerViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
        {
            View v = LayoutInflater.from (parent.getContext ()).inflate (resource, parent, false);

            return new DrawerViewHolder (v);
        }

        @Override
        public void onBindViewHolder (DrawerViewHolder holder, int position)
        {
            holder.name.setText (values.get (position));
            holder.icon.setImageDrawable (getResources ().getDrawable (icons.get (position)));
        }

        @Override
        public int getItemCount ()
        {
            return values.size ();
        }
    }

    public KochanowskiMainActivity ()
    {
        helper = new TimeTableDbHelper (this);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_kochanowski_main);

        values.add (getString (R.string.day_name_today));
        values.add (getString (R.string.classes));
        values.add (getString (R.string.teachers));
        values.add (getString (R.string.classrooms));

        icons.add (R.drawable.ic_person_black);
        icons.add (R.drawable.ic_group_black);
        icons.add (R.drawable.ic_person_black);
        icons.add (R.drawable.ic_person_black);

        toolbar = (Toolbar) findViewById (R.id.activity_main_toolbar);
        spinner = (Spinner) findViewById (R.id.main_activity_spinner);

        drawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout);

        drawerList = (RecyclerView) findViewById (R.id.left_drawer);
        drawerLayoutManager = new ListLayoutManager (this, TwoWayLayoutManager.Orientation.VERTICAL);
        drawerAdapter = new NavDrawerAdapter (R.layout.drawer_list_item, values, icons);
        clickSupport = ItemClickSupport.addTo (drawerList);

        toggle = new ActionBarDrawerToggle (
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        prefs = getSharedPreferences (getString (R.string.shared_prefs_name), MODE_PRIVATE);
    }

    protected void onPostCreate (Bundle savedInstanceState)
    {
        super.onPostCreate (savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState ();
    }

    @Override
    protected void onStart ()
    {
        super.onStart ();

        if (toolbar != null)
        {
            setSupportActionBar (toolbar);
            toolbar.setNavigationIcon (R.drawable.ic_menu_black);
            getSupportActionBar ().setDisplayShowTitleEnabled (false);

            drawerLayout.setDrawerListener (toggle);
            drawerLayout.setStatusBarBackgroundColor (getResources ().getColor (android.R.color.darker_gray));

            drawerList.setLayoutManager (drawerLayoutManager);
            drawerList.setAdapter (drawerAdapter);
            setUpClickListeners ();
        }

        DaySelectAdapter adapter = new DaySelectAdapter (this, R.layout.spinner_item, DaySelectAdapter.getDays (this));
        spinner.setAdapter (adapter);
        spinner.setOnItemSelectedListener (this);
        spinner.setSelection (Calendar.getInstance ().get (Calendar.DAY_OF_WEEK) - 2);
        spinner.setVisibility (View.VISIBLE);

        fragment = TimeTableDisplayFragment.newInstance (
                prefs.getString (getString (R.string.pref_table_name), ""),
                TimeTableType.CLASS,
                1);

        FragmentTransaction transaction = getFragmentManager ().beginTransaction ();

        transaction.replace (R.id.fragmentStub, fragment);
        transaction.commit ();

        Log.i ("liquid", "onStart ()");
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        Log.i ("liquid", "onResume ()");
    }

    @Override
    protected void onSaveInstanceState (Bundle outState)
    {

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

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
    {
        Log.i ("liquid", "Item selected: " + position);

        fragment.setDay (position);
        fragment.refresh ();
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent)
    {

    }

    public static TimeTableDbHelper getHelper ()
    {
        return helper;
    }

    @Override
    public void onTimeTableSelected (String tableName, int tableType)
    {
        Log.i ("liquid", "Selected " + tableName);

        /*TimeTableDisplayFragment fragment = TimeTableDisplayFragment.newInstance (tableName, tableType, 0);
        fragment.setDay (0);

        FragmentTransaction transaction = getFragmentManager ().beginTransaction ();
        transaction.replace (R.id.fragmentStub, fragment);
        transaction.commit ();*/
    }

    private void setUpClickListeners ()
    {
        clickSupport.setOnItemClickListener (new ItemClickSupport.OnItemClickListener ()
        {
            @Override
            public void onItemClick (RecyclerView recyclerView, View view, int position, long id)
            {
                int tableType = TimeTableType.NONE;
                switch (position)
                {
                    case SCREEN_TODAY:
                        tableType = TimeTableType.NONE;
                        break;
                    case SCREEN_CLASSES:
                        tableType = TimeTableType.CLASS;
                        break;
                    case SCREEN_TEACHERS:
                        tableType = TimeTableType.TEACHER;
                        break;
                    case SCREEN_CLASSROOMS:
                        tableType = TimeTableType.CLASSROOM;
                        break;
                }

                FragmentTransaction transaction = getFragmentManager ().beginTransaction ();

                if (tableType != TimeTableType.NONE)
                {
                    TimeTableListFragment fragment = TimeTableListFragment.newInstance (tableType);

                    spinner.setVisibility (View.GONE);
                    getSupportActionBar ().setDisplayShowTitleEnabled (true);
                    getSupportActionBar ().setTitle (values.get (position));

                    transaction.replace (R.id.fragmentStub, fragment);
                }
                else
                {
                    getSupportActionBar ().setDisplayShowTitleEnabled (false);
                    spinner.setVisibility (View.VISIBLE);

                    int currentDay = Calendar.getInstance ().get (Calendar.DAY_OF_WEEK) - 2;

                    if (currentDay == spinner.getSelectedItemPosition ()) fragment.setDay (currentDay);
                    else spinner.setSelection (currentDay);

                    transaction.replace (R.id.fragmentStub, fragment);
                }

                transaction.commit ();

                drawerLayout.closeDrawer (drawerList);
            }
        });


    }
}