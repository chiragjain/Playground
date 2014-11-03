package com.learn.playground;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.learn.playground.extras.Constants;

public class BaseActivity extends Activity implements Constants {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * Start activity with FLAG_ACITIVITY_CLEAR_TOP
	 * 
	 * @param current
	 *            current activity
	 * @param dstClass
	 *            destination class
	 */
	public void startWithFlag(Activity current, Class<?> dstClass) {
		Intent intent = new Intent(current, dstClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * Start activity with FLAG_ACITIVITY_CLEAR_TOP
	 * 
	 * @param current
	 *            current activity
	 * @param dstClass
	 *            destination class
	 * @param data
	 *            extra data
	 */
	public void startWithFlag(Activity current, Class<?> dstClass, Bundle data) {
		Intent intent = new Intent(current, dstClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(KEY_DATA, data);
		startActivity(intent);
	}

	/**
	 * Start normal activity
	 * 
	 * @param current
	 *            current activity
	 * @param dstClass
	 *            destination class
	 */

	public void startIntent(Activity current, Class<?> dstClass) {
		startActivity(new Intent(current, dstClass));
	}

	/**
	 * Start Activity with FLAG_ACITIVITY_CLEAR_TOP
	 * 
	 * @param current
	 *            current activity
	 * @param dstClass
	 *            destination class
	 * @param data
	 *            extra data
	 */
	public void startIntent(Activity current, Class<?> dstClass, Bundle data) {
		Intent intent = new Intent(current, dstClass);
		intent.putExtra(KEY_DATA, data);
		startActivity(intent);
	}

	/**
	 * Start Activity for a result
	 * 
	 * @param current
	 *            current activity
	 * @param dstClass
	 *            destination class
	 * @param requestCode
	 *            request code
	 */
	public void startIntentforResult(Activity current, Class<?> dstClass,
			int requestCode) {
		startActivityForResult((new Intent(current, dstClass)), requestCode);
	}

	/**
	 * Start Activity for a result
	 * 
	 * @param current
	 *            current activity
	 * @param dstClass
	 *            destination class
	 * @param requestCode
	 *            request code
	 * @param data
	 *            data bundle
	 */
	public void startIntentforResult(Activity current, Class<?> dstClass,
			int requestCode, Bundle data) {
		Intent intent = new Intent(current, dstClass);
		intent.putExtra(KEY_DATA, data);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * Start Activity for sending a result with data
	 * 
	 * @param current
	 *            current activity
	 * @param dstClass
	 *            destination class
	 * @param resultCode
	 *            result code
	 * @param data
	 *            data bundle
	 */
	public void startResult(Activity current, Class<?> dstClass,
			int resultCode, Bundle data) {
		Intent intent = new Intent(current, dstClass);
		intent.putExtra(KEY_DATA, data);
		setResult(resultCode, intent);
	}

}
