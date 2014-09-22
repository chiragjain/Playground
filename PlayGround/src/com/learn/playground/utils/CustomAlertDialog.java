package com.learn.playground.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.learn.playground.R;

public class CustomAlertDialog {

	private Context mContext;
	private Dialog dialog;
	private FrameLayout customView;
	private View view;
	private View titleDivider, contentDivider, buttonDivider;
	int customViewId;
	LinearLayout titleContainer, btnContainer;
	private TextView title;
	private ImageView icon;
	private TextView okBtn, cancelBtn;

	private boolean hasTitle = false, hasPositiveButton = false,
			hasNagativeButton = false;

	public CustomAlertDialog(Context context, int viewId) {
		this(context, LayoutInflater.from(context).inflate(viewId, null));
	}

	public CustomAlertDialog(Context context, int viewId, int style) {
		this(context, LayoutInflater.from(context).inflate(viewId, null), style);
	}

	public CustomAlertDialog(Context context, String message) {
		this(context, new TextView(context));
		setMessage(message);
	}

	public CustomAlertDialog(Context context, String message, int style) {
		this(context, new TextView(context), style);
		setMessage(message);
	}

	public CustomAlertDialog(Context context, View view, int style) {
		this.mContext = context;
		init(view, style);

	}

	public CustomAlertDialog(Context context, View view) {
		this(context, view, R.attr.customAlertDialogStyle, 0);
	}

	private CustomAlertDialog(Context context, View view, int defStyle, int i) {
		this.mContext = context;
		int style = 0;
		TypedArray a = context.getTheme().obtainStyledAttributes(
				new int[] { defStyle });

		try {

			style = a.getResourceId(0, 0);

			if (style == 0) {

				style = R.style.cadStyle;
			}

		} finally {

			a.recycle();
		}

		init(view, style);
	}

	private void init(View view, int style) {

		TypedArray a = mContext.obtainStyledAttributes(style,
				R.styleable.CustomAlertDialog);

		try {

		} finally {
			a.recycle();
		}

		dialog = new Dialog(mContext, style);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_alert_dialog);
		dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);

		this.view = view;
		bindViews();

	}

	private void bindViews() {

		btnContainer = (LinearLayout) dialog
				.findViewById(R.id.buttons_container);
		title = (TextView) dialog.findViewById(R.id.alertDialogTitle);
		titleContainer = (LinearLayout) dialog
				.findViewById(R.id.title_container);
		icon = (ImageView) dialog.findViewById(R.id.alertDialogicon);
		okBtn = (TextView) dialog.findViewById(R.id.alernDialogOkBtn);
		cancelBtn = (TextView) dialog.findViewById(R.id.alertDialogCancelBtn);
		customView = (FrameLayout) dialog.findViewById(R.id.customPanel);
		customView.addView(view);
		titleDivider = dialog.findViewById(R.id.title_divider);
		contentDivider = dialog.findViewById(R.id.content_divider);
		buttonDivider = dialog.findViewById(R.id.button_divider);
	}

	public View getCustomView() {
		return view;
	}

	public void setCancelable(boolean flag) {
		dialog.setCancelable(flag);
	}

	public void setCanceledOnTouchOutside(boolean flag) {
		dialog.setCanceledOnTouchOutside(flag);
	}

	public void setTitle(String str) {
		title.setText(str);
		hasTitle = true;
	}

	public void setIcon(int drawable) {
		icon.setVisibility(View.VISIBLE);
		icon.setImageResource(drawable);
		hasTitle = true;
	}

	public void setPositiveButton(String str, CustomDialogListener listener) {

		hasPositiveButton = true;
		okBtn.setText(str);
		okBtn.setOnClickListener(listener);

	}

	public void setNegativeButton(String str, CustomDialogListener listener) {
		hasNagativeButton = true;
		cancelBtn.setText(str);
		cancelBtn.setOnClickListener(listener);

	}

	public void setButtonFonts(Typeface positive, Typeface negative) {
		okBtn.setTypeface(positive);
		cancelBtn.setTypeface(negative);
	}

	public void setView(View v) {
		customView.removeAllViews();
		view = v;
		customView.addView(view);
	}

	public void setView(int resId) {
		customView.removeAllViews();
		view = LayoutInflater.from(mContext).inflate(resId, null);
		customView.addView(view);
	}

	public void setMessage(String message) {
		customView.removeAllViews();
		TextView tv = new TextView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		tv.setLayoutParams(params);
		tv.setText(message);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(16);
		tv.setPadding(d2p(8), 0, d2p(8), d2p(16));
		view = tv;
		customView.addView(view);
	}

	public void setAdapter(ListAdapter adapter, OnItemClickListener listener) {
		customView.removeAllViews();
		ListView listview = new ListView(mContext);
		listview.setOnItemClickListener(listener);
		listview.setAdapter(adapter);
		view = listview;
		customView.addView(view);
	}

	public void show() {
		if (!hasTitle) {
			titleContainer.setVisibility(View.GONE);
			titleDivider.setVisibility(View.GONE);
		}
		if (!hasPositiveButton) {
			okBtn.setVisibility(View.GONE);
			buttonDivider.setVisibility(View.GONE);
		}
		if (!hasNagativeButton) {
			cancelBtn.setVisibility(View.GONE);
			buttonDivider.setVisibility(View.GONE);
		}
		if (!hasPositiveButton && !hasNagativeButton)
			contentDivider.setVisibility(View.GONE);
		dialog.show();

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public interface CustomDialogListener extends OnClickListener {

	}

	public class StyledButton extends Button {

		public StyledButton(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

	}

	public int d2p(int dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dip, mContext.getResources().getDisplayMetrics());
	}

}
