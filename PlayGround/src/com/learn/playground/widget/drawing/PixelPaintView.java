package com.learn.playground.widget.drawing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.learn.playground.R;
import com.learn.playground.utils.PlayGroundUtils;
import com.learn.playground.widget.drawing.PanAndZoomListener.Anchor;

public class PixelPaintView extends View implements BaseMovableView {

	private static final int BRUSH_SUPER_TYPE_PATH = 0;
	private static final int BRUSH_SUPER_TYPE_IMAGE = 1;

	private static final int BRUSH_TYPE_ERASER = -1;
	public static final int BRUSH_TYPE_NORMAL = 0;
	public static final int BRUSH_TYPE_EMBOSS = 1;
	public static final int BRUSH_TYPE_BLUR = 2;
	public static final int BRUSH_TYPE_SPRAY_PAINT = 3;
	public static final int BRUSH_TYPE_CRAYON_PAINT = 4;
	public static final int BRUSH_TYPE_CHALK_PAINT = 5;

	public static final int BITMAP_COUNT = 2;
	public static final int BITMAP_INDEX_SPRAY_PAINT = 0;
	public static final int BITMAP_INDEX_CRAYON_PAINT = 1;

	private boolean mIsErase = false;

	private int mBrushSize;
	private Paint mBrushPaint;
	private Paint mCanvasPaint;

	private int mSelectedColor;

	protected int[] colors;
	protected int width, height;

	private int historyIndex;
	private float previousX = -1, previousY = -1;

	private Path path = new Path();
	private List<SnapShot> history;

	private int mBrushType;
	private int mBrushSuperType;

	private EmbossMaskFilter mEmbossFilter;
	private BlurMaskFilter mBlurMaskFilter;
	private PorterDuffColorFilter mColorFilter;

	private Bitmap mCanvasBitmap;
	private Canvas mDrawingCanvas;

	private OnDrawingStateChangeListener mListener;

	private Bitmap[] brushBitmaps;
	private int currentbrushImageIndex = -1;

	private boolean isViewMoving = false;

	private PanAndZoomListener mMoveListener;

	private Matrix mBitmapMatrix;
	private Matrix mInvertMatrix;

	public PixelPaintView(Context context) {
		super(context);
		setupDrawing();
	}

