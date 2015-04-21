package com.github.LiquidPL.kochanowski.parse;

import com.github.LiquidPL.kochanowski.db.TimeTableContract.*;
import com.github.LiquidPL.kochanowski.util.DbUtils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.TreeMap;

public class DbWriter
{
    private static TreeMap <String, Integer> subjects;

    public static void insertLesson (int day, String startTime, String endTime, int hourId, int groupId, String subjectName, String teacherCode, String classroom, String className)
    {
        ContentValues values = new ContentValues ();

        values.put (LessonTable.COLUMN_NAME_DAY_ID, day);
        values.put (LessonTable.COLUMN_NAME_START_TIME, startTime);
        values.put (LessonTable.COLUMN_NAME_END_TIME, endTime);
        values.put (LessonTable.COLUMN_NAME_HOUR_ID, hourId);
        values.put (LessonTable.COLUMN_NAME_GROUP_ID, groupId);
        values.put (LessonTable.COLUMN_NAME_SUBJECT_ID, getSubjectId (subjectName));
        values.put (LessonTable.COLUMN_NAME_TEACHER_ID, teacherCode);
        values.put (LessonTable.COLUMN_NAME_CLASSROOM_NAME, classroom);
        values.put (LessonTable.COLUMN_NAME_CLASS_NAME_SHORT, className);

        SQLiteDatabase db = DbUtils.getInstance ().openDatabase ();
        db.insert (LessonTable.TABLE_NAME, null, values);
        DbUtils.getInstance ().closeDatabase ();
    }

    public static void insertClass (String shortName, String longName)
    {
        ContentValues values = new ContentValues ();

        values.put (ClassTable.COLUMN_NAME_NAME_SHORT, shortName);
        values.put (ClassTable.COLUMN_NAME_NAME_LONG, longName);

        SQLiteDatabase db = DbUtils.getInstance ().openDatabase ();
        db.insert (ClassTable.TABLE_NAME, null, values);
        DbUtils.getInstance ().closeDatabase ();
    }

    public static void insertTeacher (String teacherCode, String name, String surname)
    {
        ContentValues values = new ContentValues ();

        values.put (TeacherTable.COLUMN_NAME_TEACHER_ID, teacherCode);
        values.put (TeacherTable.COLUMN_NAME_TEACHER_NAME, name);
        values.put (TeacherTable.COLUMN_NAME_TEACHER_SURNAME, surname);

        SQLiteDatabase db = DbUtils.getInstance ().openDatabase ();
        db.insert (TeacherTable.TABLE_NAME, null, values);
        DbUtils.getInstance ().closeDatabase ();
    }

    private static int getSubjectId (String subjectName)
    {
        if (subjects == null)
        {
            subjects = new TreeMap<> ();
        }

        Integer subjectId = subjects.get (subjectName);

        if (subjectId == null)
        {
            subjects.put (subjectName, subjects.size () + 1);
            subjectId = subjects.size ();

            ContentValues values = new ContentValues ();

            values.put (SubjectTable._ID, subjects.size ());
            values.put (SubjectTable.COLUMN_NAME_SUBJECT_NAME, subjectName);

            SQLiteDatabase db = DbUtils.getInstance ().openDatabase ();
            db.insert (SubjectTable.TABLE_NAME, null, values);
            DbUtils.getInstance ().closeDatabase ();
        }

        return subjectId;
    }
}
