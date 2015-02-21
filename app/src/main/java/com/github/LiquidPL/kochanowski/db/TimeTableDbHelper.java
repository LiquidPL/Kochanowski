package com.github.LiquidPL.kochanowski.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.LiquidPL.kochanowski.util.PrefUtils;

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

    /*private static final String SQL_CREATE_TABLE_HOURS =
            "CREATE TABLE " + TimeTableContract.HourTable.TABLE_NAME + " (" +
                    TimeTableContract.HourTable._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.HourTable.COLUMN_NAME_START_TIME + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.HourTable.COLUMN_NAME_END_TIME + TEXT_TYPE + ");";*/

    private static final String SQL_CREATE_TABLE_LESSONS =
            "CREATE TABLE " + TimeTableContract.LessonTable.TABLE_NAME + " (" +
                    TimeTableContract.LessonTable._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_DAY + INT_TYPE + COMMA_SEP +
//                    TimeTableContract.LessonTable.COLUMN_NAME_HOUR_ID + INT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_START_TIME + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_END_TIME + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_SUBJECT_ID + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_TEACHER_CODE + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_CLASSROOM + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_CLASS_NAME_SHORT + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.LessonTable.COLUMN_NAME_GROUP_ID + INT_TYPE + ");";

    private static final String SQL_CREATE_TABLE_SUBJECTS =
            "CREATE TABLE " + TimeTableContract.SubjectTable.TABLE_NAME + " (" +
                    TimeTableContract.SubjectTable._ID + INT_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    TimeTableContract.SubjectTable.COLUMN_NAME_SUBJECT_NAME + TEXT_TYPE + COMMA_SEP +
                    TimeTableContract.SubjectTable.COLUMN_NAME_SUBJECT_COLOR + INT_TYPE + ");";

    private static final String SQL_DELETE_TABLE_TEACHERS =
            "DROP TABLE IF EXISTS " + TimeTableContract.TeacherTable.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_CLASSES =
            "DROP TABLE IF EXISTS " + TimeTableContract.ClassTable.TABLE_NAME;

    /*private static final String SQL_DELETE_TABLE_HOURS =
            "DROP TABLE IF EXISTS " + TimeTableContract.HourTable.TABLE_NAME;*/

    private static final String SQL_DELETE_TABLE_LESSONS =
            "DROP TABLE IF EXISTS " + TimeTableContract.LessonTable.TABLE_NAME;

    private static final String SQL_DELETE_TABLE_SUBJECTS =
            "DROP TABLE IF EXISTS " + TimeTableContract.SubjectTable.TABLE_NAME;

    private final Context context;

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "timetables.db";

    public TimeTableDbHelper (Context context)
    {
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate (SQLiteDatabase db)
    {
        createTables (db);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        dropTables (db);
        createTables (db);

        // setting the upgraded flag so the user is notified of the redownload requirement
        PrefUtils.setDatabaseUpgradeStatus (context, true);

        // setting the timetables synced flag to false
        PrefUtils.setTimeTablesSynced (context, false);
    }

    public void createTables (SQLiteDatabase db)
    {
        db.execSQL (SQL_CREATE_TABLE_TEACHERS);
        db.execSQL (SQL_CREATE_TABLE_CLASSES);
//        db.execSQL (SQL_CREATE_TABLE_HOURS);
        db.execSQL (SQL_CREATE_TABLE_LESSONS);
        db.execSQL (SQL_CREATE_TABLE_SUBJECTS);
    }

    public void dropTables (SQLiteDatabase db)
    {
        db.execSQL (SQL_DELETE_TABLE_TEACHERS);
        db.execSQL (SQL_DELETE_TABLE_CLASSES);
//        db.execSQL (SQL_DELETE_TABLE_HOURS);
        db.execSQL (SQL_DELETE_TABLE_LESSONS);
        db.execSQL (SQL_DELETE_TABLE_SUBJECTS);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade (db, oldVersion, newVersion);
    }
}
