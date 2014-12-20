package com.liquid.kochanowski;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimeTableDbHelper extends SQLiteOpenHelper
{
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_TABLE_TEACHERS =
            "CREATE TABLE " + TimeTableContract.TeacherTable.TABLE_NAME + " (" +
                    TimeTableContract.TeacherTable._ID + INT_TYPE + COMMA_SEP +
                    TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_CODE + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_NAME + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_SURNAME + TEXT_TYPE + ");";

    private static final String SQL_CREATE_TABLE_CLASSES =
            "CREATE TABLE " + TimeTableContract.ClassTable.TABLE_NAME + " (" +
                    TimeTableContract.ClassTable._ID + INT_TYPE + COMMA_SEP +
                    TimeTableContract.ClassTable.COLUMN_NAME_NAME_SHORT + TEXT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.ClassTable.COLUMN_NAME_NAME_LONG + TEXT_TYPE + ");";

    /*private static final String SQL_CREATE_TABLE_DAYS =
            "CREATE TABLE " + TimeTableContract.DaysTable.TABLE_NAME + " (" +
                    TimeTableContract.DaysTable._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.DaysTable.COLUMN_NAME_DAY_NAME + TEXT_TYPE + ");";*/

    private static final String SQL_CREATE_TABLE_HOURS =
            "CREATE TABLE " + TimeTableContract.HourTable.TABLE_NAME + " (" +
                    TimeTableContract.HourTable._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.HourTable.COLUMN_NAME_START_HOUR + INT_TYPE + COMMA_SEP +
                    TimeTableContract.HourTable.COLUMN_NAME_START_MINUTE + INT_TYPE + COMMA_SEP +
                    TimeTableContract.HourTable.COLUMN_NAME_END_HOUR + INT_TYPE + COMMA_SEP +
                    TimeTableContract.HourTable.COLUMN_NAME_END_MINUTE + INT_TYPE + ");";

    private static final String SQL_CREATE_TABLE_LESSONS =
            "CREATE TABLE " + TimeTableContract.LessonTable.TABLE_NAME + " (" +
                    TimeTableContract.LessonTable._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_DAY + INT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_HOUR_ID + INT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_SUBJECT + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_TEACHER_CODE + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_CLASSROOM + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_CLASS_NAME_SHORT + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_GROUP_ID + INT_TYPE + ");";

    private static final String SQL_DELETE_TABLES =
            "DROP TABLE IF EXISTS " + TimeTableContract.TeacherTable.TABLE_NAME +
                    "; DROP TABLE IF EXISTS " + TimeTableContract.ClassTable.TABLE_NAME +
                    "; DROP TABLE IF EXISTS " + TimeTableContract.HourTable.TABLE_NAME +
                    "; DROP TABLE IF EXISTS " + TimeTableContract.LessonTable.TABLE_NAME + ";";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "timetables.db";

    public TimeTableDbHelper (Context context)
    {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db)
    {
        db.execSQL (SQL_CREATE_TABLE_TEACHERS);
        db.execSQL (SQL_CREATE_TABLE_CLASSES);
        //db.execSQL (SQL_CREATE_TABLE_DAYS);
        db.execSQL (SQL_CREATE_TABLE_HOURS);
        db.execSQL (SQL_CREATE_TABLE_LESSONS);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL (SQL_DELETE_TABLES);
        onCreate (db);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade (db, oldVersion, newVersion);
    }
}
