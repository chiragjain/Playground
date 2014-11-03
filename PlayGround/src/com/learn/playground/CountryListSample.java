package com.learn.playground;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.learn.playground.data.CountryCodes;
import com.learn.playground.data.CountryCodes.CountryCode;
import com.learn.playground.utils.CustomAlertDialog;
import com.learn.playground.utils.CustomAlertDialog.CustomDialogListener;

@SuppressLint("InflateParams")
public class CountryListSample extends BaseActivity implements OnClickListener {

	private Button countryCodeButton;
	private Button continueButton;
	private EditText phoneNumberView;

	private CountryCode selectedCountryCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		countryCodeButton = (Button) findViewById(R.id.selectCountryButton);
		countryCodeButton.setOnClickListener(this);

		continueButton = (Button) findViewById(R.id.continueButton);
		continueButton.setOnClickListener(this);

		phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
		phoneNumberView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				validateForm();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		validateForm();
		setCurrentCountry();
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

	/**
	 * Set current country
	 */
	private void setCurrentCountry() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String countryCode = tm.getSimCountryIso();

		ArrayList<CountryCode> codes = CountryCodes.getInstance(
				CountryListSample.this).getCountryCodesList();
		for (CountryCode code : codes) {
			if (code.isoCode.equalsIgnoreCase(countryCode)) {
				setCountryInButton(code);
				break;
			}
		}
	}

	/**
	 * Set country code in select country code button
	 * 
	 * @param code
	 */
	private void setCountryInButton(CountryCode code) {
		selectedCountryCode = code;
		if (code != null) {
			countryCodeButton.setText(String.format(
					getString(R.string.country_list_format),
					selectedCountryCode.name, selectedCountryCode.phoneCode
							+ ""));
		} else {
			countryCodeButton.setText(getString(R.string.select_country));
		}

	}

	/**
	 * Create dialog for confirmation
	 * 
	 * @param number
	 *            Phone Number
	 * @return Alert Dialog
	 */
	private CustomAlertDialog getDialogForConfirmation(String number) {

		View v = getLayoutInflater().inflate(R.layout.dialog_confirm_phone,
				null, false);

		TextView phoneNumber = (TextView) v
				.findViewById(R.id.confirmPhoneNumber);
		phoneNumber.setText(String.format(getString(R.string.phone_no_format),
				selectedCountryCode.phoneCode + "", number));

		final CustomAlertDialog dialog = new CustomAlertDialog(
				CountryListSample.this, v);
		dialog.setTitle(getString(R.string.phone_no_verification));
		dialog.setPositiveButton(getString(R.string.confirm),
				new CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});

		dialog.setNegativeButton(getString(R.string.edit),
				new CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});

		return dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selectCountryButton:
			startIntentforResult(CountryListSample.this, CountryList.class,
					REQUEST_COUNTRY_CODE);
			break;

		case R.id.continueButton:
			if (validateForm() == 1) {
				getDialogForConfirmation(phoneNumberView.getText().toString())
						.show();
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_COUNTRY_CODE) {
			if (resultCode == RESULT_OK) {

				if (data != null && data.getExtras() != null) {
					CountryCode code = CountryCodes
							.getInstance(CountryListSample.this).new CountryCode(
							data.getExtras().getInt(KEY_COUNTRY_ID), data
									.getExtras().getString(KEY_COUNTRY_NAME),
							data.getExtras().getString(KEY_ISO_CODE), data
									.getExtras().getInt(KEY_PHONE_CODE));

					setCountryInButton(code);
				} else {
					setCountryInButton(null);
				}
				validateForm();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Validate Form
	 * 
	 * @return error code
	 */
	private int validateForm() {
		if (phoneNumberView.getText().toString().length() < 8) {
			continueButton.setEnabled(false);
			phoneNumberView.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.ic_cross), null);
			return -1;
		} else if (selectedCountryCode == null) {
			continueButton.setEnabled(false);
			phoneNumberView.setCompoundDrawablesWithIntrinsicBounds(null, null,
					null, null);
			return -2;
		} else {
			continueButton.setEnabled(true);
			phoneNumberView.setCompoundDrawablesWithIntrinsicBounds(null, null,
					null, null);
			return 1;
		}
	}

}
