package com.learn.playground.widget.drawing;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PanAndZoomListener implements OnTouchListener {

	public static class Anchor {

		public static final int CENTER = 0;
		public static final int TOPLEFT = 1;
	}

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	
	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	PanZoomCalculator panZoomCalculator;

	public PanAndZoomListener(BaseMovableView view, int anchor, int width,
			int height) {
		panZoomCalculator = new PanZoomCalculator(view, anchor, width, height);
	}

	public boolean onTouch(View view, MotionEvent event) {
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				panZoomCalculator.doPan(event.getX() - start.x, event.getY()
						- start.y);
				start.set(event.getX(), event.getY());
			} else if (mode == ZOOM) {
				float newDist = spacing(event);

				if (newDist > 10f) {
					float scale = newDist / oldDist;
					oldDist = newDist;
					panZoomCalculator.doZoom(scale, mid);
				}
			}
			break;

		}
		return true; // indicate event was handled
	}

	// Determine the space between the first two fingers
	private float spacing(MotionEvent event) {

		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	// Calculate the mid point of the first two fingers
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public class PanZoomCalculator {

		// / The current pan position
		PointF currentPan;
		// / The current zoom position
		float currentZoom;

		BaseMovableView child;

		Matrix matrix;

		// Pan jitter is a workaround to get the video view to update its layout
		// properly when zoom is changed
		int panJitter = 0;
		int anchor;

		int windowWidth;
		int windowHeight;

		PanZoomCalculator(BaseMovableView child, int anchor, int windowWidth,
				int windowHeight) {
			// Initialize class fields
			currentPan = new PointF(0, 0);
			currentZoom = 1f;

			this.windowWidth = windowWidth;
			this.windowHeight = windowHeight;

			this.child = child;
			matrix = new Matrix();
			this.anchor = anchor;
			onPanZoomChanged();
			/*
			 * IS THIS COMPATIBLE WITH 2.3.3?
			 * this.child.addOnLayoutChangeListener(new OnLayoutChangeListener()
			 * { // This catches when the image bitmap changes, for some reason
			 * it doesn't recurse
			 * 
			 * public void onLayoutChange(View v, int left, int top, int right,
			 * int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
			 * { onPanZoomChanged(); } });
			 */
		}

		public void doZoom(float scale, PointF zoomCenter) {

			float oldZoom = currentZoom;

			// multiply in the zoom change
			currentZoom *= scale;

			// this limits the zoom
			currentZoom = Math.max(getMinimumZoom(), currentZoom);
			currentZoom = Math.min(8f, currentZoom);

			// Adjust the pan accordingly
			// Need to make it such that the point under the zoomCenter remains
			// under the zoom center after the zoom

			// calculate in fractions of the image so:

			float width = this.windowWidth;
			float height = this.windowHeight;
			float oldScaledWidth = width * oldZoom;
			float oldScaledHeight = height * oldZoom;
			float newScaledWidth = width * currentZoom;
			float newScaledHeight = height * currentZoom;

			if (anchor == Anchor.CENTER) {

				float reqXPos = ((oldScaledWidth - width) * 0.5f + zoomCenter.x - currentPan.x)
						/ oldScaledWidth;
				float reqYPos = ((oldScaledHeight - height) * 0.5f
						+ zoomCenter.y - currentPan.y)
						/ oldScaledHeight;
				float actualXPos = ((newScaledWidth - width) * 0.5f
						+ zoomCenter.x - currentPan.x)
						/ newScaledWidth;
				float actualYPos = ((newScaledHeight - height) * 0.5f
						+ zoomCenter.y - currentPan.y)
						/ newScaledHeight;

				currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
				currentPan.y += (actualYPos - reqYPos) * newScaledHeight;
			} else {
				// assuming top left
				float reqXPos = (zoomCenter.x - currentPan.x) / oldScaledWidth;
				float reqYPos = (zoomCenter.y - currentPan.y) / oldScaledHeight;
				float actualXPos = (zoomCenter.x - currentPan.x)
						/ newScaledWidth;
				float actualYPos = (zoomCenter.y - currentPan.y)
						/ newScaledHeight;
				currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
				currentPan.y += (actualYPos - reqYPos) * newScaledHeight;
			}

			onPanZoomChanged();
		}

		public void doPan(float panX, float panY) {
			currentPan.x += panX;
			currentPan.y += panY;
			onPanZoomChanged();
		}

		private float getMinimumZoom() {
			return 1f;
		}

		// / Call this to reset the Pan/Zoom state machine
		public void reset() {
			// Reset zoom and pan
			currentZoom = getMinimumZoom();
			currentPan = new PointF(0f, 0f);
			onPanZoomChanged();
		}

		public void onPanZoomChanged() {

			// Things to try: use a scroll view and set the pan from the
			// scrollview
			// when panning, and set the pan of the scroll view when zooming

			float winWidth = this.windowWidth;
			float winHeight = this.windowHeight;

			if (currentZoom <= 1f) {
				currentPan.x = 0;
				currentPan.y = 0;
			} else if (anchor == Anchor.CENTER) {

				float maxPanX = (currentZoom - 1f) * this.windowWidth * 0.5f;
				float maxPanY = (currentZoom - 1f) * this.windowHeight * 0.5f;
				currentPan.x = Math.max(-maxPanX,
						Math.min(maxPanX, currentPan.x));
				currentPan.y = Math.max(-maxPanY,
						Math.min(maxPanY, currentPan.y));
			} else {
				// assume top left

				float maxPanX = (currentZoom - 1f) * this.windowWidth;
				float maxPanY = (currentZoom - 1f) * this.windowHeight;
				currentPan.x = Math.max(-maxPanX, Math.min(0, currentPan.x));
				currentPan.y = Math.max(-maxPanY, Math.min(0, currentPan.y));
			}

			Bitmap bm = child.getBitmap();
			if (bm != null) {
				// Limit Pan

				float bmWidth = bm.getWidth();
				float bmHeight = bm.getHeight();

				float fitToWindow = Math.min(winWidth / bmWidth, winHeight
						/ bmHeight);
				float xOffset = (winWidth - bmWidth * fitToWindow) * 0.5f
						* currentZoom;
				float yOffset = (winHeight - bmHeight * fitToWindow) * 0.5f
						* currentZoom;

				matrix.reset();
				matrix.postScale(currentZoom * fitToWindow, currentZoom
						* fitToWindow);
				matrix.postTranslate(currentPan.x + xOffset, currentPan.y
						+ yOffset);
				child.setImageMatrix(matrix);

			}
		}
	}
}
