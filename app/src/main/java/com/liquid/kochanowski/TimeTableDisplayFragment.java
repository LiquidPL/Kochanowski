package com.liquid.kochanowski;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liquid.kochanparser.TimeTableType;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimeTableDisplayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimeTableDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableDisplayFragment extends Fragment
{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TABLE_NAME = "tablename";
    private static final String ARG_TABLE_TYPE = "tabletype";
    private static final String ARG_DAY_ID     = "dayid";
    private static final String ARG_GROUP_ID   = "groupid";

    // the given fragments parameters
    private String tableName;
    private String tableType;
    private int dayId;
    private int groupId;

    private OnFragmentInteractionListener listener;
    private Activity activity;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tableName Name of the timetable, corresponding to a column in the database
     * @param tableType The timetable type to display (class/teacher/classroom)
     * @param dayId The day to be displayed
     * @param groupId The group to be displayed
     *
     * @return A new instance of fragment TimeTableDisplayFragment.
     */
    public static TimeTableDisplayFragment newInstance (String tableName, TimeTableType tableType, int dayId, int groupId)
    {
        TimeTableDisplayFragment fragment = new TimeTableDisplayFragment ();
        Bundle args = new Bundle ();

        args.putString (ARG_TABLE_NAME, tableName);
        args.putString (ARG_TABLE_TYPE, tableType.getValue ());
        args.putInt (ARG_DAY_ID, dayId);
        args.putInt (ARG_GROUP_ID, groupId);

        fragment.setArguments (args);
        return fragment;
    }

    public TimeTableDisplayFragment ()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        if (getArguments () != null)
        {
            tableName = getArguments ().getString (ARG_TABLE_NAME);
            tableType = getArguments ().getString (ARG_TABLE_TYPE);
            dayId = getArguments ().getInt (ARG_DAY_ID);
            groupId = getArguments ().getInt (ARG_GROUP_ID);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate (R.layout.fragment_timetable_display, container, false);

        recyclerView = (RecyclerView) view.findViewById (R.id.timeTableList);

        layoutManager = new LinearLayoutManager (activity);
        recyclerView.setLayoutManager (layoutManager);

        adapter = new TimeTableAdapter (KochanowskiMainActivity.getHelper ().getReadableDatabase (), tableName, tableType, dayId, groupId);

        recyclerView.setAdapter (adapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed (Uri uri)
    {
        if (listener != null)
        {
            listener.onFragmentInteraction (uri);
        }
    }

    @Override
    public void onAttach (Activity activity)
    {
        super.onAttach (activity);
        try
        {
            listener = (OnFragmentInteractionListener) activity;
            this.activity = activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException (activity.toString ()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach ()
    {
        super.onDetach ();
        listener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onFragmentInteraction (Uri uri);
    }

}
