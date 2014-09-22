package com.learn.playground.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.learn.playground.R;
import com.learn.playground.data.CountryCodes.CountryCode;
import com.learn.playground.utils.AlphabetIndexerForList;
import com.learn.playground.widget.SectionIndexerBaseAdapter;

public class CountryCodeSectionAdapter extends SectionIndexerBaseAdapter
		implements Filterable {

	private ArrayList<CountryCode> mCodes;
	private ArrayList<CountryCode> mAllCodes;
	private Context mContext;
	private AlphabetIndexerForList<CountryCode> mIndexer;

	public CountryCodeSectionAdapter(Context context,
			ArrayList<CountryCode> codes) {
		mCodes = codes;
		mAllCodes = codes;
		mContext = context;
		mIndexer = new AlphabetIndexerForList<CountryCode>(codes,
				" ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		setIndexer(mIndexer);
	}

	@Override
	public Object getDataItem(int position) {
		return mCodes.get(position);
	}

	@Override
	public long getDataItemId(int position) {
		return position;
	}

	@Override
	public int getDataCount() {
		return mCodes.size();
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			v = inflater.inflate(R.layout.item_country_list, parent, false);
		}

		TextView countryName = (TextView) v.findViewById(R.id.countryItem);
		countryName.setTextColor(mContext.getResources().getColorStateList(
				R.color.color_selector));
		countryName.setBackgroundResource(R.drawable.bg_list_selector);

		countryName
				.setText(String.format(
						mContext.getString(R.string.country_list_format),
						mCodes.get(position).name,
						mCodes.get(position).phoneCode + ""));

		v.findViewById(R.id.headerDivider).setVisibility(View.GONE);

		return v;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {

		View v = convertView;

		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			v = inflater.inflate(R.layout.item_country_list, parent, false);
		}

		v.setEnabled(false);

		TextView countryName = (TextView) v.findViewById(R.id.countryItem);
		countryName.setTextColor(mContext.getResources().getColor(
				R.color.violet));
		countryName.setBackgroundResource(R.color.light_gray);
		countryName.setText((String) getSections()[section]);

		v.findViewById(R.id.headerDivider).setVisibility(View.VISIBLE);

		return v;
	}

	@Override
	public void notifyDataSetChanged() {
		mIndexer = new AlphabetIndexerForList<CountryCode>(mCodes,
				" ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		setIndexer(mIndexer);
		super.notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mCodes = (ArrayList<CountryCode>) results.values;
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();

				ArrayList<CountryCode> filteredCodes = new ArrayList<CountryCode>();

				constraint = constraint.toString().toLowerCase(Locale.US);
				for (int i = 0; i < mAllCodes.size(); i++) {
					CountryCode code = mAllCodes.get(i);
					if (code.name.toLowerCase(Locale.US).startsWith(
							constraint.toString())) {
						filteredCodes.add(code);
					}
				}

				results.count = filteredCodes.size();
				results.values = filteredCodes;

				return results;
			}
		};
		return filter;
	}
}
