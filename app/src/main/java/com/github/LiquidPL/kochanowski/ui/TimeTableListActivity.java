package com.github.LiquidPL.kochanowski.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.LiquidPL.kochanowski.R;
import com.liquid.kochanparser.TimeTableType;

public class TimeTableListActivity
        extends BaseActivity
{
    public static final String ARG_TYPE = "type";

    private int tableType;

    private TimeTableListFragment listFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_timetable_list);

        tableType = TimeTableType.CLASS;

        listFragment = TimeTableListFragment.newInstance (tableType, null);

        getSupportFragmentManager ().beginTransaction ().
                replace (R.id.fragment_stub, listFragment).commit ();

    }

    @Override
    protected int getSelfNavDrawerItem ()
    {
        return NAVDRAWER_ITEM_BROWSE_TIMETABLES;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_timetable_list, menu);

        // setting the toolbar title to the filter selected on activity start
        getToolbar ().setTitle (getString (R.string.classes));

        // initializing SearchView
        final MenuItem searchItem = menu.findItem (R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService (Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView (searchItem);

        searchView.setSearchableInfo (searchManager.getSearchableInfo (getComponentName ()));

        TextView textView = (TextView) searchView.findViewById (android.support.v7.appcompat.R.id.search_src_text);
        textView.setTextColor (getResources ().getColor (R.color.white_100));
        textView.setHintTextColor (getResources ().getColor (R.color.white_30));

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
            case R.id.filter_class:
                item.setChecked (true);
                listFragment.setFilter (TimeTableType.CLASS);
                tableType = TimeTableType.CLASS;
                getToolbar ().setTitle (getString (R.string.classes));
                return true;
            case R.id.filter_teachers:
                item.setChecked (true);
                listFragment.setFilter (TimeTableType.TEACHER);
                tableType = TimeTableType.TEACHER;
                getToolbar ().setTitle (getString (R.string.teachers));
                return true;
            case R.id.filter_classrooms:
                item.setChecked (true);
                listFragment.setFilter (TimeTableType.CLASSROOM);
                getToolbar ().setTitle (getString (R.string.classrooms));
                tableType = TimeTableType.CLASSROOM;
                return true;
        }

        return super.onOptionsItemSelected (item);
    }

    /**
     * overriding startActivity so we can pass more data to SearchActivity
     */
    @Override
    public void startActivity (Intent intent)
    {
        if (Intent.ACTION_SEARCH.equals (intent.getAction ()))
        {
            intent.putExtra (ARG_TYPE, tableType);
        }

        super.startActivity (intent);
    }
}
