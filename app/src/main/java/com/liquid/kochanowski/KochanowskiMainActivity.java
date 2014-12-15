package com.liquid.kochanowski;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.liquid.kochanparser.TimeTableType;

import java.util.Calendar;


public class KochanowskiMainActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener
{
    private static TimeTableDbHelper helper;

    private SharedPreferences prefs;

    private Toolbar toolbar;
    private Spinner spinner;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private TimeTableDisplayFragment fragment;

    public KochanowskiMainActivity ()
    {
        helper = new TimeTableDbHelper (this);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_kochanowski_main);

        toolbar = (Toolbar) findViewById (R.id.activity_main_toolbar);
        spinner = (Spinner) findViewById (R.id.main_activity_spinner);

        drawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle (
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        prefs = getSharedPreferences (getString (R.string.shared_prefs_name), MODE_PRIVATE);
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        if (toolbar != null)
        {
            setSupportActionBar (toolbar);
            toolbar.setNavigationIcon (R.drawable.ic_menu_black);
            getSupportActionBar ().setDisplayShowTitleEnabled (false);

            drawerLayout.setDrawerListener (toggle);
        }

        DaySelectAdapter adapter = new DaySelectAdapter (this, R.layout.spinner_item, DaySelectAdapter.getDays (this));
        spinner.setAdapter (adapter);
        spinner.setOnItemSelectedListener (this);
        spinner.setSelection (Calendar.getInstance ().get (Calendar.DAY_OF_WEEK) - 2);

        fragment = TimeTableDisplayFragment.newInstance (
                prefs.getString (getString (R.string.pref_table_name), ""),
                TimeTableType.TIMETABLE_TYPE_CLASS,
                1);

        FragmentTransaction transaction = getFragmentManager ().beginTransaction ();

        transaction.replace (R.id.fragmentStub, fragment);
        transaction.commit ();
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState)
    {
        super.onPostCreate (savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState ();
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
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent)
    {

    }

    public static TimeTableDbHelper getHelper ()
    {
        return helper;
    }
}
