package com.learn.playground.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.learn.playground.R;

public class BrushAdapter extends BaseAdapter {

	private Context mContext;

	private String[] mBrushList;

	public BrushAdapter(Context context, String[] brushList) {
		this.mBrushList = brushList;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mBrushList.length;
	}

	@Override
	public Object getItem(int position) {
		return mBrushList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			v = inflater.inflate(R.layout.item_brush_list, parent, false);
		}

		TextView brushTypeView = (TextView) v.findViewById(R.id.brushItem);
		brushTypeView.setText(mBrushList[position]);

		return v;
	}

}
