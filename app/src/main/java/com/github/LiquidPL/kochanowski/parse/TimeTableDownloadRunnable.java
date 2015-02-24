package com.github.LiquidPL.kochanowski.parse;

import android.os.Process;

import com.github.LiquidPL.kochanowski.parse.handler.VulcanHandler;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CompactHtmlSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by liquid on 24.02.15.
 */
public class TimeTableDownloadRunnable
    implements Runnable
{
    interface DownloadRunnableMethods
    {
        void handleState (TimeTableDownloadRunnable runnable, int state);
    }

    private URL url;

    private String tableName;
    private Thread currentThread;

    private ThreadManager manager = ThreadManager.getInstance ();

    public TimeTableDownloadRunnable (URL url)
    {
        this.url = url;

        currentThread = Thread.currentThread ();
    }

    @Override
    public void run ()
    {
        manager.handleState (this, ThreadManager.TASK_STARTED);

        android.os.Process.setThreadPriority (Process.THREAD_PRIORITY_BACKGROUND);

        // setting up HtmlCleaner
        HtmlCleaner cleaner = new HtmlCleaner ();
        CleanerProperties props = cleaner.getProperties ();
        props.setRecognizeUnicodeChars (true);
        props.setCharset ("utf-8");

        try
        {
            if (Thread.interrupted ())
            {
                throw new InterruptedException ();
            }

            try
            {
                URLConnection connection = url.openConnection ();

                manager.handleState (this, ThreadManager.TASK_STARTED);

                TagNode node = cleaner.clean (connection.getInputStream (), "iso-8859-2");

                ByteArrayOutputStream ostr = new ByteArrayOutputStream ();
                new CompactHtmlSerializer (props).writeToStream (node, ostr, "utf-8");
                ByteArrayInputStream istr = new ByteArrayInputStream (ostr.toByteArray ());

                if (Thread.interrupted ())
                {
                    throw new InterruptedException ();
                }

                SAXParserFactory factory = SAXParserFactory.newInstance ();
                SAXParser parser = factory.newSAXParser ();

                DefaultHandler handler = new VulcanHandler (this, getTableType (url));
                parser.parse (istr, handler);
            }
            catch (IOException e)
            {
                manager.handleState (this, ThreadManager.TASK_FAILED);
                e.printStackTrace ();
            }
            catch (ParserConfigurationException e)
            {
                manager.handleState (this, ThreadManager.TASK_FAILED);
                e.printStackTrace ();
            }
            catch (SAXException e)
            {
                manager.handleState (this, ThreadManager.TASK_FAILED);
                e.printStackTrace ();
            }

            manager.handleState (this, ThreadManager.TASK_COMPLETED);
        }
        catch (InterruptedException e)
        {
            manager.handleState (this, ThreadManager.TASK_FAILED);
        }
        finally
        {
            currentThread = null;
            Thread.interrupted ();
        }
    }

    private int getTableType (URL url)
    {
        String path = url.getPath ();;

        String[] values = path.split ("/");
        String filename = values[values.length - 1];

        switch (filename.charAt (0))
        {
            case 'o':
                return Type.CLASS;
            case 'n':
                return Type.CLASSROOM;
            case 's':
                return Type.TEACHER;
        }

        return -1;
    }

    public String getTableName ()
    {
        return tableName;
    }

    public void setTableName (String tableName)
    {
        this.tableName = tableName;
    }

    public Thread getCurrentThread ()
    {
        return currentThread;
    }
}
