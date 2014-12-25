package com.liquid.kochanowski.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by liquid on 21.12.14.
 */
public class DatabaseHelper
{
    private static TimeTableDbHelper helper;

    private DatabaseHelper ()
    {

    }

    public static void initHelper (Context context)
    {
        helper = new TimeTableDbHelper (context);
    }

    public static SQLiteDatabase getWritableDatabase ()
    {
        return helper.getWritableDatabase ();
    }

    public static SQLiteDatabase getReadableDatabase ()
    {
        return helper.getReadableDatabase ();
    }

    public static void resetTables ()
    {
        helper.dropTables (helper.getWritableDatabase ());
        helper.createTables (helper.getWritableDatabase ());
    }
}
