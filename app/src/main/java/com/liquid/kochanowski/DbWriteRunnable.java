package com.liquid.kochanowski;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;

import com.liquid.kochanowski.TimeTableContract.ClassTable;
import com.liquid.kochanowski.TimeTableContract.HourTable;
import com.liquid.kochanowski.TimeTableContract.LessonTable;
import com.liquid.kochanparser.Lesson;
import com.liquid.kochanparser.TimeTable;

/**
 * Created by liquid on 03.12.14.
 */
public class DbWriteRunnable implements Runnable
{
    static final int WRITE_FAILED = -1;
    static final int WRITE_STARTED = 0;
    static final int WRITE_COMPLETED = 1;

    private ParseTask task;

    private TimeTable table;
    private SQLiteDatabase db;

    private ContentValues values;

    interface DbWriteRunnableMethods
    {
        void setDbWriteThread (Thread currentThread);

        void handleDbWriteState (int state);
    }

    public DbWriteRunnable (ParseTask task)
    {
        this.task = task;
        this.db = task.getDb ();
        this.table = task.getTable ();
    }

    @Override
    public void run ()
    {
        task.setDbWriteThread (Thread.currentThread ());

        android.os.Process.setThreadPriority (Process.THREAD_PRIORITY_BACKGROUND);

        task.handleDbWriteState (WRITE_STARTED);

        try
        {
            if (Thread.interrupted ())
            {
                throw new InterruptedException ();
            }

            for (Lesson l : table.getLessons ())
            {
                values = new ContentValues ();

                values.put (LessonTable.COLUMN_NAME_DAY, l.getDay ());
                values.put (LessonTable.COLUMN_NAME_HOUR_ID, l.getLesson ());
                values.put (LessonTable.COLUMN_NAME_SUBJECT, l.getSubject ());
                values.put (LessonTable.COLUMN_NAME_TEACHER_CODE, l.getTeacherCode ());
                values.put (LessonTable.COLUMN_NAME_CLASSROOM, l.getClassroom ());
                values.put (LessonTable.COLUMN_NAME_CLASS_NAME_SHORT, table.getShortName ());

                db.insert (LessonTable.TABLE_NAME, null, values);
            }

            if (Thread.interrupted ())
            {
                throw new InterruptedException ();
            }

            for (int i = 0; i < table.getStarthours ().size (); i++)
            {
                Cursor c = db.rawQuery ("SELECT * FROM " + HourTable.TABLE_NAME +
                        " WHERE " + HourTable._ID + "=" + i, null);

                if (c.getCount () != 0) continue;

                values = new ContentValues ();

                values.put (HourTable._ID, i);
                values.put (HourTable.COLUMN_NAME_START_HOUR, table.getStarthours ().get (i).getHour ());
                values.put (HourTable.COLUMN_NAME_START_MINUTE, table.getStarthours ().get (i).getMinute ());
                values.put (HourTable.COLUMN_NAME_END_HOUR, table.getEndhours ().get (i).getHour ());
                values.put (HourTable.COLUMN_NAME_END_MINUTE, table.getEndhours ().get (i).getMinute ());

                db.insert (HourTable.TABLE_NAME, null, values);
            }

            values = new ContentValues ();
            values.put (ClassTable.COLUMN_NAME_NAME_SHORT, table.getShortName ());
            values.put (ClassTable.COLUMN_NAME_NAME_LONG, table.getLongName ());

            db.insert (ClassTable.TABLE_NAME, null, values);
        }
        catch (InterruptedException e)
        {
            // do nothing
        }
        catch (SQLiteException e)
        {
            task.handleDbWriteState (WRITE_FAILED);
            e.printStackTrace ();
        }

        task.handleDbWriteState (WRITE_COMPLETED);
    }
}
