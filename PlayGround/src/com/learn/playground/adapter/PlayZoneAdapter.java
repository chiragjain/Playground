
package com.learn.playground.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learn.playground.R;

public class PlayZoneAdapter extends BaseAdapter {

    Context mContext;
    int[] linkResource;

    public PlayZoneAdapter(Context context, int[] links) {
        this.mContext = context;
        this.linkResource = links;
    }

    @Override
    public int getCount() {
        return linkResource.length;
    }

    @Override
    public Object getItem(int position) {
        return linkResource[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_playzone_list, null);

            holder.playZoneTextView = (TextView) convertView.findViewById(R.id.playZoneText);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.playZoneTextView.setText(linkResource[position]);

        return convertView;
    }

    public class ViewHolder {
        TextView playZoneTextView;
    }

}
