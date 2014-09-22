package com.learn.playground.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

public class FlipImageAnimation extends Animation {

	private Camera camera;

	private float centerX;
	private float centerY;

	private ImageView view;
	private Drawable fromDrawable;
	private Drawable toDrawable;
	private boolean forward;

	private int mRepetition;

	private Context mContext;

	/**
	 * Creates a 3D flip animation between two views.
	 * 
	 * @param fromView
	 *            First view in the transition.
	 * @param toView
	 *            Second view in the transition.
	 */
	public FlipImageAnimation(Context context, ImageView imageView,
			int fromDrawable, int toDrawable) {
		initAnim(context, imageView, fromDrawable);
		this.toDrawable = mContext.getResources().getDrawable(toDrawable);
	}

	public FlipImageAnimation(Context context, ImageView imageView,
			int fromDrawable) {
		initAnim(context, imageView, fromDrawable);
		this.toDrawable = getFlippedDrawable(fromDrawable);

	}

	private void initAnim(Context context, ImageView imageView, int fromDrawable) {

		this.mContext = context;
		this.view = imageView;
		mRepetition = 4;
		this.fromDrawable = mContext.getResources().getDrawable(fromDrawable);
		view.setImageDrawable(this.fromDrawable);
		forward = true;
		setDuration(2000);
		setFillAfter(false);
		setInterpolator(new AccelerateDecelerateInterpolator());
	}

	/**
	 * One repetition is equals to one flip i.e. Odd number is equals to flipped
	 * image and even means normalImage
	 * 
	 * @param repetation
	 */

	public void setRepetition(int repetition) {
		this.mRepetition = repetition;
	}

	@Override
	public void setAnimationListener(AnimationListener listener) {

		super.setAnimationListener(listener);
	}

	public void isForward(boolean isForward) {

		if (isForward != forward) {
			swapDrawables();
		}

		forward = isForward;
	}

	private void swapDrawables() {
		Drawable switchDrawable = toDrawable;
		toDrawable = fromDrawable;
		fromDrawable = switchDrawable;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		centerX = width / 2;
		centerY = height / 2;
		camera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float delta = 1f / mRepetition;
		float x1 = 0;
		int i = 0;
		while (i < mRepetition) {
			if (interpolatedTime < delta * i)
				break;
			i++;
		}
		x1 = delta * (i-1);		
		float mappedTime = ((interpolatedTime - x1) * mRepetition) < 1 ? ((interpolatedTime - x1) * mRepetition)
				: 1;
		createSingleFlip(mappedTime, t, i);
	}

	private void createSingleFlip(float interpolatedTime, Transformation t, int currentRepetition) {
		// Angle around the y-axis of the rotation at the given time
		// calculated both in radians and degrees.
		final double radians = Math.PI * interpolatedTime;
		float degrees = (float) (180.0 * radians / Math.PI);

		// Once we reach the midpoint in the animation, we need to hide the
		// source view and show the destination view. We also need to change
		// the angle by 180 degrees so that the destination does not come in
		// flipped around
		if (interpolatedTime >= 0.5f) {
			degrees -= 180.f;
			if (currentRepetition%2 == 0)
				view.setImageDrawable(fromDrawable);
			else
				view.setImageDrawable(toDrawable);
		}

		if (forward)
			degrees = -degrees; // determines direction of rotation when flip
								// begins

		final Matrix matrix = t.getMatrix();
		camera.save();
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		camera.restore();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}

	private Drawable getFlippedDrawable(int drawableId) {

		Matrix matrix = new Matrix();
		matrix.preScale(-1, 1);

		Bitmap src = BitmapFactory.decodeResource(mContext.getResources(),
				drawableId);

		Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
				src.getHeight(), matrix, false);

		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		dst.setDensity((int) (metrics.density * 160f));

		return new BitmapDrawable(mContext.getResources(), dst);
	}
}
