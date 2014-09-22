/**
 * 
 */
package com.learn.playground.particleanim;

//import net.obviam.particles.model.ElaineAnimated;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * @author impaler This is the main surface that handles the ontouch events and
 *         draws the image to the screen.
 */
public class ParticleAnimationView extends View {

	private static final String TAG = ParticleAnimationView.class
			.getSimpleName();

	private static final boolean IS_DEBUG = true;

	private static final int EXPLOSION_SIZE = 40;

	private Explosion explosion;

	private Bitmap mParticeBitmap;

	private CustomAnimation mAnimation;

	private int mDuration;

	private Interpolator mInterpolator;

	private ParticleAnimationListener mListener;

	public ParticleAnimationView(Context context) {
		super(context);
		init();
	}

	public ParticleAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ParticleAnimationView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		this.mDuration = 300;
		this.mInterpolator = new AccelerateDecelerateInterpolator();
		mAnimation = new CustomAnimation();
	}

	public void setAnimationListener(ParticleAnimationListener listener) {
		mListener = listener;

		mAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mListener.onAnimationEnd();

			}
		});
	}

	public void setBitmap(Bitmap bitmap) {
		mParticeBitmap = bitmap;
	}

	public void setDuration(int duration) {
		this.mDuration = duration;
	}

	public void setInterpolator(Interpolator interpolator) {
		this.mInterpolator = interpolator;
	}

	public void render(Canvas canvas) {

		// render explosions
		if (explosion != null) {
			if (mParticeBitmap != null)
				explosion.draw(mParticeBitmap, canvas);
			else
				explosion.draw(canvas);
		}
	}

	public void startAnimation(int x, int y) {

		createLog("Starting Animation");
		if (explosion == null || explosion.getState() == Explosion.STATE_DEAD) {
			explosion = new Explosion(EXPLOSION_SIZE, x, y);
		}

		mAnimation.setDuration(mDuration);
		mAnimation.setInterpolator(mInterpolator);
		startAnimation(mAnimation);

		createLog("Started");
	}

	public void startAnimation() {
		startAnimation(getWidth() / 2, getHeight() / 2);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		render(canvas);
		super.onDraw(canvas);
	}

	/**
	 * This is the game update method. It iterates through all the objects and
	 * calls their update method if they have one or calls specific engine's
	 * update method.
	 */
	public void update(float interpolatedTime) {
		// update explosions
		if (explosion != null && explosion.isAlive()) {
			explosion.update(interpolatedTime);
		}
	}

	private class CustomAnimation extends Animation {

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			createLog("Updating");
			update(interpolatedTime);
			postInvalidate();
			super.applyTransformation(interpolatedTime, t);
		}

	}

	public interface ParticleAnimationListener {

		public void onAnimationEnd();

	}

	private void createLog(String text) {
		if (IS_DEBUG) {
			Log.v(TAG, text);
		}

	}

}
