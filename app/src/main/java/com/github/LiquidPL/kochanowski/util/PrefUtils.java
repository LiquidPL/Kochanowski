package com.github.LiquidPL.kochanowski.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtils
{
    private final static String TAG = "PrefUtils";

    /**
     * Boolean preference checking whether user have successfully downloaded timetables to his device.
     */
    public static final String PREF_TABLES_SYNCED = "pref_tables_synced";

    /**
     * Boolean flag indicating whether database has been upgraded.
     */
    public static final String PREF_DATABASE_UPGRADED = "pref_database_upgraded";

    /**
     * String preference that holds a name of users preferred timetable.
     */
    public static final String PREF_DEFAULT_TABLE_NAME = "pref_table_name";

    /**
     * Integer preference that holds the user preferred group (1,2 or both, values 0/1/2)
     */
    public static final String PREF_DEFAULT_GROUP = "pref_default_group";

    /**
     * Boolean preference that indicates whether user has performed the first-run UI welcome flow.
     */
    public static final String PREF_WELCOME_DONE = "pref_welcome_done";

    /**
     * Boolean preference that indicates whether app should send notifications about ongoing lessons.
     */
    public static final String PREF_SHOULD_NOTIFY = "pref_notify";

    /**
     * Boolean preference that indicates whether app should send notification to a wearable.
     */
    public static final String PREF_SHOULD_NOTIFY_WEARABLE = "pref_notify_wearable";

    public static boolean hasSyncedTimeTables (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getBoolean (PREF_TABLES_SYNCED, false);
    }

    public static void setTimeTablesSynced (final Context context, boolean synced)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putBoolean (PREF_TABLES_SYNCED, synced).apply ();
    }

    public static boolean isDatabaseUpgraded (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getBoolean (PREF_DATABASE_UPGRADED, false);
    }

    public static void setDatabaseUpgradeStatus (final Context context, boolean upgraded)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putBoolean (PREF_DATABASE_UPGRADED, upgraded).apply ();
    }

    public static String getDefaultTableName (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getString (PREF_DEFAULT_TABLE_NAME, "");
    }

    public static void setTableName (final Context context, String tableName)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putString (PREF_DEFAULT_TABLE_NAME, tableName).apply ();
    }

    public static int getDefaultGroup (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return Integer.valueOf (prefs.getString (PREF_DEFAULT_GROUP, "0"));
    }

    public static void setDefaultGroup (final Context context, int defaultGroup)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        // this preference is set using a ListPreference that returns a string, so internally we're doing it so
        prefs.edit ().putString (PREF_DEFAULT_GROUP, Integer.toString (defaultGroup)).apply ();
    }

    public static boolean isWelcomeDone (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getBoolean (PREF_WELCOME_DONE, false);
    }

    public static void setWelcomeDone (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putBoolean (PREF_WELCOME_DONE, true).apply ();
    }

    public static boolean shouldNotify (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getBoolean (PREF_SHOULD_NOTIFY, false);
    }

    public static void setNotifyPreference (final Context context, boolean notify)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putBoolean (PREF_SHOULD_NOTIFY, notify).apply ();
    }

    public static boolean shouldNotifyWearable (final Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        return prefs.getBoolean (PREF_SHOULD_NOTIFY_WEARABLE, false);
    }

    public static void setWearableNotifyPreference (final Context context, boolean notify)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences (context);

        prefs.edit ().putBoolean (PREF_SHOULD_NOTIFY_WEARABLE, notify).apply ();
    }
}
