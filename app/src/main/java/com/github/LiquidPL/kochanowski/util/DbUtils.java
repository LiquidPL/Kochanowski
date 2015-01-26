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

    private static SQLiteDatabase readableDb;
    private static SQLiteDatabase writableDb;

    private DbUtils ()
    {

    }

    public static void initHelper (Context context)
    {
        if (helper == null)
        {
            helper = new TimeTableDbHelper (context);

            readableDb = helper.getReadableDatabase ();
            writableDb = helper.getWritableDatabase ();
        }
    }

    public static SQLiteDatabase getWritableDatabase ()
    {
        return writableDb;
    }

    public static SQLiteDatabase getReadableDatabase ()
    {
        return readableDb;
    }

    public static void resetTables ()
    {
        helper.dropTables (writableDb);
        helper.createTables (writableDb);
    }
}
