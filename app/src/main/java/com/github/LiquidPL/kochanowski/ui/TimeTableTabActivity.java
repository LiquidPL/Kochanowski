package com.github.LiquidPL.kochanowski.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.ui.fragment.TimeTableDisplayFragment;
import com.github.LiquidPL.kochanowski.ui.widget.SlidingTabLayout;

import java.util.Calendar;
import java.util.Locale;


public class TimeTableTabActivity extends BaseActivity
{
    // timetable's display name
    static final String ARG_TABLE_NAME = "name";
    // timetable's name in database
    static final String ARG_TABLE_VALUE = "value";
    static final String ARG_TABLE_TYPE = "type";

    private String name;
    private String value;
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
            return TimeTableDisplayFragment.newInstance (value, tableType, position, 0);
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

        name = extras.getString (ARG_TABLE_NAME);
        value = extras.getString (ARG_TABLE_VALUE);
        tableType = extras.getInt (ARG_TABLE_TYPE);

        slidingTabLayout = (SlidingTabLayout) findViewById (R.id.sliding_tabs);
        viewPager = (ViewPager) findViewById (R.id.view_pager);

        viewPager.setAdapter (new ViewPagerAdapter (getSupportFragmentManager ()));
        slidingTabLayout.setColorList (getResources ().getColorStateList (R.color.sliding_tab_color));
        slidingTabLayout.setViewPager (viewPager);

        slidingTabLayout.setSelectedIndicatorColors (getResources ().getColor (R.color.accent));

        getSupportActionBar ().setTitle (name);

        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_timetable_tab, menu);
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
