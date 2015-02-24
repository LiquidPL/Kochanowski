package com.github.LiquidPL.kochanowski.parse;

import com.github.LiquidPL.kochanowski.db.TimeTableContract.*;
import com.github.LiquidPL.kochanowski.util.DbUtils;

import android.content.ContentValues;
import android.util.Log;

import java.util.TreeMap;

/**
 * Created by liquid on 23.02.15.
 */
public class DbWriter
{
    private static TreeMap <String, Integer> subjects;

    public static void insertLesson (int day, String startTime, String endTime, int hourId, int groupId, String subjectName, String teacherCode, String classroom, String className)
    {
        ContentValues values = new ContentValues ();

        values.put (LessonTable.COLUMN_NAME_DAY, day);
        values.put (LessonTable.COLUMN_NAME_START_TIME, startTime);
        values.put (LessonTable.COLUMN_NAME_END_TIME, endTime);
        values.put (LessonTable.COLUMN_NAME_HOUR_ID, hourId);
        values.put (LessonTable.COLUMN_NAME_GROUP_ID, groupId);
        values.put (LessonTable.COLUMN_NAME_SUBJECT_ID, getSubjectId (subjectName));
        values.put (LessonTable.COLUMN_NAME_TEACHER_CODE, teacherCode);
        values.put (LessonTable.COLUMN_NAME_CLASSROOM, classroom);
        values.put (LessonTable.COLUMN_NAME_CLASS_NAME_SHORT, className);

        DbUtils.getWritableDatabase ().insert (LessonTable.TABLE_NAME, null, values);
    }

    public static void insertClass (String shortName, String longName)
    {
        ContentValues values = new ContentValues ();

        values.put (ClassTable.COLUMN_NAME_NAME_SHORT, shortName);
        values.put (ClassTable.COLUMN_NAME_NAME_LONG, longName);

        DbUtils.getWritableDatabase ().insert (ClassTable.TABLE_NAME, null, values);
    }

    public static void insertTeacher (String teacherCode, String name, String surname)
    {
        ContentValues values = new ContentValues ();

        values.put (TeacherTable.COLUMN_NAME_TEACHER_CODE, teacherCode);
        values.put (TeacherTable.COLUMN_NAME_TEACHER_NAME, name);
        values.put (TeacherTable.COLUMN_NAME_TEACHER_SURNAME, surname);

        DbUtils.getWritableDatabase ().insert (TeacherTable.TABLE_NAME, null, values);
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

            DbUtils.getWritableDatabase ().insert (SubjectTable.TABLE_NAME, null, values);
        }

        return subjectId;
    }
}
