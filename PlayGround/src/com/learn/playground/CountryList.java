package com.learn.playground;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.learn.playground.adapter.CountryCodeSectionAdapter;
import com.learn.playground.data.CountryCodes;
import com.learn.playground.data.CountryCodes.CountryCode;
import com.learn.playground.widget.PinnedHeaderListView;
import com.learn.playground.widget.PinnedHeaderListView.OnItemClickListener;

public class CountryList extends BaseActivity {

	CountryCodeSectionAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_list);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		PinnedHeaderListView countryList = (PinnedHeaderListView) findViewById(R.id.countryList);
		mAdapter = new CountryCodeSectionAdapter(CountryList.this, CountryCodes
				.getInstance(CountryList.this).getCountryCodesList());
		countryList.setAdapter(mAdapter);
		countryList.setFastScrollEnabled(true);

		countryList.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onSectionClick(AdapterView<?> adapterView, View view,
					int section, long id) {
			}
			
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int section,
					int position, long id) {			

					CountryCode code = (CountryCode) ((CountryCodeSectionAdapter) adapterView
							.getAdapter()).getDataItem(position);

					Bundle data = new Bundle();
					data.putInt(KEY_COUNTRY_ID, code.id);
					data.putString(KEY_COUNTRY_NAME, code.name);
					data.putString(KEY_ISO_CODE, code.isoCode);
					data.putInt(KEY_PHONE_CODE, code.phoneCode);

					Intent intent = new Intent();
					intent.putExtras(data);

					setResult(RESULT_OK, intent);
					finish();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater i = getSupportMenuInflater();
		i.inflate(R.menu.menu_country_list, menu);

		SearchView mSearchView = (SearchView) menu.findItem(R.id.menu_search)
				.getActionView();
		mSearchView.setQueryHint(getString(R.string.search_country));

		AutoCompleteTextView searchText = (AutoCompleteTextView) mSearchView
				.findViewById(R.id.abs__search_src_text);
		searchText.setHintTextColor(getResources().getColor(
				R.color.white_disabled));
		searchText.setTextColor(getResources().getColor(R.color.white));

		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				mAdapter.getFilter().filter(newText);
				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
