package com.liquid.kochanowski;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
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


public class KochanowskiMainActivity extends ActionBarActivity implements  AdapterView.OnItemSelectedListener
{
    private static TimeTableDbHelper helper;

    private SharedPreferences prefs;

    private Toolbar toolbar;

    private Button syncButton;
    private TextView noTimeTablesAlert;

    private Spinner spinner;

    private View fragmentStub;
    private TimeTableDisplayFragment fragment;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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

        syncButton = (Button) findViewById (R.id.syncButton);
        noTimeTablesAlert = (TextView) findViewById (R.id.noTimeTablesAlert);

        spinner = (Spinner) findViewById (R.id.main_activity_spinner);

        fragmentStub = findViewById (R.id.fragmentStub);

        prefs = getSharedPreferences (getString (R.string.shared_prefs_name), MODE_PRIVATE);

        if (toolbar != null)
        {
            setSupportActionBar (toolbar);
            toolbar.setNavigationIcon (R.drawable.ic_menu_black);
            getSupportActionBar ().setDisplayShowTitleEnabled (false);
        }

        if (!prefs.getBoolean (getString (R.string.pref_timetables_synced), false))
        {
            syncButton.setVisibility (View.VISIBLE);
            noTimeTablesAlert.setVisibility (View.VISIBLE);
        }
        else
        {
            syncButton.setVisibility (View.INVISIBLE);
            noTimeTablesAlert.setVisibility (View.INVISIBLE);

            int currentDay = Calendar.getInstance ().get (Calendar.DAY_OF_WEEK) - 2;

            DaySelectAdapter adapter = new DaySelectAdapter (this, R.layout.spinner_item, DaySelectAdapter.getDays (this));
            spinner.setAdapter (adapter);
            spinner.setOnItemSelectedListener (this);
            spinner.setSelection (currentDay);

            if (savedInstanceState != null)
            {
                return;
            }

            fragment = TimeTableDisplayFragment.newInstance ("2A", TimeTableType.TIMETABLE_TYPE_CLASS, currentDay, 1);

            FragmentManager manager = getFragmentManager ();
            FragmentTransaction transaction = manager.beginTransaction ();

            transaction.add (R.id.fragmentStub, fragment);
            transaction.commit ();
        }
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    public void onSyncClick (View view)
    {
        Intent intent = new Intent (this, SyncActivity.class);
        startActivity (intent);
    }

    public static TimeTableDbHelper getHelper ()
    {
        return helper;
    }

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
    {
        FragmentManager manager = getFragmentManager ();
        FragmentTransaction transaction = manager.beginTransaction ();

        Log.i ("liquid", "Item selected: " + position);

        fragment = TimeTableDisplayFragment.newInstance ("2A", TimeTableType.TIMETABLE_TYPE_CLASS, position, 1);
        transaction.replace (R.id.fragmentStub, fragment);
        transaction.commit ();
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent)
    {

    }
}
