package com.learn.playground.widget.drawing;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.learn.playground.R;
import com.learn.playground.utils.PlayGroundUtils;

public class PaintView extends View {

	private static final int BRUSH_SUPER_TYPE_PATH = 0;
	private static final int BRUSH_SUPER_TYPE_IMAGE = 1;

	private static final int BRUSH_TYPE_ERASER = -1;
	public static final int BRUSH_TYPE_NORMAL = 0;
	public static final int BRUSH_TYPE_EMBOSS = 1;
	public static final int BRUSH_TYPE_BLUR = 2;
	public static final int BRUSH_TYPE_SPRAY_PAINT = 3;
	public static final int BRUSH_TYPE_CRAYON_PAINT = 4;

	public static final int BITMAP_COUNT = 2;
	public static final int BITMAP_INDEX_SPRAY_PAINT = 0;
	public static final int BITMAP_INDEX_CRAYON_PAINT = 1;

	private boolean mIsErase = false;

	private int mBrushSize;
	private Paint mBrushPaint;
	private Paint mCanvasPaint;
	private int mSelectedColor;
	private ArrayList<PathState> paths;
	private ArrayList<PathState> bufferPaths;

	private Path tempPath;
	private int mBrushType;
	private int mBrushSuperType;

	private Bitmap mCanvasBitmap;
	private Canvas mDrawingCanvas;

	private EmbossMaskFilter mEmbossFilter;
	private BlurMaskFilter mBlurMaskFilter;
	private PorterDuffColorFilter mColorFilter;

	private OnDrawingStateChangeListener mListener;

	private Bitmap[] brushBitmaps;
	private int currentbrushImageIndex = -1;
	private ArrayList<Vector2> mPoints;

	public PaintView(Context context) {
		super(context);
		setupDrawing();
	}