	public PixelPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	public PixelPaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupDrawing();
	}

	private void setupDrawing() {

		mBrushPaint = new Paint();
		mCanvasPaint = new Paint();
		brushBitmaps = new Bitmap[BITMAP_COUNT];

		// Setting default values
		mBrushType = BRUSH_TYPE_NORMAL;
		mBrushSuperType = BRUSH_SUPER_TYPE_PATH;
		mBrushSize = getResources().getDimensionPixelSize(
				R.dimen.min_brush_size);
		mSelectedColor = getResources()
				.getColor(R.color.default_selected_color);
		mCanvasPaint = new Paint(Paint.DITHER_FLAG);

		history = new LinkedList<SnapShot>();
		historyIndex = -1;

		mEmbossFilter = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6,
				3.5f);

		mBlurMaskFilter = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);

		mColorFilter = new PorterDuffColorFilter(mSelectedColor,
				PorterDuff.Mode.SRC_ATOP);

		// Initializing Brush Bitmaps
		brushBitmaps[BITMAP_INDEX_SPRAY_PAINT] = PlayGroundUtils.getBitmapFromAsset(
				getContext(), "brush_image/spray_brush.png");
		brushBitmaps[BITMAP_INDEX_CRAYON_PAINT] = PlayGroundUtils.getBitmapFromAsset(
				getContext(), "brush_image/crayon_brush.png");

		mBitmapMatrix = new Matrix();
		mInvertMatrix = new Matrix();

		initializeBrushPaint();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		this.width = w;
		this.height = h;

		mMoveListener = new PanAndZoomListener(this, Anchor.TOPLEFT, w, h);

		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mDrawingCanvas = new Canvas(mCanvasBitmap);

		newCanvas();

		int[] pixels = new int[w * h];

		mCanvasBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
	}

	private void newCanvas() {
		history.clear();
		historyIndex = -1;
		colors = new int[width * height];

		// for (int i = 0; i < (width * height); i++) {
		// colors[i] = Color.WHITE;
		// }
		// mCanvasBitmap.setPixels(colors, 0, width, 0, 0, width, height);
		// mDrawingCanvas.drawBitmap(colors, 0, width, 0, 0, width, height,
		// false,
		// new Paint());
		mDrawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();
		addCurrentBitmapToHistory();

	}

	private void addCurrentBitmapToHistory() {

		int[] pixels = new int[width * height];

		mCanvasBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		SnapShot saved = new SnapShot(pixels);

		if (history.size() == 8) {
			history.remove(0);
			historyIndex--;
		}

		historyIndex++;

		history.add(historyIndex, saved);
		history = history.subList(0, historyIndex + 1);
	}

	private void drawImagePath(MotionEvent event) {

		if (previousX == -1 || previousY == -1) {
			previousX = event.getX();
			previousY = event.getY();
			return;
		}

		final int historySize = event.getHistorySize();
		final int pointerCount = event.getPointerCount();

		for (int h = 0; h < historySize; h++) {
			for (int p = 0; p < pointerCount; p++) {
				int historicalX = (int) event.getHistoricalX(p, h);
				int historicalY = (int) event.getHistoricalY(p, h);
				interpolatePoints(historicalX, historicalY, previousX,
						previousY);
				invalidate();
				previousX = historicalX;
				previousY = historicalY;
			}
		}

		float x = event.getX();
		float y = event.getY();

		interpolatePoints(x, y, previousX, previousY);
		invalidate();
		previousX = x;
		previousY = y;
	}

	private void interpolatePoints(double X1, double Y1, double X2, double Y2) {

		if (X2 == -1 || Y2 == -1)
			return;

		interpolate(X1, Y1, X2, Y2);
	}

	private void interpolate(double X1, double Y1, double X2, double Y2) {

		if (Math.abs(X2 - X1) <= 3) {
			double y1 = Math.min(Y1, Y2);
			double y2 = Math.max(Y1, Y2);

			for (int i = (int) y1; i <= (int) y2; i += 2) {
				drawBrush((int) X1, i);
			}
			return;
		}
		double m = (Y2 - Y1) / (X2 - X1);
		double b = Y1 - m * X1;

		if (m < 1.0) { // slope is less than 1, interpolate along x
			double x1 = Math.min(X1, X2);
			double x2 = Math.max(X1, X2);

			for (int i = (int) x1; i <= (int) x2; i += 5) {
				int yCalc = (int) (m * i + b);

				drawBrush(i, yCalc);
			}
		} else {// interpolate along y
			double y1 = Math.min(Y1, Y2);
			double y2 = Math.max(Y1, Y2);

			for (int i = (int) y1; i <= (int) y2; i += 10) {
				int xCalc = (int) ((i - b) / m);
				drawBrush(xCalc, i);
			}
		}
	}

	private void drawBrush(int x_coord, int y_coord) {

		if (mBrushType != BRUSH_TYPE_CHALK_PAINT) {
			mDrawingCanvas.drawBitmap(brushBitmaps[currentbrushImageIndex],
					null, getRect(x_coord, y_coord, mBrushSize), mBrushPaint);
		} else {
			drawSprayPaint(x_coord, y_coord);
		}
	}

	private void drawSprayPaint(int x_coord, int y_coord) {
		int X = x_coord;
		int Y = y_coord;

		int r = mBrushSize / 2;
		int f = 1 - r;
		int ddF_x = 1;
		int ddF_y = -2 * r;
		int x = 0;
		int y = r;

		for (int i = Y - r; i <= Y + r; i++)
			try {
				int num = (int) (Math.random() * 20) + 1;
				if (num % 10 == 0)
					mCanvasBitmap.setPixel(X, i, mSelectedColor);
			} catch (IllegalArgumentException e1) {

			}
		for (int i = X - r; i <= X + r; i++)
			try {
				int num = (int) (Math.random() * 20) + 1;
				if (num % 10 == 0)
					mCanvasBitmap.setPixel(i, Y, mSelectedColor);
			} catch (IllegalArgumentException e1) {

			}

		while (x < y) {

			if (f >= 0) {
				y--;
				ddF_y += 2;
				f += ddF_y;
			}
			x++;
			ddF_x += 2;
			f += ddF_x;

			for (int i = X - x; i <= X + x; i++)
				try {
					int num = (int) (Math.random() * 20) + 1;
					if (num % 10 == 0)
						mCanvasBitmap.setPixel(i, Y + y, mSelectedColor);
				} catch (IllegalArgumentException e1) {

				}
			for (int i = X - x; i <= X + x; i++)
				try {
					int num = (int) (Math.random() * 20) + 1;
					if (num % 10 == 0)
						mCanvasBitmap.setPixel(i, Y - y, mSelectedColor);
				} catch (IllegalArgumentException e1) {

				}
			for (int i = X - y; i <= X + y; i++)
				try {
					int num = (int) (Math.random() * 20) + 1;
					if (num % 10 == 0)
						mCanvasBitmap.setPixel(i, Y + x, mSelectedColor);
				} catch (IllegalArgumentException e1) {

				}
			for (int i = X - y; i <= X + y; i++)
				try {
					int num = (int) (Math.random() * 20) + 1;
					if (num % 10 == 0)
						mCanvasBitmap.setPixel(i, Y - x, mSelectedColor);
				} catch (IllegalArgumentException e1) {

				}
		}
	}

	private RectF getRect(float x, float y, float brushSize) {
		RectF dstRect = new RectF(x - (brushSize / 2f), y - (brushSize / 2f), x
				+ (brushSize / 2f), y + (brushSize / 2f));

		return dstRect;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mCanvasBitmap, mBitmapMatrix, mCanvasPaint);
		super.onDraw(canvas);
	}

	private void initializeBrushPaint() {
		mBrushPaint.setColor(mSelectedColor);
		mBrushPaint.setAntiAlias(true);
		mBrushPaint.setStrokeWidth(mBrushSize);
		mBrushPaint.setStyle(Paint.Style.STROKE);
		mBrushPaint.setStrokeJoin(Paint.Join.ROUND);
		mBrushPaint.setStrokeCap(Paint.Cap.ROUND);
		mBrushPaint.setPathEffect(new CornerPathEffect(mBrushSize / 2));
	}

	public void setOnDrawingStateChangeListener(
			OnDrawingStateChangeListener listener) {
		this.mListener = listener;
	}

	public void setBrushSize(int size) {
		this.mBrushSize = size;
		mBrushPaint.setStrokeWidth(mBrushSize);
	}

	public int getBrushSize() {
		return this.mBrushSize;
	}

	public void setBrushType(int brushType) {
		setErase(false);
		this.mBrushType = brushType;
		setBrushTypeInternal(brushType);
	}

	public void setErase(boolean isErase) {
		this.mIsErase = isErase;
		if (isErase)
			setBrushTypeInternal(BRUSH_TYPE_ERASER);
		else
			setBrushTypeInternal(mBrushType);
	}

	public void setViewMovable(boolean isMovable) {
		this.isViewMoving = isMovable;
	}

	public boolean isViewMovable() {
		return this.isViewMoving;
	}

	protected void setBrushTypeInternal(int brushType) {
		switch (brushType) {

		case BRUSH_TYPE_ERASER:
			mBrushSuperType = BRUSH_SUPER_TYPE_PATH;
			mBrushPaint.setXfermode(new PorterDuffXfermode(
					PorterDuff.Mode.CLEAR));
			mBrushPaint.setMaskFilter(null);
			mBrushPaint.setColorFilter(null);
			currentbrushImageIndex = -1;
			break;

		case BRUSH_TYPE_NORMAL:
			mBrushSuperType = BRUSH_SUPER_TYPE_PATH;
			mBrushPaint.setXfermode(null);
			mBrushPaint.setMaskFilter(null);
			mBrushPaint.setColorFilter(null);
			currentbrushImageIndex = -1;
			break;

		case BRUSH_TYPE_EMBOSS:
			mBrushSuperType = BRUSH_SUPER_TYPE_PATH;
			mBrushPaint.setXfermode(null);
			mBrushPaint.setMaskFilter(mEmbossFilter);
			mBrushPaint.setColorFilter(null);
			currentbrushImageIndex = -1;
			break;

		case BRUSH_TYPE_BLUR:
			mBrushSuperType = BRUSH_SUPER_TYPE_PATH;
			mBrushPaint.setXfermode(null);
			mBrushPaint.setMaskFilter(mBlurMaskFilter);
			mBrushPaint.setColorFilter(null);
			currentbrushImageIndex = -1;
			break;

		case BRUSH_TYPE_SPRAY_PAINT:
			setImageBrush(BITMAP_INDEX_SPRAY_PAINT);
			break;

		case BRUSH_TYPE_CRAYON_PAINT:
			setImageBrush(BITMAP_INDEX_CRAYON_PAINT);
			break;

		case BRUSH_TYPE_CHALK_PAINT:
			setImageBrush(-1);
			break;

		}
	}

	private void setImageBrush(int index) {
		mBrushSuperType = BRUSH_SUPER_TYPE_IMAGE;
		mBrushPaint.setXfermode(null);
		mBrushPaint.setMaskFilter(null);
		mBrushPaint.setColorFilter(mColorFilter);
		currentbrushImageIndex = index;
	}

	public boolean isEraserEnable() {
		return this.mIsErase;
	}

	public void startNew() {
		newCanvas();

		if (mListener != null) {
			mListener.onUndoRedoPerformed();
		}

	}

	public void setColor(int newColor) {
		mSelectedColor = newColor;

		mColorFilter = new PorterDuffColorFilter(mSelectedColor,
				PorterDuff.Mode.SRC_ATOP);

		if (mBrushSuperType == BRUSH_SUPER_TYPE_IMAGE) {
			mBrushPaint.setColorFilter(mColorFilter);
		}

		mBrushPaint.setColor(mSelectedColor);
	}

	public int getColor() {
		return this.mSelectedColor;
	}

	public boolean getUndoState() {
		return historyIndex > 0;
	}

	public boolean getRedoState() {
		return historyIndex + 1 < history.size();
	}

	public void performUndo() {
		if (historyIndex > 0) {
			historyIndex--;
			SnapShot prev = history.get(historyIndex);
			mCanvasBitmap.setPixels(prev.getPixels(), 0, width, 0, 0, width,
					height);

			if (mListener != null) {
				mListener.onUndoRedoPerformed();
			}

			invalidate();
		}
	}

	public void performRedo() {

		if (historyIndex + 1 < history.size()) {
			historyIndex++;
			SnapShot next = history.get(historyIndex);
			mCanvasBitmap.setPixels(next.getPixels(), 0, width, 0, 0, width,
					height);

			if (mListener != null) {
				mListener.onUndoRedoPerformed();
			}

			invalidate();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isViewMoving) {

			return mMoveListener.onTouch(this, event);

		} else {

			event.transform(mInvertMatrix);

			if (event.getPointerCount() == 1) {
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_UP:

					previousX = -1;
					previousY = -1;
					path = new Path();
					addCurrentBitmapToHistory();

					if (mListener != null) {
						mListener.onDrawingEnd();
					}
					break;

				case MotionEvent.ACTION_DOWN:
					if (mListener != null) {
						mListener.onDrawingStart();
					}
				case MotionEvent.ACTION_MOVE:
					doDrawing(event);
					break;

				}
				return true;
			}
		}
		return false;
	}

	private void doDrawing(MotionEvent event) {

		switch (mBrushSuperType) {
		case BRUSH_SUPER_TYPE_IMAGE:
			drawImagePath(event);
			break;

		case BRUSH_SUPER_TYPE_PATH:
			drawCubicPath(event);
			break;
		}
	}

	private void drawCubicPath(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP)
			return;

		float x = event.getX();
		float y = event.getY();

		if (previousX == -1 && previousY == -1) { // check to see if it is the
													// first point
			path.moveTo(x, y);
			previousX = x;
			previousY = y;
		}

		else {

			// path.reset();
			// path.moveTo(previousX, previousY);

			ArrayList<Point> points = new ArrayList<Point>();
			points.add(new Point((int) previousX, (int) previousY));

			int historySize = event.getHistorySize();
			int pointerCount = event.getPointerCount();

			for (int h = 0; h < historySize; h++) {
				for (int p = 0; p < pointerCount; p++) {
					int historicalX = (int) event.getHistoricalX(p, h);
					int historicalY = (int) event.getHistoricalY(p, h);

					points.add(new Point(historicalX, historicalY));
				}
			}
			points.add(new Point((int) x, (int) y));

			// at this point the 'points' arraylist contains a list of points
			// that we want to join

			path = splineInterp(points, path);

			mDrawingCanvas.drawPath(path, mBrushPaint);

			invalidate();
		}

		previousX = x;
		previousY = y;
	}

	private Path splineInterp(List<Point> points, Path path) {
		for (int i = 1; i < points.size(); i++) {
			Point prev = points.get(i - 1);
			Point curr = points.get(i);

			if (i == 1) {
				path.quadTo(prev.x, prev.y, curr.x, curr.y);

			} else {
				Point prev2 = points.get(i - 2);
				path.cubicTo(prev2.x, prev2.y, prev.x, prev.y, curr.x, curr.y);
			}
		}

		return path;
	}

	public class SnapShot {

		private int[] mPixels;

		public SnapShot(int[] pixels) {
			mPixels = pixels;
		}

		public int[] getPixels() {
			return mPixels;
		}

	}

	public interface OnDrawingStateChangeListener {

		public void onDrawingStart();

		public void onDrawingEnd();

		public void onUndoRedoPerformed();

	}

	@Override
	public void setImageMatrix(Matrix matrix) {
		mBitmapMatrix = matrix;
		mBitmapMatrix.invert(mInvertMatrix);
		postInvalidate();
	}

	@Override
	public Bitmap getBitmap() {
		return mCanvasBitmap;
	}

	@Override
	public Matrix getImageMatrix() {
		return mBitmapMatrix;
	}

}