package com.github.LiquidPL.kochanowski.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.LiquidPL.kochanowski.db.TimeTableDbHelper;

/**
 * Created by liquid on 21.12.14.
 */
public class DbUtils
{
    private static TimeTableDbHelper helper;

    private DbUtils ()
    {

    }

    public static void initHelper (Context context)
    {
        if (helper == null)
        {
            helper = new TimeTableDbHelper (context);
        }
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
