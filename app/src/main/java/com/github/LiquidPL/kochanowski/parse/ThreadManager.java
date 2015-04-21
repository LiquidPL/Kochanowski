package com.github.LiquidPL.kochanowski.parse;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.github.LiquidPL.kochanowski.R;
import com.github.LiquidPL.kochanowski.ui.SyncActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager
    implements TimeTableDownloadRunnable.DownloadRunnableMethods
{
    public static final int TASK_FAILED = -1;
    public static final int TASK_STARTED = 1;
    public static final int TASK_COMPLETED = 2;

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAX_POOL_SIZE = 8;
    private static final int CORE_NUMBER = Runtime.getRuntime ().availableProcessors ();

    private final BlockingQueue<Runnable> taskQueue;
    private final ThreadPoolExecutor taskPool;

    private static ThreadManager instance;
    private Handler handler;

    private SyncActivity activity;

    private static int currentTimetable = 0;
    private static int timetableCount;

    private static int result;

    static
    {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        instance = new ThreadManager ();
    }

    ThreadManager ()
    {
        taskQueue = new LinkedBlockingQueue<> ();

        taskPool = new ThreadPoolExecutor (
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                taskQueue
        );

        handler = new Handler (Looper.getMainLooper ())
        {
            @Override
            public void handleMessage (Message msg)
            {
                TimeTableDownloadRunnable runnable = ((TimeTableDownloadRunnable) msg.obj);

                String name = "";
                if (runnable != null) name = runnable.getTableName ();

                switch (msg.what)
                {
                    case TASK_COMPLETED:
                        currentTimetable++;
                        activity.currentDownload.setText (activity.getResources ().getString (R.string.downloaded_timetable) +
                                                                  " " + name);
                        activity.currentCount.setText (currentTimetable + "/" + timetableCount);
                        activity.progressBar.setProgress (currentTimetable);

                        if (currentTimetable == timetableCount)
                        {
                            result = TASK_COMPLETED;
                        }

                        if (runnable != null)
                        {
                            runnable = null;
                        }
                        break;
                    case TASK_FAILED:
                        if (runnable != null)
                        {
                            runnable = null;
                        }
                        result = TASK_FAILED;
                        activity.finishSync ();
                        break;
                }

                if (currentTimetable == timetableCount)
                {
                    activity.finishSync ();
                }
            }
        };
    }

    public static void parseTimetable (String url)
    {
        try
        {
            TimeTableDownloadRunnable downloadRunnable = new TimeTableDownloadRunnable (new URL (url));

            instance.taskPool.execute (downloadRunnable);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace ();
        }
    }

    public static void cancelAll ()
    {
        synchronized (instance)
        {
            for (Runnable runnable : instance.taskQueue)
            {
                Thread thread = ((TimeTableDownloadRunnable) runnable).getCurrentThread ();

                if (thread != null)
                {
                    thread.interrupt ();
                }
            }

            instance.taskPool.shutdownNow ();
        }
    }

    @Override
    public void handleState (TimeTableDownloadRunnable runnable, int state)
    {
        switch (state)
        {
            case TASK_COMPLETED:
                handler.obtainMessage (state, runnable).sendToTarget ();
                break;
            default:
                handler.obtainMessage (state, runnable).sendToTarget ();
                break;
        }
    }

    public static void resetManager ()
    {
        timetableCount = 0;
        currentTimetable = 0;
        result = 0;

        instance.taskQueue.clear ();
    }

    public void setContext (SyncActivity activity)
    {
        this.activity = activity;
    }

    public static void setTimetableCount (int timetableCount)
    {
        ThreadManager.timetableCount = timetableCount;
    }

    public static int getTimetableCount ()
    {
        return timetableCount;
    }

    public static int getResult ()
    {
        return result;
    }

    public static ThreadManager getInstance ()
    {
        return instance;
    }
}
