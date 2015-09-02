package com.github.LiquidPL.kochanowski.parse;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public MasterlistHandler (List<String> urls, String school_url, SQLiteDatabase db)
    {
        this.urls = urls;
        this.school_url = school_url;
        this.db = db;
    }

    @Override
    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        for (int i = 0; i < attributes.getLength (); i++)
        {
            if (attributes.getQName (i).equals ("href"))
            {
                if (checkType (attributes.getValue (i)) == Type.CLASS)
                {
                    urls.add (school_url + attributes.getValue (i));
                    Log.i ("liquid", school_url + attributes.getValue (i));
                }
                if (checkType (attributes.getValue (i)) == Type.TEACHER)
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
            /*teacher[teacher.length - 1] = new String (teacher[teacher.length - 1].toCharArray (), 1, teacher[teacher.length - 1].length () - 2);

            if (teacher.length == 4)
            {
                teacher[0] += " " + teacher[1];
                teacher[1] = teacher[2];
            }

            DbWriter.insertTeacher (teacher[teacher.length - 1], teacher[0], teacher[1]);*/

            teacher[1] = teacher[1].substring (1, teacher[1].length () - 1);

            DbWriter.insertTeacher (teacher[1], teacher[0], "");

            currentAttribute = "";
        }
    }

    private int checkType (String url)
    {
        String values[] = url.split ("/");
        int ret = Type.NONE;
        if (values.length != 2) return Type.NONE;
        switch (values[1].charAt (0))
        {
            case 'o':
                ret = Type.CLASS;
                break;
            case 'n':
                ret = Type.TEACHER;
                break;
            case 's':
                ret = Type.CLASSROOM;
                break;
        }
        return ret;
    }
}
