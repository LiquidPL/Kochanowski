package com.liquid.kochanowski;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.liquid.kochanparser.TimeTableType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class MasterlistHandler extends DefaultHandler
{
    private List <String> urls = null;
    private SQLiteDatabase db;

    private String currentName = "";
    private String currentAttribute = "";

    public MasterlistHandler (List<String> urls, SQLiteDatabase db)
    {
        this.urls = urls;
        this.db = db;
    }

    @Override
    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        TimeTableType attrValue = TimeTableType.TIMETABLE_TYPE_NONE;
        for (int i = 0; i < attributes.getLength (); i++)
        {
            if (attributes.getQName (i) == "href")
            {
                if (checkType (attributes.getValue (i)) == TimeTableType.TIMETABLE_TYPE_CLASS)
                {
                    urls.add (attributes.getValue (i));
                }
                if (checkType (attributes.getValue (i)) == TimeTableType.TIMETABLE_TYPE_TEACHER)
                {
                    currentAttribute = "teacher";
                }
            }
        }
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException
    {
        String value = new String (ch, start, length).trim ();
        if (value.length () == 0) return;

        if (currentAttribute == "teacher")
        {
            String[] teacher = value.split (" ");
            teacher[2] = new String (teacher[2].toCharArray (), 1, teacher[2].length () - 1);

            ContentValues values = new ContentValues ();
            values.put (TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_CODE, teacher[2]);
            values.put (TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_NAME, teacher[0]);
            values.put (TimeTableContract.TeacherTable.COLUMN_NAME_TEACHER_SURNAME, teacher[1]);

            db.insert (TimeTableContract.TeacherTable.TABLE_NAME, null, values);
            currentAttribute = "";
        }
    }

    private TimeTableType checkType (String url)
    {
        String values[] = url.split ("/");
        TimeTableType ret = TimeTableType.TIMETABLE_TYPE_NONE;
        if (values.length != 2) return TimeTableType.TIMETABLE_TYPE_NONE;
        switch (values[1].charAt (0))
        {
            case 'o':
                ret = TimeTableType.TIMETABLE_TYPE_CLASS;
                break;
            case 'n':
                ret = TimeTableType.TIMETABLE_TYPE_TEACHER;
                break;
            case 's':
                ret = TimeTableType.TIMETABLE_TYPE_CLASSROOM;
                break;
        }
        return ret;
    }
}
