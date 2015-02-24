package com.github.LiquidPL.kochanowski.parse.handler;

import com.github.LiquidPL.kochanowski.parse.DbWriter;
import com.github.LiquidPL.kochanowski.parse.TimeTableDownloadRunnable;
import com.github.LiquidPL.kochanowski.parse.Type;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A handler for the SAX parser.
 * The functions should be called by the parser, rather than on their own.
 *
 * @author Krzysztof Gutkowski (LiquidPL)
 * @version dev
 */
public class VulcanHandler
        extends DefaultHandler
{
    private TimeTableDownloadRunnable runnable;
    private int tableType;

    private String shortName;
    private String longName;

    private int currentLesson = -4;// currentLesson and currentDay are set to negative values
    private int currentDay = -3;   // because of the <tr> and <td> tags at the beginning of
                                   // the HTML file not containing any important data (so the indexing begins from 0)
    private String currentName = "";
    private String currentAttribute = "";
    private int currentGroup = 0; // 0 - current lesson is not grouped; -1 - current lesson is going to be grouped
                                  // 1 - group 1; 2 - group 2
    private String currentSubject = "";
    private String currentTeacher = "";
    private String currentClassroom = "";
    private String currentClass = "";

    private List<String> startTimes = new ArrayList<> ();
    private List<String> endTimes = new ArrayList<> ();

    /**
     * Class constructor
     *
     */
    public VulcanHandler (TimeTableDownloadRunnable runnable, int tableType)
    {
        this.runnable = runnable;
        this.tableType = tableType;
    }

    @Override
    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        currentName = qName; // storing the name for use in other methods
        if ("tr".equals (qName)) // increasing the lesson number as we traverse the timetable
        {
            currentLesson++;
            currentDay = -3;
        }
        if ("td".equals (qName) || "th".equals (qName)) currentDay++; // same with the day
        if ("span".equals (qName) && currentGroup == -1) // set the appropriate group number when in group mode
        {
            currentGroup = 1;
        }
        else if ("span".equals (qName) && currentGroup == 1) // same as above
        {
            currentGroup = 2;
        }
        if ("span".equals (qName) || "a".equals (qName)) // if we hit elements that may contain timetable data,
        {                                                // we check if this is the case and store the information
            int length = attributes.getLength ();        // on what data may it be, so we can check it in
            for (int i = 0; i < length; i++)             // characters () method
            {
                String value = attributes.getValue (i);
                // p - subject, n - teacher, s - classroom, o - class
                // tytulnapis is a special case, it contains title of the timetable
                if ("p".equals (value) || "n".equals (value) || "s".equals (value) || "o".equals (value) || "tytulnapis".equals (value))
                {
                    currentAttribute = value;
                }
                if ("font-size:85%".equals (value)) // entering group mode, there will be two lessons within one hour here
                {
                    currentGroup = -1;
                }
            }
        }

        // inserting lesson for the first group, they are separated by a <br/> tag
        if ("br".equals (qName) && currentGroup == 1 && currentDay >= 0 && currentDay <= 4 &&
                !checkIfEmpty (currentSubject, currentTeacher, currentClassroom, currentClass))
        {
            DbWriter.insertLesson (
                    currentDay,
                    startTimes.get (currentLesson),
                    endTimes.get (currentLesson),
                    currentLesson,
                    currentGroup,
                    currentSubject,
                    currentTeacher,
                    currentClassroom,
                    shortName);
            currentSubject = ""; currentTeacher = ""; currentClassroom = ""; currentClass = "";
        }
    }

    @Override
    public void endElement (String uri, String localName, String qName) throws SAXException
    {
        // inserting lesson for the second group, or if the lesson is not grouped
        if ("td".equals (qName) && currentDay >= 0 && currentDay <= 4 &&
            !checkIfEmpty (currentSubject, currentTeacher, currentClassroom, currentClass))
        {
            if (currentGroup == 1 || currentGroup == 2)
            {
                DbWriter.insertLesson (
                        currentDay,
                        startTimes.get (currentLesson),
                        endTimes.get (currentLesson),
                        currentLesson,
                        currentGroup,
                        currentSubject,
                        currentTeacher,
                        currentClassroom,
                        shortName);
            }
            else
            {
                DbWriter.insertLesson (
                        currentDay,
                        startTimes.get (currentLesson),
                        endTimes.get (currentLesson),
                        currentLesson,
                        0,
                        currentSubject,
                        currentTeacher,
                        currentClassroom,
                        shortName);
            }
            currentGroup = 0;
            currentSubject = ""; currentTeacher = ""; currentClassroom = ""; currentClass = "";
        }
    }

    @Override
    public void characters (char[] ch, int start, int length) throws SAXException
    {
        String value = new String (ch, start, length).trim ();
        if (value.length () == 0) return;
        // next for ifs: storing timetable data for later insertion depending on the attribute
        if ("p".equals (currentAttribute))
        {
            currentSubject = value;
        }
        if ("n".equals (currentAttribute))
        {
            currentTeacher = value;
        }
        if ("s".equals (currentAttribute))
        {
            currentClassroom = value;
        }
        if ("o".equals (currentAttribute))
        {
            currentClass = value;
        }
        if ("td".equals (currentName) && currentDay == -1) // storing the lessons begin and end hours
        {
            String[] values = new String (value.toCharArray (), 3, value.length () - 3).split ("-");
            startTimes.add (values[0]);
            endTimes.add (values[1]);
        }
        if ("span".equals (currentName) && "-------".equals (value)) // this means that only group 2 has a lesson at this hour
        {
            currentGroup = 2;
        }
        // storing the timetable name, works different for class/teacher and classroom
        if ("span".equals (currentName) && "tytulnapis".equals (currentAttribute))
        {
            String values[] = value.split (" \\(");

            longName = values[0];
            shortName = new String (values[1].toCharArray (), 0, values[1].length () - 1);

            if (tableType == Type.CLASS)
            {
                DbWriter.insertClass (shortName, longName);
                runnable.setTableName (longName + " (" + shortName + ")");
            }

            currentAttribute = "";
        }
    }

    /**
     * Checks if the entire set of lesson data have been already collected.
     * @param name Subject name
     * @param code Teacher code
     * @param room Classroom
     * @param _class Class
     * @return
     */
    private Boolean checkIfEmpty (String name, String code, String room, String _class)
    {
        if (name.length () != 0 && code.length () != 0 && room.length () != 0) return false;
        else if (_class.length () != 0 && name.length () != 0 && room.length () != 0) return false;
        else if (code.length () != 0 && _class.length () != 0 && name.length () != 0) return false;
        else return true;
    }
}
