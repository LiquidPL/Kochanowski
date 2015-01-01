package com.liquid.kochanowski.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.liquid.kochanowski.R;

public class HelpActivity extends BaseActivity
{

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_help);
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_help, menu);
        return true;
    }

    @Override
    protected int getSelfNavDrawerItem ()
    {
        return NAVDRAWER_ITEM_HELP;
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
            case R.id.action_about:
                break;
            case R.id.action_os_licenses:
                break;
        }

        return super.onOptionsItemSelected (item);
    }
}
