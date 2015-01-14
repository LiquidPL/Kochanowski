package com.github.LiquidPL.kochanowski.parse;

import com.github.LiquidPL.kochanowski.parse.table.TimeTable;
import com.github.LiquidPL.kochanowski.parse.table.TimeTableType;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CompactHtmlSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by liquid on 03.12.14.
 */
public class TimeTableDownloadRunnable implements Runnable
{
    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 0;
    static final int DOWNLOAD_COMPLETED = 1;

    public final ParseTask task;

    private TimeTable table;

    interface DownloadRunnableMethods
    {
        void setDownloadThread (Thread currentThread);

        void handleDownloadState (int state);
    }

    public TimeTableDownloadRunnable (ParseTask task)
    {
        this.task = task;
        this.table = task.getTable ();
    }

    @Override
    public void run ()
    {
        task.setDownloadThread (Thread.currentThread ());

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        // Setting up the HtmlCleaner for cleaning up the sad HTML
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
                URLConnection conn = task.getUrl ().openConnection ();

                task.handleDownloadState (DOWNLOAD_STARTED);

                TagNode node = cleaner.clean (conn.getInputStream (), "iso-8859-2");

                ByteArrayOutputStream ostr = new ByteArrayOutputStream ();
                new CompactHtmlSerializer (props).writeToStream (node, ostr, "utf-8");
                ByteArrayInputStream istr = new ByteArrayInputStream (ostr.toByteArray ());

                if (Thread.interrupted ())
                {
                    throw new InterruptedException ();
                }

                table.setType (TimeTableType.CLASS);
                table.parse (istr);

                if (Thread.interrupted ())
                {
                    throw new InterruptedException ();
                }
            }
            catch (java.io.IOException e)
            {
                task.handleDownloadState (DOWNLOAD_FAILED);
                e.printStackTrace ();
            }
            catch (ParserConfigurationException e)
            {
                task.handleDownloadState (DOWNLOAD_FAILED);
                e.printStackTrace ();
            }
            catch (SAXException e)
            {
                task.handleDownloadState (DOWNLOAD_FAILED);
                e.printStackTrace ();
            }

            task.handleDownloadState (DOWNLOAD_COMPLETED);
        }
        catch (InterruptedException e)
        {
        }
        finally
        {
            if (task.getTable ().getLessons ().size () == 0)
            {
                task.handleDownloadState (DOWNLOAD_FAILED);
            }

            task.setDownloadThread (null);
            Thread.interrupted ();
        }
    }
}
