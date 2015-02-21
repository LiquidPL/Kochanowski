package com.github.LiquidPL.kochanowski.parse;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Process;

import com.github.LiquidPL.kochanowski.db.TimeTableContract;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.ClassTable;
import com.github.LiquidPL.kochanowski.db.TimeTableContract.LessonTable;
import com.github.LiquidPL.kochanowski.parse.table.Lesson;
import com.github.LiquidPL.kochanowski.parse.table.TimeTable;

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
                values.put (LessonTable.COLUMN_NAME_START_TIME, table.getStarttimes ().get (l.getLesson ()));
                values.put (LessonTable.COLUMN_NAME_END_TIME, table.getEndtimes ().get (l.getLesson ()));
//                values.put (LessonTable.COLUMN_NAME_HOUR_ID, l.getLesson ());
                values.put (LessonTable.COLUMN_NAME_SUBJECT_ID, getSubjectId (l.getSubject ()));
                values.put (LessonTable.COLUMN_NAME_TEACHER_CODE, l.getTeacherCode ());
                values.put (LessonTable.COLUMN_NAME_CLASSROOM, l.getClassroom ());
                values.put (LessonTable.COLUMN_NAME_GROUP_ID, l.getGroup ());
                values.put (LessonTable.COLUMN_NAME_CLASS_NAME_SHORT, table.getShortName ());

                db.insert (LessonTable.TABLE_NAME, null, values);

                if (Thread.interrupted ())
                {
                    throw new InterruptedException ();
                }
            }

            /*for (int i = 0; i < table.getStarttimes ().size (); i++)
            {
                Cursor cur = db.rawQuery ("SELECT * FROM " + HourTable.TABLE_NAME +
                        " WHERE " + HourTable._ID + "=" + i, null);

                if (cur.getCount () != 0) continue;

                values = new ContentValues ();
                values.put (HourTable._ID, i);
                values.put (HourTable.COLUMN_NAME_START_TIME, table.getStarttimes ().get (i));
                values.put (HourTable.COLUMN_NAME_END_TIME, table.getEndtimes ().get (i));

                db.insert (HourTable.TABLE_NAME, null, values);

                if (Thread.interrupted ())
                {
                    throw new InterruptedException ();
                }
            }*/

            values = new ContentValues ();
            values.put (ClassTable.COLUMN_NAME_NAME_SHORT, table.getShortName ());
            values.put (ClassTable.COLUMN_NAME_NAME_LONG, table.getLongName ());

            db.insert (ClassTable.TABLE_NAME, null, values);

            if (Thread.interrupted ())
            {
                throw new InterruptedException ();
            }
        }
        catch (InterruptedException e)
        {
            return;
        }
        catch (SQLiteException e)
        {
            task.handleDbWriteState (WRITE_FAILED);
            e.printStackTrace ();
        }

        task.handleDbWriteState (WRITE_COMPLETED);
    }

    public int getSubjectId (String subject)
    {
        Integer subjectId = task.manager.subjects.get (subject);

        if (subjectId == null)
        {
            subjectId = task.manager.subjects.size () + 1;
            task.manager.subjects.put (subject, subjectId);

            ContentValues values = new ContentValues ();
            values.put (TimeTableContract.SubjectTable._ID, subjectId);
            values.put (TimeTableContract.SubjectTable.COLUMN_NAME_SUBJECT_NAME, subject);
            db.insert (TimeTableContract.SubjectTable.TABLE_NAME, null, values);
        }

        return subjectId;
    }
}
