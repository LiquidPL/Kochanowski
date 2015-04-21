package com.github.LiquidPL.kochanowski.db;

import android.provider.BaseColumns;

public final class TimeTableContract
{
    public static abstract class TeacherTable implements BaseColumns
    {
        public static final String TABLE_NAME = "teachers";
        public static final String COLUMN_NAME_TEACHER_ID = "teacherid";
        public static final String COLUMN_NAME_TEACHER_NAME = "teachername";
        public static final String COLUMN_NAME_TEACHER_SURNAME = "teachersurname";
    }

    public static abstract class ClassTable implements BaseColumns
    {
        public static final String TABLE_NAME = "classes";
        public static final String COLUMN_NAME_NAME_SHORT = "shortname";
        public static final String COLUMN_NAME_NAME_LONG = "longname";
    }

    public static abstract class LessonTable implements BaseColumns
    {
        public static final String TABLE_NAME = "lessons";
        public static final String COLUMN_NAME_DAY_ID = "dayid";
        public static final String COLUMN_NAME_HOUR_ID = "hourid";
        public static final String COLUMN_NAME_START_TIME = "starttime";
        public static final String COLUMN_NAME_END_TIME = "endtime";
        public static final String COLUMN_NAME_SUBJECT_ID = "subjectid";
        public static final String COLUMN_NAME_TEACHER_ID = "teacherid";
        public static final String COLUMN_NAME_CLASSROOM_NAME = "classroomname";
        public static final String COLUMN_NAME_CLASS_NAME_SHORT = "shortname";
        public static final String COLUMN_NAME_GROUP_ID = "groupid";
    }

    public static abstract class SubjectTable implements BaseColumns
    {
        public static final String TABLE_NAME= "subjects";
        public static final String COLUMN_NAME_SUBJECT_NAME = "subjectname";
        public static final String COLUMN_NAME_SUBJECT_COLOR = "subjectcolor";
    }
}
