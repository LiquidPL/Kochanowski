package com.liquid.kochanowski;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.liquid.kochanowski.widget.ScrimInsetsFrameLayout;
import com.liquid.kochanowski.widget.SlidingTabLayout;
import com.liquid.kochanparser.TimeTableType;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.ListLayoutManager;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class TimeTableTabActivity extends ActionBarActivity
{
    static final String ARG_TABLE_NAME_SHORT = "shortname";
    static final String ARG_TABLE_NAME_LONG = "longname";
    static final String ARG_TABLE_TYPE = "type";

    private String shortName;
    private String longName;
    private int tableType;

    private SharedPreferences prefs;

    private Toolbar toolbar;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private List <String> values;
    private TypedArray icons;

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

    public TimeTableTabActivity ()
    {

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

        prefs = getSharedPreferences (getString (R.string.shared_prefs_name), MODE_PRIVATE);

        toolbar = (Toolbar) findViewById (R.id.toolbar);

        if (toolbar != null)
        {
            setSupportActionBar (toolbar);
            toolbar.setNavigationIcon (R.drawable.ic_arrow_back_white);
            toolbar.setTitleTextColor (getResources ().getColor (R.color.white_100));

            getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

            getSupportActionBar ().setTitle (longName + " (" + shortName + ")");
            if (tableType == TimeTableType.CLASSROOM) getSupportActionBar ().setTitle (shortName);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow ();
            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor (getResources ().getColor (R.color.primary_dark));
        }

        slidingTabLayout = (SlidingTabLayout) findViewById (R.id.sliding_tabs);
        viewPager = (ViewPager) findViewById (R.id.view_pager);

        viewPager.setAdapter (new ViewPagerAdapter (getSupportFragmentManager ()));
        slidingTabLayout.setColorList (getResources ().getColorStateList (R.color.sliding_tab_color));
        slidingTabLayout.setViewPager (viewPager);

        slidingTabLayout.setSelectedIndicatorColors (getResources ().getColor (R.color.accent));
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
                NavUtils.navigateUpFromSameTask (this);
                return true;
        }

        return super.onOptionsItemSelected (item);
    }
}
