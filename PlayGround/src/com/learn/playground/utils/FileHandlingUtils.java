package com.learn.playground.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class FileHandlingUtils {

	static InputStream in = null;
	static JSONObject jObj = null;
	static String json = "";

	public static JSONObject getJSONFromFile(Context context, String filePath) {

		try {
			in = context.getAssets().open(filePath);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load JSON '" + filePath + "'",
					e);
		}

		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in), 128);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;
	}

}
