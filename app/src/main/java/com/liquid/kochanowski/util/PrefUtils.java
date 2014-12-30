package com.liquid.kochanowski.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.liquid.kochanowski.ui.TimeTableDisplayFragment.Group;

/**
 * Created by liquid on 29.12.14.
 */
public class PrefUtils
{
    private final static String TAG = "PrefUtils";

    /**
     * Boolean preference checking whether user have successfully downloaded timetables to his device.
     */
    public static final String PREF_TABLES_SYNCED = "pref_tables_synced";

    /**
     * String preference that holds a name of users preferred timetable.
     */
    public static final String PREF_TABLE_NAME = "pref_table_name";

    /**
     * Integer preference that holds the user preferred group (1,2 or both, values 0/1/2)
     */
    public static final String PREF_DEFAULT_GROUP = "pref_default_group";

    /**
     * Boolean preference that indicates whether user has performed the first-run UI welcome flow.
     */
    public static final String PREF_WELCOME_DONE = "pref_welcome_done";

    public static boolean hasSyncedTimeTables (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getBoolean (PREF_TABLES_SYNCED, false);
    }

    public static void setTimeTablesSynced (final Context context, boolean synced)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putBoolean (PREF_TABLES_SYNCED, synced).commit ();
    }

    public static String getTableName (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getString (PREF_TABLE_NAME, "");
    }

    public static void setTableName (final Context context, String tableName)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putString (PREF_TABLE_NAME, tableName).commit ();
    }

    public static int getDefaultGroup (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return Integer.valueOf (prefs.getString (PREF_DEFAULT_GROUP, "2"));
    }

    public static void setDefaultGroup (final Context context, int defaultGroup)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        // this preference is set using a ListPreference that returns a string, so internally we're doing it so
        prefs.edit ().putString (PREF_DEFAULT_GROUP, Integer.toString (defaultGroup)).commit ();
    }

    public static boolean isWelcomeDone (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getBoolean (PREF_WELCOME_DONE, false);
    }

    public static void setWelcomeDone (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putBoolean (PREF_WELCOME_DONE, true).commit ();
    }
}
