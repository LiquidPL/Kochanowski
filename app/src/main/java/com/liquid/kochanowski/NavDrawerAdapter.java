package com.liquid.kochanowski;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liquid on 21.12.14.
 */
public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.DrawerViewHolder>
{
    private List<String> values;
    private TypedArray icons;

    private int resource;

    private Context context;

    public NavDrawerAdapter (int resource, List<String> values, TypedArray icons, Context context)
    {
        this.resource = resource;
        this.values = values;
        this.icons = icons;
        this.context = context;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder (ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from (parent.getContext ()).inflate (resource, parent, false);

        return new DrawerViewHolder (v);
    }

    @Override
    public void onBindViewHolder (DrawerViewHolder holder, int position)
    {
        if (values.get (position).equals ("separator"))
        {
            holder.name.setVisibility (View.GONE);
            holder.icon.setVisibility (View.GONE);
            holder.separator.setVisibility (View.VISIBLE);

            final float scale = context.getResources ().getDisplayMetrics ().density;
            int height = (int) (8.0f * scale + 0.5f);

            holder.v.setMinimumHeight (height);

            ((RelativeLayout) holder.v).setBackgroundResource (0);
        }
        else
        {
            holder.name.setText (values.get (position));
            holder.icon.setImageDrawable (icons.getDrawable (position));

            final float scale = context.getResources ().getDisplayMetrics ().density;
            int height = (int) (48.0f * scale + 0.5f);

            holder.v.setMinimumHeight (height);
        }
    }

    @Override
    public int getItemCount ()
    {
        return values.size ();
    }

    public class DrawerViewHolder extends RecyclerView.ViewHolder
    {
        View v;

        TextView name;
        ImageView icon;
        View separator;

        public DrawerViewHolder (View v)
        {
            super (v);

            this.v = v;
            name = (TextView) v.findViewById (R.id.text);
            icon = (ImageView) v.findViewById (R.id.item_icon);
            separator = v.findViewById (R.id.separator);
        }
    }
}
