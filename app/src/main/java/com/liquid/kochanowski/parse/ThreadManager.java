package com.liquid.kochanowski.parse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.liquid.kochanowski.R;
import com.liquid.kochanowski.ui.SyncActivity;
import com.liquid.kochanowski.db.DatabaseHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager
{
    public static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DB_WRITE_STARTED = 3;
    public static final int TASK_COMPLETED = 4;

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAX_POOL_SIZE = 8;
    private static int CORE_NUMBER = Runtime.getRuntime ().availableProcessors ();

    private final BlockingQueue <Runnable> downloadQueue;
    private final BlockingQueue <Runnable> dbWriteQueue;
    private final List<ParseTask> parseTaskList;

    private final ThreadPoolExecutor downloadPool;
    private final ThreadPoolExecutor dbWritePool;

    private static ThreadManager instance;

    private static Handler handler;

    private static SyncActivity context;

    private static SQLiteDatabase db;

    private static int currentTimeTable;
    private static int timeTableCount;

    private static int result;

    static
    {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        instance = new ThreadManager ();
    }

    private ThreadManager ()
    {
        downloadQueue = new LinkedBlockingQueue<Runnable> ();
        dbWriteQueue = new LinkedBlockingQueue <Runnable> ();
        //parseTaskQueue = new LinkedBlockingQueue <ParseTask> ();
        parseTaskList = new ArrayList <ParseTask> ();

        db = DatabaseHelper.getWritableDatabase ();

        downloadPool = new ThreadPoolExecutor (
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                downloadQueue
        );

        dbWritePool = new ThreadPoolExecutor (
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                dbWriteQueue
        );

        handler = new Handler (Looper.getMainLooper ())
        {
            @Override
            public void handleMessage (Message msg)
            {
                ParseTask task = (ParseTask) msg.obj;
                String name;
                if (task != null) name = task.getTable ().getLongName () + " (" + task.getTable ().getShortName () + ")";
                else name = "";

                switch (msg.what)
                {
                    case TASK_COMPLETED:
                        currentTimeTable++;
                        context.currentDownload.setText (context.getResources ().getString (R.string.downloaded_timetable) + ": " + name);
                        context.currentCount.setText (currentTimeTable + "/" + timeTableCount);
                        context.progressBar.setProgress (currentTimeTable);

                        result = TASK_COMPLETED;

                        task.recycle ();
                        break;
                    case DOWNLOAD_FAILED:
                        task.recycle ();
                        result = DOWNLOAD_FAILED;
                        context.finishSync ();
                        break;
                }

                if (currentTimeTable == timeTableCount) context.finishSync ();
            }
        };
    }

    public void resetManager ()
    {
        currentTimeTable = 0;
        timeTableCount = 0;
        result = 0;

        parseTaskList.clear ();
        downloadQueue.clear ();
        dbWriteQueue.clear ();
    }

    public static void setTimeTableCount (int timeTableCount)
    {
        ThreadManager.timeTableCount = timeTableCount;
    }

    public static int getResult ()
    {
        return result;
    }

    public static void setContext (Context context)
    {
        ThreadManager.context = (SyncActivity) context;
    }

    public void handleState (ParseTask task, int state)
    {
        switch (state)
        {
            case TASK_COMPLETED:
                Message completeMessage = handler.obtainMessage (state, task);
                completeMessage.sendToTarget ();
                break;
            case DOWNLOAD_COMPLETE:
                dbWritePool.execute (task.getDbWriteRunnable ());
                break;
            default:
                handler.obtainMessage (state, task).sendToTarget ();
                break;
        }
    }

    private void recycleTask (ParseTask task)
    {
        task.recycle ();

        //parseTaskQueue.offer (task);
        parseTaskList.remove (task);
    }

    public static void parseTimeTable (String url)
    {
        ParseTask task = new ParseTask (db);

        try
        {
            task.setUrl (new URL (url));
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace ();
        }

        instance.parseTaskList.add (task);

        instance.downloadPool.execute (task.getDownloadRunnable ());
    }

    public static void cancelAll ()
    {
        synchronized (instance)
        {
            for (ParseTask task : instance.parseTaskList)
            {
                Thread thread = task.getCurrentThread ();

                if (thread != null)
                {
                    thread.interrupt ();
                }
            }
        }
        instance.downloadPool.shutdownNow ();
        instance.dbWritePool.shutdownNow ();

        handler.obtainMessage (DOWNLOAD_FAILED, new ParseTask (db)).sendToTarget ();
    }

    public static ThreadManager getInstance ()
    {
        return instance;
    }
}
