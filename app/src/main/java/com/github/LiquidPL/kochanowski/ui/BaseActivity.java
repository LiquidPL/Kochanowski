package com.github.LiquidPL.kochanowski.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.ui.dialog.DbResetDialog;
import com.github.LiquidPL.kochanowski.ui.fragment.TimeTableListFragment;
import com.github.LiquidPL.kochanowski.util.DbUtils;
import com.github.LiquidPL.kochanowski.util.PrefUtils;
import com.github.LiquidPL.kochanowski.ui.widget.ScrimInsetsScrollView;

import java.util.ArrayList;

/**
 * Created by liquid on 29.12.14.
 */
public class BaseActivity
        extends ActionBarActivity
        implements TimeTableListFragment.OnTimeTableSelectedListener
{
    // constants for possible navdrawer items
    protected static final int NAVDRAWER_ITEM_TODAY = 0;
    protected static final int NAVDRAWER_ITEM_BROWSE_TIMETABLES = 1;
    protected static final int NAVDRAWER_ITEM_CLASSES = 2;
    protected static final int NAVDRAWER_ITEM_TEACHERS = 3;
    protected static final int NAVDRAWER_ITEM_CLASSROOMS = 4;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 5;
    protected static final int NAVDRAWER_ITEM_HELP = 6;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;

    // list of titles for navdrawer items (order must be the same as above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[] {
            R.string.day_name_today,
            R.string.navdrawer_browse_timetables,
            R.string.classes,
            R.string.teachers,
            R.string.classrooms,
            R.string.action_settings,
            R.string.action_help
    };

    // list of icons for navdrawer items (order must be the same as above)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
            R.drawable.ic_today_black,
            R.drawable.ic_list_black,
            R.drawable.ic_group_black,
            R.drawable.ic_group_black,
            R.drawable.ic_person_black,
            R.drawable.ic_settings_black,
            R.drawable.ic_help_black
    };

    // delay in milliseconds to launch activity for the animation to end
    protected static final int ACTIVITY_LAUNCH_DELAY = 250;

    // drawer layout portrait orientation width in dp units
    private static final int DRAWER_WIDTH_DP = 56;

    protected Handler handler;

    // navigation drawer
    private DrawerLayout drawerLayout;

    // list of items that were added to the navdrawer
    private ArrayList <Integer> navDrawerItems = new ArrayList<> ();

    // list of views corresponding to each navdrawer item, null if not created
    private View[] navDrawerViews = null;

    private ViewGroup drawerItemsListContainer;

    private Toolbar toolbar;

    private int statusBarColor;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        statusBarColor = getResources ().getColor (R.color.primary_dark);

        handler = new Handler ();

        DbUtils.initialize (getApplicationContext ());
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        if (PrefUtils.isDatabaseUpgraded (this) && !getClass ().getName ().equals ("SyncActivity"))
        {
            new DbResetDialog ().show (getSupportFragmentManager (), null);
        }
    }

    @Override
    public void setContentView (int layoutResID)
    {
        super.setContentView (layoutResID);
        getToolbar ();
    }

    /**
     * Returns a navdrawer item that corresponds to this activity. Subclasses
     * should override this method to indicate what navdrawer item corresponds to them.
     * Returning NAVDRAWER_ITEM_INVALID means that this activity has no navdrawer item.
     */
    protected int getSelfNavDrawerItem ()
    {
        return NAVDRAWER_ITEM_INVALID;
    }

    private void setUpNavDrawer ()
    {
        // Indicates what navdrawer item should be selected.
        int selfItem = getSelfNavDrawerItem ();

        drawerLayout = (DrawerLayout) findViewById (R.id.drawer_layout);
        if (drawerLayout == null)
        {
            return;
        }

        // set the drawer layout width to <screen-width-in-dp>-56dp
        DisplayMetrics metrics = getResources ().getDisplayMetrics ();
        int screenWidth = ((int) (metrics.widthPixels / metrics.density));
        int drawerWidth = screenWidth - DRAWER_WIDTH_DP;

        drawerLayout.setStatusBarBackgroundColor (statusBarColor);
        ScrimInsetsScrollView navDrawer = (ScrimInsetsScrollView) findViewById (R.id.navdrawer);

        if (selfItem == NAVDRAWER_ITEM_INVALID)
        {
            // do not show a navdrawer
            if (navDrawer != null)
            {
                ((ViewGroup) navDrawer.getParent ()).removeView (navDrawer);
            }
            drawerLayout = null;
            return;
        }

        if (toolbar != null)
        {
            toolbar.setNavigationIcon (R.drawable.ic_menu_white);
            toolbar.setNavigationOnClickListener (new View.OnClickListener ()
            {
                @Override
                public void onClick (View v)
                {
                    drawerLayout.openDrawer (Gravity.START);
                }
            });
        }

        populateNavDrawer ();

        // Launching the app for the first time with navdrawer being open.
        if (!PrefUtils.isWelcomeDone (this))
        {
            PrefUtils.setWelcomeDone (this);
            drawerLayout.openDrawer (Gravity.START);
        }
        navDrawer.setMinimumWidth (drawerWidth);
    }

    private void populateNavDrawer ()
    {
        navDrawerItems.clear ();

        navDrawerItems.add (NAVDRAWER_ITEM_TODAY);

        if (PrefUtils.hasSyncedTimeTables (this))
        {
            navDrawerItems.add (NAVDRAWER_ITEM_BROWSE_TIMETABLES);
        }

        navDrawerItems.add (NAVDRAWER_ITEM_SEPARATOR);

        /*navDrawerItems.add (NAVDRAWER_ITEM_CLASSES);
        navDrawerItems.add (NAVDRAWER_ITEM_TEACHERS);
        navDrawerItems.add (NAVDRAWER_ITEM_CLASSROOMS);

        navDrawerItems.add (NAVDRAWER_ITEM_SEPARATOR);*/

        navDrawerItems.add (NAVDRAWER_ITEM_SETTINGS);
        navDrawerItems.add (NAVDRAWER_ITEM_HELP);

        createNavDrawerItems ();
    }

    private void createNavDrawerItems ()
    {
        drawerItemsListContainer = (ViewGroup) findViewById (R.id.navdrawer_items);
        if (drawerItemsListContainer == null)
        {
            return;
        }

        navDrawerViews = new View[navDrawerItems.size ()];
        drawerItemsListContainer.removeAllViews ();

        int i = 0;
        for (int itemId : navDrawerItems)
        {
            navDrawerViews[i] = makeNavDrawerItem (itemId, drawerItemsListContainer);
            drawerItemsListContainer.addView (navDrawerViews[i]);
            i++;
        }
    }

    private View makeNavDrawerItem (final int itemId, ViewGroup container)
    {
        boolean selected = getSelfNavDrawerItem () == itemId;

        int layoutToInflate;
        switch (itemId)
        {
            case NAVDRAWER_ITEM_SEPARATOR:
                layoutToInflate = R.layout.navdrawer_separator;
                break;
            default:
                layoutToInflate = R.layout.navdrawer_item;
                break;
        }

        View view = LayoutInflater.from (this).inflate (layoutToInflate, container, false);

        if (itemId == NAVDRAWER_ITEM_SEPARATOR)
        {
            // we're done inflating this view
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById (R.id.item_icon);
        TextView titleView = (TextView) view.findViewById (R.id.text);

        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set the icon and text
        iconView.setVisibility (iconId > 0 ? View.VISIBLE : View.GONE);

        if (iconId > 0)
        {
            iconView.setImageResource (iconId);
        }
        titleView.setText (getResources ().getString (titleId));

        setNavDrawerItemSelectionState (view, itemId, selected);

        view.setOnClickListener (new View.OnClickListener ()
        {
            @Override
            public void onClick (View v)
            {
                onNavDrawerItemClicked (itemId);
            }
        });

        return view;
    }

    private void setNavDrawerItemSelected (int itemId)
    {
        if (navDrawerViews != null)
        {
            for (int i = 0; i < navDrawerViews.length; i++)
            {
                if (navDrawerItems.get (i) == itemId)
                {
                    setNavDrawerItemSelectionState (navDrawerViews[i], itemId, true);
                }
            }
        }
    }

    private void setNavDrawerItemSelectionState (View view, int itemId, boolean selected)
    {
        if (itemId == NAVDRAWER_ITEM_SEPARATOR)
        {
            return;
        }

        ImageView iconView = (ImageView) view.findViewById (R.id.item_icon);
        TextView titleView = (TextView) view.findViewById (R.id.text);

        iconView.setColorFilter (selected ?
                getResources ().getColor (R.color.primary) :
                getResources ().getColor (R.color.black_100));
        titleView.setTextColor (selected ?
                getResources ().getColor (R.color.primary) :
                getResources ().getColor (R.color.black_87));
    }

    private void onNavDrawerItemClicked (final int itemId)
    {
        if (itemId == getSelfNavDrawerItem ())
        {
            drawerLayout.closeDrawer (Gravity.START);
            return;
        }

        if (isSpecialItem (itemId))
        {
            handler.postDelayed (new Runnable ()
            {
                @Override
                public void run ()
                {
                    goToNavDrawerItem (itemId);

                }
            }, ACTIVITY_LAUNCH_DELAY);
        }
        else
        {
            handler.postDelayed (new Runnable ()
            {
                @Override
                public void run ()
                {
                    goToNavDrawerItem (itemId);

                }
            }, ACTIVITY_LAUNCH_DELAY);

            setNavDrawerItemSelected (itemId);
        }

        drawerLayout.closeDrawer (Gravity.START);
    }

    private void goToNavDrawerItem (final int itemId)
    {
        Intent intent;
        switch (itemId)
        {
            case NAVDRAWER_ITEM_TODAY:
                intent = new Intent (this, MainActivity.class);
                startActivity (intent);
                finish ();
                break;
            case NAVDRAWER_ITEM_BROWSE_TIMETABLES:
                intent = new Intent (this, TimeTableListActivity.class);
                startActivity (intent);
                finish ();
                break;
            /*case NAVDRAWER_ITEM_CLASSES:
                intent = new Intent (this, TimeTableListActivity.class);
                intent.putExtra (TimeTableListActivity.ARG_TYPE, NAVDRAWER_ITEM_CLASSES);
                startActivity (intent);
                finish ();
                break;
            case NAVDRAWER_ITEM_TEACHERS:
                intent = new Intent (this, TimeTableListActivity.class);
                intent.putExtra (TimeTableListActivity.ARG_TYPE, NAVDRAWER_ITEM_TEACHERS);
                startActivity (intent);
                finish ();
                break;
            case NAVDRAWER_ITEM_CLASSROOMS:
                intent = new Intent (this, TimeTableListActivity.class);
                intent.putExtra (TimeTableListActivity.ARG_TYPE, NAVDRAWER_ITEM_CLASSROOMS);
                startActivity (intent);
                finish ();
                break;*/
            case NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent (this, SettingsActivity.class);
                startActivity (intent);
                break;
            case NAVDRAWER_ITEM_HELP:
                intent = new Intent (this, HelpActivity.class);
                startActivity (intent);
                finish ();
                break;
        }
    }

    private boolean isSpecialItem (int itemId)
    {
        return itemId == NAVDRAWER_ITEM_SETTINGS;
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState)
    {
        super.onPostCreate (savedInstanceState);

        setUpNavDrawer ();
    }

    protected Toolbar getToolbar ()
    {
        if (toolbar == null)
        {
            toolbar = (Toolbar) findViewById (R.id.toolbar);

            if (toolbar != null)
            {
                setSupportActionBar (toolbar);
            }
        }
        return toolbar;
    }

    @Override
    public void onTimeTableSelected (String shortName, String longName, int tableType)
    {
        final Intent intent = new Intent (this, TimeTableTabActivity.class);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_TYPE, tableType);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_NAME_SHORT, shortName);
        intent.putExtra (TimeTableTabActivity.ARG_TABLE_NAME_LONG, longName);

        handler.postDelayed (new Runnable ()
        {
            @Override
            public void run ()
            {
                startActivity (intent);
            }
        }, ACTIVITY_LAUNCH_DELAY);
    }
}
