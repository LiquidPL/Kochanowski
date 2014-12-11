package com.liquid.kochanowski;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SyncActivity extends ActionBarActivity
{
    private ThreadManager manager;

    private List<String> urls;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEditor;

    protected TextView currentDownload;
    protected TextView currentCount;
    protected ProgressBar progressBar;

    protected TextView syncResult;
    protected Button continueButton;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_sync);

        prefs = getSharedPreferences (getString (R.string.shared_prefs_name), MODE_PRIVATE);
        prefEditor = prefs.edit ();

        Toolbar toolbar = (Toolbar) findViewById (R.id.activity_sync_toolbar);
        if (toolbar != null)
        {
            setSupportActionBar (toolbar);
            //toolbar.setNavigationIcon (R.drawable.ic_arrow_back);
        }

        if (prefs.getBoolean (getString (R.string.pref_timetables_synced), false) == false)
        {
            initSync ();
        }
    }

    public void initSync ()
    {
        urls = new ArrayList<> ();

        currentDownload = (TextView) findViewById (R.id.currentDownload);
        currentCount = (TextView) findViewById (R.id.currentCount);
        progressBar = (ProgressBar) findViewById (R.id.progressBar);

        syncResult = (TextView) findViewById (R.id.syncResult);
        continueButton = (Button) findViewById (R.id.continueButton);

        currentDownload.setText (getString(R.string.downloading_metadata));
        currentCount.setText ("");

        Handler syncActivityHandler = new Handler ();
        Runnable runnable = new MasterlistDownloadRunnable (urls, this, syncActivityHandler);

        new Thread (runnable).start ();
    }

    public void beginSync (List<String> urls)
    {
        progressBar.setMax (urls.size ());
        currentCount.setText ("0/" + urls.size ());
        manager.setTimeTableCount (urls.size ());

        manager = ThreadManager.getInstance ();

        manager.setContext (this);

        for (String url : urls)
        {
            manager.parseTimeTable (url);
        }
    }

    public void finishSync ()
    {
        switch (manager.getResult ())
        {
            case ThreadManager.DOWNLOAD_FAILED:
                syncResult.setText (R.string.sync_result_failure);
                prefEditor.putBoolean (getString (R.string.pref_timetables_synced), false);
                break;
            case ThreadManager.TASK_COMPLETED:
                syncResult.setText (R.string.sync_result_success);
                prefEditor.putBoolean (getString (R.string.pref_timetables_synced), true);

                getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
                break;
        }

        prefEditor.commit ();

        syncResult.setVisibility (View.VISIBLE);
        continueButton.setVisibility (View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_sync, menu);
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
            case R.id.home:
                NavUtils.navigateUpFromSameTask (this);
                return true;
        }

        return super.onOptionsItemSelected (item);
    }

    public void onContinueClick (View view)
    {
        Intent intent = new Intent (this, KochanowskiMainActivity.class);
        startActivity (intent);
    }
}
