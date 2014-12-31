package com.liquid.kochanowski.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.liquid.kochanowski.R;
import com.liquid.kochanowski.widget.SlidingTabLayout;
import com.liquid.kochanparser.TimeTableType;

import java.util.Calendar;
import java.util.Locale;


public class TimeTableTabActivity extends BaseActivity
{
    static final String ARG_TABLE_NAME_SHORT = "shortname";
    static final String ARG_TABLE_NAME_LONG = "longname";
    static final String ARG_TABLE_TYPE = "type";

    private String shortName;
    private String longName;
    private int tableType;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private ViewPagerAdapter (FragmentManager fm)
        {
            super (fm);
        }

        @Override
        public Fragment getItem (int position)
        {
            return TimeTableDisplayFragment.newInstance (shortName, tableType, position, 0);
        }

        @Override
        public int getCount ()
        {
            return 5;
        }

        @Override
        public CharSequence getPageTitle (int position)
        {
            Calendar cal = Calendar.getInstance ();
            cal.set (Calendar.DAY_OF_WEEK, position + 2);

            return cal.getDisplayName (Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault ());
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_time_table_tab);

        Bundle extras = getIntent ().getExtras ();

        shortName = extras.getString (ARG_TABLE_NAME_SHORT);
        longName = extras.getString (ARG_TABLE_NAME_LONG);
        tableType = extras.getInt (ARG_TABLE_TYPE);

        slidingTabLayout = (SlidingTabLayout) findViewById (R.id.sliding_tabs);
        viewPager = (ViewPager) findViewById (R.id.view_pager);

        viewPager.setAdapter (new ViewPagerAdapter (getSupportFragmentManager ()));
        slidingTabLayout.setColorList (getResources ().getColorStateList (R.color.sliding_tab_color));
        slidingTabLayout.setViewPager (viewPager);

        slidingTabLayout.setSelectedIndicatorColors (getResources ().getColor (R.color.accent));

        getSupportActionBar ().setTitle (longName + " (" + shortName + ")");
        if (tableType == TimeTableType.CLASSROOM) getSupportActionBar ().setTitle (shortName);

        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_time_table_tab, menu);
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
        switch (id)
        {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent (this);
                intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo (this, intent);
                return true;
        }

        return super.onOptionsItemSelected (item);
    }
}
