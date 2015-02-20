package com.github.LiquidPL.kochanowski.parse;

import android.database.sqlite.SQLiteDatabase;

import com.github.LiquidPL.kochanowski.parse.DbWriteRunnable.DbWriteRunnableMethods;
import com.github.LiquidPL.kochanowski.parse.TimeTableDownloadRunnable.DownloadRunnableMethods;
import com.github.LiquidPL.kochanowski.parse.table.TimeTable;

import java.net.URL;

/**
 * Created by liquid on 03.12.14.
 */
public class ParseTask implements DownloadRunnableMethods, DbWriteRunnableMethods
{
    private URL url;

    private Thread thread;

    private Runnable downloadRunnable;
    private Runnable dbWriteRunnable;

    protected ThreadManager manager;

    private TimeTable table;
    private String tablename;

    private SQLiteDatabase db;

    public ParseTask (SQLiteDatabase db)
    {
        this.db = db;

        manager = ThreadManager.getInstance ();
        table = new TimeTable ();
        downloadRunnable = new TimeTableDownloadRunnable (this);
        dbWriteRunnable = new DbWriteRunnable (this);
    }

    private void setCurrentThread (Thread currentThread)
    {
        synchronized (manager)
        {
            this.thread = currentThread;
        }
    }

    public Thread getCurrentThread ()
    {
        return thread;
    }

    @Override
    public void setDbWriteThread (Thread currentThread)
    {
        setCurrentThread (currentThread);
    }

    @Override
    public void setDownloadThread (Thread currentThread)
    {
        setCurrentThread (currentThread);
    }

    @Override
    public void handleDbWriteState (int state)
    {
        int newState;

        switch (state)
        {
            case DbWriteRunnable.WRITE_COMPLETED:
                newState = ThreadManager.TASK_COMPLETED;
                break;
            case DbWriteRunnable.WRITE_FAILED:
                newState = ThreadManager.DOWNLOAD_FAILED;
                break;
            default:
                newState = ThreadManager.DB_WRITE_STARTED;
                break;
        }

        manager.handleState (this, newState);
    }

    @Override
    public void handleDownloadState (int state)
    {
        int newState;

        switch (state)
        {
            case TimeTableDownloadRunnable.DOWNLOAD_COMPLETED:
                newState = ThreadManager.DOWNLOAD_COMPLETE;
                break;
            case TimeTableDownloadRunnable.DOWNLOAD_FAILED:
                newState = ThreadManager.DOWNLOAD_FAILED;
                break;
            default:
                newState = ThreadManager.DOWNLOAD_STARTED;
                break;
        }

        manager.handleState (this, newState);
    }

    public void recycle ()
    {
        url = null;
        thread = null;
        downloadRunnable = null;
        dbWriteRunnable = null;
        manager = null;
        table = null;
        tablename = null;
        db = null;
    }

    public void cancel ()
    {
        recycle ();

        if (thread != null)
        {
            thread.interrupt ();
        }
    }

    public URL getUrl ()
    {
        return url;
    }

    public void setUrl (URL url)
    {
        this.url = url;
    }

    public TimeTable getTable ()
    {
        return table;
    }

    public Runnable getDownloadRunnable ()
    {
        return downloadRunnable;
    }

    public Runnable getDbWriteRunnable ()
    {
        return dbWriteRunnable;
    }

    public SQLiteDatabase getDb ()
    {
        return db;
    }
}
