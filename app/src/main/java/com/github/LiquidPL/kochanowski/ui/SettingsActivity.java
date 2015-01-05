package com.github.LiquidPL.kochanowski.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.ClassTable;
import com.github.LiquidPL.kochanowski.util.DbUtils;
import com.github.LiquidPL.kochanowski.util.PrefUtils;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener
{
    SQLiteDatabase db;
    Cursor cur;
    CharSequence[] entries;
    CharSequence[] values;

    ListPreference defaultClass;
    ListPreference defaultGroup;
    Preference removeTables;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        DbUtils.initHelper (this);

        addPreferencesFromResource (R.xml.preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (this);
        prefs.registerOnSharedPreferenceChangeListener (this);

        db = DbUtils.getReadableDatabase ();
        cur = db.rawQuery ("SELECT * FROM " + ClassTable.TABLE_NAME +
                " ORDER BY " + ClassTable.COLUMN_NAME_NAME_SHORT +
                " ASC", null);
        int length = cur.getCount ();

        entries = new CharSequence[length];
        values = new CharSequence[length];

        for (int i = 0; i < length; i++)
        {
            cur.moveToPosition (i);

            CharSequence shortname = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_SHORT));
            CharSequence longname = cur.getString (cur.getColumnIndexOrThrow (ClassTable.COLUMN_NAME_NAME_LONG));

            entries[i] = longname + " (" + shortname + ")";
            values[i] = shortname;
        }

        defaultClass = (ListPreference) findPreference (getString (R.string.pref_table_name));
        defaultGroup = (ListPreference) findPreference (getString (R.string.pref_default_group));
        removeTables = findPreference (getString(R.string.pref_db_reset));

        removeTables.setOnPreferenceClickListener (this);

        if (PrefUtils.hasSyncedTimeTables (this))
        {
            defaultClass.setEnabled (true);
            removeTables.setEnabled (true);
            removeTables.setSummary ("");
        }
        else
        {
            defaultClass.setEnabled (false);
            defaultClass.setSummary (getString(R.string.download_tables_first));
            removeTables.setEnabled (false);
            removeTables.setSummary (getString(R.string.download_tables_first));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow ();
            window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor (getResources ().getColor (R.color.primary_dark));
        }
    }

    @Override
    protected void onResume ()
    {
        super.onResume ();

        defaultClass.setEntries (entries);
        defaultClass.setEntryValues (values);

        if (defaultClass.getEntry () != null)
        {
            defaultClass.setSummary (defaultClass.getEntry ());
        }

        if (defaultGroup.getEntry () != null)
        {
            defaultGroup.setSummary (defaultGroup.getEntry ());
        }
    }

    @Override
    protected void onPostCreate (Bundle savedInstanceState)
    {
        super.onPostCreate (savedInstanceState);

        ViewGroup root = (ViewGroup) findViewById (android.R.id.content);
        View list = root.getChildAt (0);
        Toolbar toolbar = (Toolbar) LayoutInflater.from (this).inflate (R.layout.toolbar, root, false);

        root.addView (toolbar, 0);
        root.bringChildToFront (toolbar);

        toolbar.setNavigationOnClickListener (new View.OnClickListener ()
        {
            @Override
            public void onClick (View v)
            {
                finish ();
            }
        });

        toolbar.setTitle (getString (R.string.action_settings));
        toolbar.setNavigationIcon (R.drawable.ic_arrow_back_white);

        list.setPadding (0, (int) getResources ().getDimension (R.dimen.toolbar_height), 0, 0);
    }

    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key)
    {
        Preference preference = findPreference (key);

        if (preference instanceof ListPreference)
        {
            ListPreference listPreference = ((ListPreference) preference);
            preference.setSummary (listPreference.getEntry ());
        }
    }

    @Override
    public boolean onPreferenceClick (Preference preference)
    {
        if (preference.getKey ().equals ("pref_db_reset"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder (this);

            builder.setTitle (getString (R.string.dialog_remove_timetables));
            builder.setMessage (getString(R.string.dialog_remove_timetables_message));

            builder.setPositiveButton (getString(R.string.action_yes), new DialogInterface.OnClickListener ()
            {
                @Override
                public void onClick (DialogInterface dialog, int which)
                {
                    PrefUtils.setTimeTablesSynced (getApplicationContext (), false);
                }
            });
            builder.setNegativeButton (getString (R.string.action_no), new DialogInterface.OnClickListener ()
            {
                @Override
                public void onClick (DialogInterface dialog, int which)
                {

                }
            });

            builder.create ().show ();

            return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected (item);
    }
}