	public PaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	public PaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupDrawing();
	}

	private void setupDrawing() {

		paths = new ArrayList<PathState>();
		bufferPaths = new ArrayList<PathState>();
		mBrushPaint = new Paint();
		mCanvasPaint = new Paint();
		tempPath = new Path();
		brushBitmaps = new Bitmap[BITMAP_COUNT];
		mPoints = new ArrayList<Vector2>();

		// Setting default values
		mBrushType = BRUSH_TYPE_NORMAL;
		mBrushSuperType = BRUSH_SUPER_TYPE_PATH;
		mBrushSize = getResources().getDimensionPixelSize(
				R.dimen.min_brush_size);
		mSelectedColor = getResources()
				.getColor(R.color.default_selected_color);
		mCanvasPaint = new Paint(Paint.DITHER_FLAG);

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

		initializeBrushPaint();

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mDrawingCanvas = new Canvas(mCanvasBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);

		if (!mIsErase && mBrushType == BRUSH_TYPE_NORMAL)
			canvas.drawPath(tempPath, mBrushPaint);

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
		paths.clear();
		bufferPaths.clear();
		if (mListener != null) {
			mListener.onUndoRedoPerformed();
		}
		mDrawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();
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
		return paths.size() > 0;
	}

	public boolean getRedoState() {
		return bufferPaths.size() > 0;
	}

	public boolean performUndo() {
		mDrawingCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		if (paths.size() > 0) {

			bufferPaths.add(paths.get(paths.size() - 1));
			paths.remove(paths.size() - 1);

			for (PathState pathState : paths) {
				drawPath(mDrawingCanvas, pathState);
			}

			if (mListener != null) {
				mListener.onUndoRedoPerformed();
			}

			invalidate();
			return true;
		}
		return false;
	}

	public boolean performRedo() {

		if (bufferPaths.size() > 0) {
			drawPath(mDrawingCanvas, bufferPaths.get(bufferPaths.size() - 1));
			paths.add(bufferPaths.get(bufferPaths.size() - 1));
			bufferPaths.remove(bufferPaths.size() - 1);
			invalidate();

			if (mListener != null) {
				mListener.onUndoRedoPerformed();
			}

			return true;
		}
		return false;

	}

	private void drawPath(Canvas canvas, PathState state) {
		switch (state.getType()) {
		case BRUSH_SUPER_TYPE_IMAGE:
			for (Vector2 p : state.getPoints()) {
				canvas.drawBitmap(brushBitmaps[state.getBrushBitmapIndex()],
						null,
						getRect(p.x, p.y, state.getPaint().getStrokeWidth()),
						state.getPaint());
			}
			break;

		case BRUSH_SUPER_TYPE_PATH:
			canvas.drawPath(state.getPath(), state.getPaint());
			break;

		default:
			break;
		}

	}

	private RectF getRect(float x, float y, float brushSize) {
		RectF dstRect = new RectF(x - (brushSize / 2f), y - (brushSize / 2f), x
				+ (brushSize / 2f), y + (brushSize / 2f));

		return dstRect;
	}

	@SuppressWarnings("unused")
	private ArrayList<Vector2> getInterpolatedPoints(Vector2 lastPoint,
			Vector2 newPoint, float delta) {
		float deltaX = newPoint.x - lastPoint.x;
		float deltaY = newPoint.y - lastPoint.y;
		float distance = lastPoint.distance(newPoint);
		float unitX = deltaX / distance;
		float unitY = deltaY / distance;
		float tempDist = 0;

		ArrayList<Vector2> interpolatedPoints = new ArrayList<PaintView.Vector2>();

		Vector2 pt = new Vector2(lastPoint.x, lastPoint.y);

		while (distance >= tempDist) {

			pt.x = pt.x + unitX * delta;
			pt.y = pt.y + unitY * delta;

			tempDist = lastPoint.distance(pt);
			interpolatedPoints.add(pt);
		}
		return interpolatedPoints;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float touchX = event.getX();
		float touchY = event.getY();

		// respond to down, move and up events
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:

			if (mListener != null) {
				mListener.onDrawingStart();
			}
			bufferPaths.clear();

			if (mBrushSuperType == BRUSH_SUPER_TYPE_PATH) {
				tempPath.moveTo(touchX, touchY);
			}

			break;

		case MotionEvent.ACTION_MOVE:

			if (mBrushSuperType == BRUSH_SUPER_TYPE_PATH) {

				tempPath.lineTo(touchX, touchY);
				if (mIsErase || mBrushType != BRUSH_TYPE_NORMAL)
					mDrawingCanvas.drawPath(tempPath, mBrushPaint);

			} else if (mBrushSuperType == BRUSH_SUPER_TYPE_IMAGE) {

				mDrawingCanvas.drawBitmap(brushBitmaps[currentbrushImageIndex],
						null, getRect(touchX, touchY, mBrushSize), mBrushPaint);

				mPoints.add(new Vector2(touchX, touchY));
			}

			break;

		case MotionEvent.ACTION_UP:

			if (mBrushSuperType == BRUSH_SUPER_TYPE_PATH) {

				tempPath.lineTo(touchX, touchY);
				if (!mIsErase && mBrushType == BRUSH_TYPE_NORMAL)
					mDrawingCanvas.drawPath(tempPath, mBrushPaint);
				paths.add(new PathState(tempPath, mBrushPaint));
				tempPath.reset();

			} else if (mBrushSuperType == BRUSH_SUPER_TYPE_IMAGE) {
				paths.add(new PathState(mPoints, currentbrushImageIndex,
						mBrushPaint));
				mPoints.clear();
			}

			if (mListener != null) {
				mListener.onDrawingEnd();
			}
			break;

		default:
			return false;

		}

		invalidate();
		return true;
	}

	private class PathState {

		Path path;
		Paint paint;

		ArrayList<Vector2> points;
		int brushBitmapIndex;

		int pathType;

		public PathState(Path path, Paint paint) {
			this.pathType = BRUSH_SUPER_TYPE_PATH;
			this.path = new Path(path);
			this.paint = new Paint(paint);
		}

		public PathState(ArrayList<Vector2> points, int bitmapIndex, Paint paint) {
			this.pathType = BRUSH_SUPER_TYPE_IMAGE;
			this.brushBitmapIndex = bitmapIndex;
			this.points = new ArrayList<Vector2>(points);
			this.paint = new Paint(paint);
		}

		public ArrayList<Vector2> getPoints() {
			return this.points;
		}

		public int getType() {
			return this.pathType;
		}

		public int getBrushBitmapIndex() {
			return this.brushBitmapIndex;
		}

		public Path getPath() {
			return this.path;
		}

		public Paint getPaint() {
			return this.paint;
		}
	}

	private static final class Vector2 {
		public Vector2(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public float x;
		public float y;

		public float distance(Vector2 p) {
			return (float) Math.sqrt((x - p.x) * (x - p.x) + (y - p.y)
					* (y - p.y));
		}
	}

	public interface OnDrawingStateChangeListener {

		public void onDrawingStart();

		public void onDrawingEnd();

		public void onUndoRedoPerformed();

	}

}
