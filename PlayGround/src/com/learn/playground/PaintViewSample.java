package com.learn.playground;

import java.util.UUID;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;
import com.learn.playground.adapter.BrushAdapter;
import com.learn.playground.utils.CustomAlertDialog;
import com.learn.playground.utils.CustomAlertDialog.CustomDialogListener;
import com.learn.playground.widget.drawing.PixelPaintView;
import com.learn.playground.widget.drawing.PixelPaintView.OnDrawingStateChangeListener;

public class PaintViewSample extends BaseActivity implements OnClickListener,
		OnDrawingStateChangeListener {

	ImageButton newButton, undoButton, redoButton, moveButton, saveButton;
	ImageButton brushButton, brushSizeButton, eraserButton, colorPickerButton;

	View selectedColorView;

	PixelPaintView mPaintView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paint_view);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		newButton = (ImageButton) findViewById(R.id.newButton);
		newButton.setOnClickListener(this);

		undoButton = (ImageButton) findViewById(R.id.undoButton);
		undoButton.setOnClickListener(this);

		redoButton = (ImageButton) findViewById(R.id.redoButton);
		redoButton.setOnClickListener(this);

		moveButton = (ImageButton) findViewById(R.id.moveButton);
		moveButton.setOnClickListener(this);

		saveButton = (ImageButton) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);

		brushButton = (ImageButton) findViewById(R.id.brushButton);
		brushButton.setOnClickListener(this);

		brushSizeButton = (ImageButton) findViewById(R.id.brushSizeButton);
		brushSizeButton.setOnClickListener(this);

		eraserButton = (ImageButton) findViewById(R.id.eraserButton);
		eraserButton.setOnClickListener(this);

		colorPickerButton = (ImageButton) findViewById(R.id.colorPickerButton);
		colorPickerButton.setOnClickListener(this);

		selectedColorView = findViewById(R.id.selectedColorView);

		mPaintView = (PixelPaintView) findViewById(R.id.paintView);
		mPaintView.setOnDrawingStateChangeListener(this);

		checkUndoRedoState();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.newButton:
			mPaintView.startNew();
			break;

		case R.id.undoButton:
			mPaintView.performUndo();
			break;

		case R.id.redoButton:
			mPaintView.performRedo();
			break;

		case R.id.moveButton:
			mPaintView.setViewMovable(!mPaintView.isViewMovable());
			moveButton.setSelected(mPaintView.isViewMovable());
			break;

		case R.id.saveButton:
			saveDrawing();
			break;

		case R.id.brushButton:
			getDialogForBrushType().show();
			break;

		case R.id.brushSizeButton:
			getDialogForBrushSize(mPaintView.getBrushSize()).show();
			break;

		case R.id.eraserButton:
			mPaintView.setErase(!mPaintView.isEraserEnable());
			eraserButton.setSelected(mPaintView.isEraserEnable());
			break;

		case R.id.colorPickerButton:
			getDialogForColorPicker(mPaintView.getColor()).show();
			break;

		default:
			break;

		}

	}

	private void saveDrawing() {
		final CustomAlertDialog saveDialog = new CustomAlertDialog(
				PaintViewSample.this, getString(R.string.save_drawing_message));
		saveDialog.setTitle(getString(R.string.save));
		saveDialog.setPositiveButton(getString(R.string.ok),
				new CustomDialogListener() {

					@Override
					public void onClick(View v) {
						// save drawing
						mPaintView.setDrawingCacheEnabled(true);
						// attempt to save
						String imgSaved = MediaStore.Images.Media.insertImage(
								getContentResolver(),
								mPaintView.getDrawingCache(), UUID.randomUUID()
										.toString() + ".png", "drawing");
						// feedback
						if (imgSaved != null) {
							Toast savedToast = Toast.makeText(
									getApplicationContext(),
									"Drawing saved to Gallery!",
									Toast.LENGTH_SHORT);
							savedToast.show();
						} else {
							Toast unsavedToast = Toast.makeText(
									getApplicationContext(),
									"Oops! Image could not be saved.",
									Toast.LENGTH_SHORT);
							unsavedToast.show();
						}
						mPaintView.destroyDrawingCache();
						saveDialog.dismiss();
					}
				});

		saveDialog.setNegativeButton(getString(R.string.cancel),
				new CustomDialogListener() {

					@Override
					public void onClick(View v) {
						saveDialog.dismiss();
					}
				});
		saveDialog.show();
	}

	private void checkUndoRedoState() {
		undoButton.setEnabled(mPaintView.getUndoState());
		redoButton.setEnabled(mPaintView.getRedoState());
	}

	private void setBrushColor(int color) {
		mPaintView.setColor(color);
		selectedColorView.setBackgroundColor(color);
	}

	/**
	 * Create dialog for color picker
	 * 
	 * @param oldColor
	 *            Already selected color
	 * @return Alert Dialog
	 */
	@SuppressLint("InflateParams") private CustomAlertDialog getDialogForColorPicker(int oldColor) {

		View v = getLayoutInflater().inflate(R.layout.dialog_color_picker,
				null, false);

		final ColorPicker picker = (ColorPicker) v.findViewById(R.id.picker);
		SVBar svBar = (SVBar) v.findViewById(R.id.svbar);
		picker.addSVBar(svBar);
		picker.setOldCenterColor(oldColor);

		final CustomAlertDialog dialog = new CustomAlertDialog(
				PaintViewSample.this, v);
		dialog.setTitle(getString(R.string.color_picker));
		dialog.setPositiveButton(getString(R.string.ok),
				new CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.dismiss();
						setBrushColor(picker.getColor());
					}
				});

		dialog.setNegativeButton(getString(R.string.cancel),
				new CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});

		return dialog;
	}

	/**
	 * Create dialog for color picker
	 * 
	 * @param oldBrushSize
	 *            selected Brush Size
	 * @return Alert Dialog
	 */
	@SuppressLint("InflateParams") private CustomAlertDialog getDialogForBrushSize(int oldBrushSize) {

		View v = getLayoutInflater().inflate(R.layout.dialog_select_brush_size,
				null, false);

		final int maxBrushSize = getResources().getDimensionPixelSize(
				R.dimen.max_brush_size);
		final int minBrushSize = getResources().getDimensionPixelSize(
				R.dimen.min_brush_size);

		final SeekBar sizeChooser = (SeekBar) v.findViewById(R.id.brushSeekBar);
		final ImageView brushSizePreviewView = (ImageView) v
				.findViewById(R.id.brushSizePreview);

		final LayoutParams params = new LayoutParams(oldBrushSize, maxBrushSize);
		brushSizePreviewView.setLayoutParams(params);

		sizeChooser
				.setProgress((int) (((float) (oldBrushSize - minBrushSize) / (float) (maxBrushSize - minBrushSize)) * 100));

		sizeChooser.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				params.width = minBrushSize
						+ (int) ((maxBrushSize - minBrushSize) * ((float) progress / 100f));
				brushSizePreviewView.setLayoutParams(params);
			}
		});

		final CustomAlertDialog dialog = new CustomAlertDialog(
				PaintViewSample.this, v);
		dialog.setTitle(getString(R.string.brush_size));
		dialog.setPositiveButton(getString(R.string.ok),
				new CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.dismiss();
						mPaintView
								.setBrushSize(minBrushSize
										+ (int) ((maxBrushSize - minBrushSize) * ((float) sizeChooser
												.getProgress() / 100f)));
					}
				});

		dialog.setNegativeButton(getString(R.string.cancel),
				new CustomDialogListener() {

					@Override
					public void onClick(View view) {
						dialog.dismiss();
					}
				});

		return dialog;
	}

	/**
	 * Create dialog for pick brush type
	 * 
	 * @return Alert Dialog
	 */
	private CustomAlertDialog getDialogForBrushType() {

		BrushAdapter adapter = new BrushAdapter(PaintViewSample.this,
				getResources().getStringArray(R.array.brush_list));

		final CustomAlertDialog dialog = new CustomAlertDialog(
				PaintViewSample.this, "", R.style.CustomListDialogStyle);
		dialog.setTitle(getString(R.string.brush_type));

		dialog.setAdapter(adapter, new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPaintView.setBrushType(position);
				eraserButton.setSelected(mPaintView.isEraserEnable());
				dialog.dismiss();
			}
		});

		return dialog;
	}

	@Override
	public void onDrawingStart() {
	}

	@Override
	public void onDrawingEnd() {
		checkUndoRedoState();
	}

	@Override
	public void onUndoRedoPerformed() {
		checkUndoRedoState();
	}

}
