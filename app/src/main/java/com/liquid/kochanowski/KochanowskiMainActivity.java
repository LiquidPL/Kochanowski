package com.liquid.kochanowski;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class KochanowskiMainActivity extends ActionBarActivity
{
    private static TimeTableDbHelper helper;

    private SharedPreferences prefs;

    private Button syncButton;
    private TextView noTimeTablesAlert;

    public KochanowskiMainActivity ()
    {
        helper = new TimeTableDbHelper (this);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_kochanowski_main);

        syncButton = (Button) findViewById (R.id.syncButton);
        noTimeTablesAlert = (TextView) findViewById (R.id.noTimeTablesAlert);

        prefs = getSharedPreferences (getString (R.string.shared_prefs_name), MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);

        if (toolbar != null)
        {
            setSupportActionBar (toolbar);
            toolbar.setNavigationIcon (R.drawable.ic_menu_black);
        }

        if (prefs.getBoolean (getString (R.string.pref_timetables_synced), false) == false)
        {
            syncButton.setVisibility (View.VISIBLE);
            noTimeTablesAlert.setVisibility (View.VISIBLE);
        }
        else
        {
            syncButton.setVisibility (View.INVISIBLE);
            noTimeTablesAlert.setVisibility (View.INVISIBLE);
        }
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        if (prefs.getBoolean (getString (R.string.pref_timetables_synced), false) == false)
        {
            syncButton.setVisibility (View.VISIBLE);
            noTimeTablesAlert.setVisibility (View.VISIBLE);
        }
        else
        {
            syncButton.setVisibility (View.INVISIBLE);
            noTimeTablesAlert.setVisibility (View.INVISIBLE);
        }
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
}
