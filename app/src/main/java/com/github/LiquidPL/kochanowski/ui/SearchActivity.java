package com.github.LiquidPL.kochanowski.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.parse.table.TimeTableType;

public class SearchActivity
        extends BaseActivity
{
    private String searchQuery;
    private int tableType;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_search);

        // get the search query
        Intent intent = getIntent ();
        if (intent.ACTION_SEARCH.equals (intent.getAction ()))
        {
            searchQuery = intent.getStringExtra (SearchManager.QUERY);
            tableType = intent.getIntExtra ("type", TimeTableType.NONE);
        }

        Log.i ("liquid", searchQuery);
        Log.i ("liquid", ""+tableType);

        TimeTableListFragment fragment = TimeTableListFragment.newInstance (tableType, searchQuery);

        FragmentTransaction transaction = getSupportFragmentManager ().beginTransaction ();
        transaction.replace (R.id.fragment_stub, fragment);
        transaction.commit ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_search, menu);
        return true;
    }

    @Override
    protected int getSelfNavDrawerItem ()
    {
        return NAVDRAWER_ITEM_BROWSE_TIMETABLES;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        return super.onOptionsItemSelected (item);
    }

}
