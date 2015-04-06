package com.github.LiquidPL.kochanowski.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.LiquidPL.kochanowski.db.TimeTableDbHelper;

/**
 * Created by liquid on 21.12.14.
 */
public class DbUtils
{
    private static DbUtils instance;
    private static TimeTableDbHelper helper;
    private SQLiteDatabase database;

    private int openCounter = 0;

    private DbUtils ()
    {

    }

    public static synchronized void initialize (Context context)
    {
        if (helper == null)
        {
            instance = new DbUtils ();
            helper = new TimeTableDbHelper (context);
        }
    }

    public static synchronized DbUtils getInstance ()
    {
        if (instance == null)
        {
            throw new IllegalStateException (DbUtils.class.getSimpleName () +
                                                     " is not initialized, call initialize () method first");
        }
        return instance;
    }

    public synchronized SQLiteDatabase openDatabase ()
    {
        openCounter++;
        if (openCounter == 1)
        {
            // opening new database
            database = helper.getWritableDatabase ();
        }
        return database;
    }

    public synchronized void closeDatabase ()
    {
        openCounter--;
        if (openCounter == 0)
        {
            // closing the database
            database.close ();
        }
    }

    public void resetTables ()
    {
        SQLiteDatabase db = openDatabase ();
        helper.dropTables (db);
        helper.createTables (db);
        closeDatabase ();
    }
}
