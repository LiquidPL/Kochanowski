package com.github.LiquidPL.kochanowski.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.LiquidPL.kochanowski.BuildConfig;
import com.github.LiquidPL.kochanowski.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HelpActivity extends BaseActivity
{

    public static class AboutDialogFragment extends DialogFragment
    {
        @NonNull
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

            builder.setTitle (getString (R.string.action_about));

            // add an 'OK' button
            builder.setPositiveButton (getString(R.string.action_ok), null);

            // return the dialog
            return builder.create ();
        }
    }

    public static class LicensesDialogFragment extends DialogFragment
    {
        @NonNull
        @Override
        public Dialog onCreateDialog (Bundle savedInstanceState)
        {
            // get the dialog builder and layout inflater
            AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ());
            LayoutInflater inflater = getActivity ().getLayoutInflater ();

            // inflate the view
            View view = inflater.inflate (R.layout.dialog_licenses, null);

            // put the licenses file into a TextView
            AssetManager manager = getActivity ().getAssets ();
            try
            {
                InputStream istr = manager.open ("licenses.txt");
                BufferedReader reader = new BufferedReader (new InputStreamReader (istr));

                StringBuilder stringBuilder = new StringBuilder ();
                String input;

                while ((input = reader.readLine ()) != null)
                {
                    stringBuilder.append (input);
                    stringBuilder.append ("\n");
                }

                TextView textView = (TextView) view.findViewById (R.id.licenses_text);
                textView.setText (stringBuilder.toString ());
            }
            catch (IOException e)
            {
                e.printStackTrace ();
            }

            builder.setView (view);

            builder.setTitle (getString (R.string.action_os_licenses));

            builder.setPositiveButton (getString (R.string.action_ok), null);

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
                return true;
            case R.id.action_os_licenses:
                new LicensesDialogFragment ().show (getSupportFragmentManager (), null);
                return true;
        }

        return super.onOptionsItemSelected (item);
    }
}
