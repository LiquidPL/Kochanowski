package com.github.LiquidPL.kochanowski.parse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.github.LiquidPL.kochanowski.ui.SyncActivity;
import com.github.LiquidPL.kochanowski.util.DbUtils;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CompactHtmlSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MasterlistDownloadRunnable implements Runnable
{
    private List <String> urls;
    private SQLiteDatabase db;
    private SyncActivity context;
    private Handler handler;

    String header_url[];

    public MasterlistDownloadRunnable (List<String> urls, Context context, Handler handler)
    {
        this.urls = urls;
        this.context = (SyncActivity) context;
        this.handler = handler;
    }

    @Override
    public void run ()
    {
        String masterlist_url = "";

        try
        {
            URL url = new URL ("http://liquidpl.github.io/Kochanowski/masterlist");
            InputStream istr = url.openStream ();

            int data = istr.read ();
            while (data != -1)
            {
                masterlist_url += (char) data;
                data = istr.read ();
            }

            header_url = masterlist_url.split ("lista.html");
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

            ByteArrayOutputStream ostr = new ByteArrayOutputStream ();
            new CompactHtmlSerializer (props).writeToStream (node, ostr, "utf-8");
            ByteArrayInputStream istr = new ByteArrayInputStream (ostr.toByteArray ());

            SAXParserFactory factory = SAXParserFactory.newInstance ();
            SAXParser parser = factory.newSAXParser ();

            // opening the database for the handler to write in
            SQLiteDatabase db = DbUtils.getInstance ().openDatabase ();

            DefaultHandler handler = new MasterlistHandler (urls, header_url[0], db);
            parser.parse (istr, handler);

            // closing the database as we're done with parsing
            DbUtils.getInstance ().closeDatabase ();
        }
        catch (java.io.IOException | SAXException | ParserConfigurationException e)
        {
            e.printStackTrace ();
        }

        handler.post (new Runnable ()
        {
            @Override
            public void run ()
            {
                context.beginSync (urls);
            }
        });
    }
}
