package com.liquid.kochanowski;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CompactHtmlSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by liquid on 02.12.14.
 */
public class MasterlistDownloadTask extends AsyncTask <String, Void, List<String>>
{
    private Context context;
    private SQLiteDatabase db;

    public MasterlistDownloadTask (Context context, SQLiteDatabase db)
    {
        this.context = context;
        this.db = db;
    }

    @Override
    protected List<String> doInBackground (String... params)
    {
        List <String> urls = new ArrayList <String> ();
        String masterlist_url = "";

        try
        {
            URL url = new URL (params[0]);
            InputStream str = url.openStream ();

            int data = str.read ();
            while (data != -1)
            {
                masterlist_url += (char) data;
                data = str.read ();
            }
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace ();
        }

        HtmlCleaner cleaner = new HtmlCleaner ();
        CleanerProperties props = cleaner.getProperties ();
        props.setRecognizeUnicodeChars (true);
        props.setCharset ("utf-8");

        try
        {
            URL url = new URL (masterlist_url);
            URLConnection conn = url.openConnection ();

            TagNode node = cleaner.clean (conn.getInputStream (), "iso-8859-2");
            File file = new File (context.getFilesDir (), "lista.html");

            FileOutputStream ostr = new FileOutputStream (file);
            new CompactHtmlSerializer (props).writeToStream (node, ostr, "utf-8");

            SAXParserFactory factory = SAXParserFactory.newInstance ();
            SAXParser parser = factory.newSAXParser ();

            DefaultHandler handler = new MasterlistHandler (urls, db);
            parser.parse (file, handler);
        }
        catch (java.io.IOException e)
        {
            e.printStackTrace ();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace ();
        }
        catch (SAXException e)
        {
            e.printStackTrace ();
        }

        return urls;
    }

    @Override
    protected void onPostExecute (List<String> strings)
    {

    }
}
