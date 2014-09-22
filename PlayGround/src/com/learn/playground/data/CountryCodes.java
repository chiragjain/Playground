package com.learn.playground.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.learn.playground.utils.FileHandlingUtils;
import com.learn.playground.utils.StringObjectForIndexer;

public class CountryCodes {

	private ArrayList<CountryCode> mCodes;	
	private static CountryCodes mInstance;

	public CountryCodes(Context context) {		
		mCodes = readCountryCodesFromFile(context);
	}

	public static CountryCodes getInstance(Context context) {
		if (mInstance == null)
			mInstance = new CountryCodes(context);
		return mInstance;
	}

	private ArrayList<CountryCode> readCountryCodesFromFile(Context context) {

		JSONObject json = FileHandlingUtils.getJSONFromFile(context,
				"country_code.json");

		ArrayList<CountryCode> codes = new ArrayList<CountryCodes.CountryCode>();

		try {
			// Getting Array of Frames
			JSONArray countries = json.getJSONArray("country");

			// looping through All Contacts
			for (int i = 0; i < countries.length(); i++) {
				JSONObject country = countries.getJSONObject(i);

				codes.add(new CountryCode(country.getInt("id"), country
						.getString("name"), country.getString("code"), country
						.getInt("phoneCode")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Collections.sort(codes, new SortCodes());

		return codes;
	}
	
	public ArrayList<CountryCode> getCountryCodesList() {		
		return this.mCodes;
	}
	
	private class SortCodes implements Comparator<CountryCode> {
		
		@Override
		public int compare(CountryCode lhs, CountryCode rhs) {			
			return lhs.name.compareTo(rhs.name);
		}
	}

	public class CountryCode extends StringObjectForIndexer {
		public int id;
		public String name;
		public String isoCode;
		public int phoneCode;

		public CountryCode(int id, String name, String code, int phoneCode) {
			super(name);
			this.id = id;
			this.name = name;
			this.isoCode = code;
			this.phoneCode = phoneCode;
		}
	}

}
