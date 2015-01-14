package com.github.LiquidPL.kochanowski.parse.table;

/**
 * A class for holding time (hour and minute), and parsing it from a string (hh:mm)
 *
 * @author Krzysztof Gutkowski (LiquidPL)
 * @version dev
 */
public class Time
{
    private int hour = 0;
    private int minute = 0;

    /**
     * Class constructor. Gets the time as a string (hh:mm) and saves the hour and minute to corresponding ints
     *
     * @param time The time in a String form (hh:mm)
     */
    public Time (String time)
    {
        String result[] = time.split (":");
        setHour (toInt (result[0]));
        setMinute (toInt (result[1]));
    }

    /**
     * Converts a number in a String form to an integer
     *
     * @param number Number in a string form
     * @return Number in an integer form
     */
    private int toInt (String number)
    {
        int result = 0;
        for (int i = 0; i < number.length (); i++)
        {
            result = result * 10 + number.charAt (i) - '0';
        }
        return result;
    }

    /**
     * Getter for the hour
     *
     * @return The hour stored in this object
     */
    public int getHour ()
    {
        return hour;
    }

    /**
     * Setter for the hour
     *
     * @param hour The hour to set
     */
    public void setHour (int hour)
    {
        this.hour = hour;
    }

    /**
     * Getter for the minute.
     *
     * @return Minute stored in this object
     */
    public int getMinute ()
    {
        return minute;
    }

    /**
     * Setter for the minute
     *
     * @param minute The minute to set
     */
    public void setMinute (int minute)
    {
        this.minute = minute;
    }

    /**
     * Returns the hour in String (hh:mm) format
     * @return Hour in hh:mm format.
     */
    public String toString ()
    {
        String ret = hour + ":" + minute;
        if (minute == 0) ret += "0";
        return ret;
    }
}
