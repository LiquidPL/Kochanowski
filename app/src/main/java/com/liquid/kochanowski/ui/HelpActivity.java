package com.liquid.kochanowski.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.liquid.kochanowski.BuildConfig;
import com.liquid.kochanowski.R;

public class HelpActivity extends BaseActivity
{

    public static class AboutDialogFragment extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog (Bundle savedInstanceState)
        {
            // get the dialog builder and layout inflater
            AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
            LayoutInflater inflater = getActivity ().getLayoutInflater ();

            // inflate the view
            View view = inflater.inflate (R.layout.dialog_about, null);

            // set the version name TextView
            TextView version = (TextView) view.findViewById (R.id.app_version);
            version.setText (BuildConfig.VERSION_NAME);

            // set the view in Builder
            builder.setView (view);

            // add an 'OK' button
            builder.setPositiveButton (getString(R.string.action_ok), null);

            // return the dialog
            return builder.create ();
        }
    }

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
                new AboutDialogFragment ().show (getSupportFragmentManager (), null);
                break;
            case R.id.action_os_licenses:
                break;
        }

        return super.onOptionsItemSelected (item);
    }
}
