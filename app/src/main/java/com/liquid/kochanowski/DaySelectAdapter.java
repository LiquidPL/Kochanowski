package com.liquid.kochanowski;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by liquid on 10.12.14.
 */
public class DaySelectAdapter extends ArrayAdapter <DaySelectAdapter.DayDate>
{
    public static class DayDate
    {
        int id;

        String day;
        String date;

        public DayDate (int id, String day, String date)
        {
            this.id = id;
            this.day = day;
            this.date = date;
        }
    }

    private List<DayDate> days = new ArrayList<> ();
    private int resource;

    private Context context;

    public DaySelectAdapter (Context context, int resource, List <DayDate> days)
    {
        super (context, resource);

        this.context = context;
        this.resource = resource;
        this.days = days;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        View view = getCustomView (position, convertView, parent);

        View separator = view.findViewById (R.id.separator);
        separator.setVisibility (View.GONE);

        return view;
    }

    @Override
    public View getDropDownView (int position, View convertView, ViewGroup parent)
    {
        return getCustomView (position, convertView, parent);
    }

    private View getCustomView (int position, View convertView, ViewGroup parent)
    {
        View view = LayoutInflater.from (parent.getContext ()).inflate (resource, parent, false);

        TextView dayName = (TextView) view.findViewById (R.id.day_name);
        TextView date = (TextView) view.findViewById (R.id.date);

        dayName.setText (days.get (position).day);
        date.setText (days.get (position).date);

        return view;
    }

    @Override
    public int getCount ()
    {
        return days.size ();
    }

    public static List <DayDate> getDays (Context context)
    {
        List <DayDate> days = new ArrayList<> ();

        Calendar cal = Calendar.getInstance ();
        int today = cal.get (Calendar.DAY_OF_WEEK);

        SimpleDateFormat format = new SimpleDateFormat ("dd MMMM");

        int diff = -cal.get (Calendar.DAY_OF_WEEK) + 2;
        cal.add (Calendar.DAY_OF_MONTH, diff);
        for (int i = 0; i < 5; i++)
        {
            if (cal.get (Calendar.DAY_OF_WEEK) == today)
            {
                days.add (new DayDate (i, context.getResources ().getString (R.string.day_name_today), format.format (cal.getTime ())));
                cal.add (Calendar.DAY_OF_MONTH, 1);
                continue;
            }

            String day = "";

            switch (cal.get (Calendar.DAY_OF_WEEK))
            {
                case 2:
                    day = context.getResources ().getString (R.string.day_name_0);
                    break;
                case 3:
                    day = context.getResources ().getString (R.string.day_name_1);
                    break;
                case 4:
                    day = context.getResources ().getString (R.string.day_name_2);
                    break;
                case 5:
                    day = context.getResources ().getString (R.string.day_name_3);
                    break;
                case 6:
                    day = context.getResources ().getString (R.string.day_name_4);
                    break;
            }

            days.add (new DayDate (i, day, format.format (cal.getTime ())));
            cal.add (Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }
}
