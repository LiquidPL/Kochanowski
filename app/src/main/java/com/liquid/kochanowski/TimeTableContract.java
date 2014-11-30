package com.liquid.kochanowski;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.liquid.kochanparser.Time;

import java.io.InputStreamReader;

public final class TimeTableContract
{
    public static abstract class TeacherTable implements BaseColumns
    {
        public static final String TABLE_NAME = "teachers";
        public static final String COLUMN_NAME_TEACHER_CODE = "code";
        public static final String COLUMN_NAME_TEACHER_NAME = "name";
        public static final String COLUMN_NAME_TEACHER_SURNAME = "surname";
    }

    public static abstract class ClassTable implements BaseColumns
    {
        public static final String TABLE_NAME = "classes";
        public static final String COLUMN_NAME_NAME_SHORT = "shortname";
        public static final String COLUMN_NAME_NAME_LONG = "longname";
    }

    public static abstract class DaysTable implements BaseColumns
    {
        public static final String TABLE_NAME = "days";
        public static final String COLUMN_NAME_DAY_NAME = "'name'";
    }

    public static abstract class HoursTable implements BaseColumns
    {
        public static final String TABLE_NAME = "hours";
        public static final String COLUMN_NAME_START_HOUR = "starthour";
        public static final String COLUMN_NAME_START_MINUTE = "startminute";
        public static final String COLUMN_NAME_END_HOUR = "endhour";
        public static final String COLUMN_NAME_END_MINUTE = "endminute";
    }

    public static abstract class LessonTable implements BaseColumns
    {
        public static final String TABLE_NAME = "lessons";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_HOUR_ID = "hourid";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_TEACHER_CODE = "code";
        public static final String COLUMN_NAME_CLASSROOM = "classroom";
        public static final String COLUMN_NAME_CLASS_NAME_SHORT = "shortname";
        public static final String COLUMN_NAME_GROUP_ID = "groupid";
    }
}
