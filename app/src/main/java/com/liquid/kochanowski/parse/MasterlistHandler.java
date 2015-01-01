package com.liquid.kochanowski.parse;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.liquid.kochanowski.db.TimeTableContract.TeacherTable;
import com.liquid.kochanowski.util.DbUtils;
import com.liquid.kochanparser.TimeTableType;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class MasterlistHandler extends DefaultHandler
{
    private String school_url;

    private List <String> urls;
    private SQLiteDatabase db;

    private String currentName = "";
    private String currentAttribute = "";

    public MasterlistHandler (List<String> urls, String school_url)
    {
        this.urls = urls;
        this.school_url = school_url;
        db = DbUtils.getWritableDatabase ();
    }

    @Override
    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        for (int i = 0; i < attributes.getLength (); i++)
        {
            if (attributes.getQName (i).equals ("href"))
            {
                if (checkType (attributes.getValue (i)) == TimeTableType.CLASS)
                {
                    urls.add (school_url + attributes.getValue (i));
                }
                if (checkType (attributes.getValue (i)) == TimeTableType.TEACHER)
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

        if (currentAttribute.equals ("teacher"))
        {
            String[] teacher = value.split (" ");
            teacher[teacher.length - 1] = new String (teacher[teacher.length - 1].toCharArray (), 1, teacher[teacher.length - 1].length () - 2);

            if (teacher.length == 4)
            {
                teacher[0] += " " + teacher[1];
                teacher[1] = teacher[2];
            }

            ContentValues values = new ContentValues ();
            values.put (TeacherTable.COLUMN_NAME_TEACHER_CODE, teacher[teacher.length - 1]);
            values.put (TeacherTable.COLUMN_NAME_TEACHER_NAME, teacher[0]);
            values.put (TeacherTable.COLUMN_NAME_TEACHER_SURNAME, teacher[1]);

            db.insert (TeacherTable.TABLE_NAME, null, values);
            currentAttribute = "";
        }
    }

    private int checkType (String url)
    {
        String values[] = url.split ("/");
        int ret = TimeTableType.NONE;
        if (values.length != 2) return TimeTableType.NONE;
        switch (values[1].charAt (0))
        {
            case 'o':
                ret = TimeTableType.CLASS;
                break;
            case 'n':
                ret = TimeTableType.TEACHER;
                break;
            case 's':
                ret = TimeTableType.CLASSROOM;
                break;
        }
        return ret;
    }
}
