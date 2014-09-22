/**
 * 
 */
package com.learn.playground.particleanim;

//import net.obviam.particles.model.ElaineAnimated;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author impaler This is the main surface that handles the ontouch events and
 *         draws the image to the screen.
 */
public class ParticleAnimationHandlerView extends View {

	private static final String TAG = ParticleAnimationHandlerView.class
			.getSimpleName();

	private static final boolean IS_DEBUG = true;

	private static final int EXPLOSION_SIZE = 40;

	private Explosion explosion;

	private Bitmap mParticeBitmap;

	private Handler handler;

	// the fps to be displayed
	private int avgFps = 60;

	private ParticleAnimationListener mListener;
	public boolean animationEnded = false;

	public void setAvgFps(int avgFps) {
		this.avgFps = avgFps;
	}

	public ParticleAnimationHandlerView(Context context) {
		super(context);
		init();
	}

	public ParticleAnimationHandlerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ParticleAnimationHandlerView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {

	}

	public void setAnimationListener(ParticleAnimationListener listener) {
		mListener = listener;
	}

	public void setBitmap(Bitmap bitmap) {
		mParticeBitmap = bitmap;
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

		animationEnded = false;
		if (explosion == null || explosion.getState() == Explosion.STATE_DEAD) {
			explosion = new Explosion(EXPLOSION_SIZE, x, y);
			handler = new Handler();
			handler.post(animator);
		}
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
	public void update() {
		// update explosions
		if (explosion != null && explosion.isAlive()) {
			
			explosion.update();
			
		} else {
			
			createLog("Animation End");
			
			animationEnded = true;
			handler.removeCallbacks(animator);
			
			if (mListener != null)
				mListener.onAnimationEnd();
			
		}
	}

	private Runnable animator = new Runnable() {
		@Override
		public void run() {

			if (animationEnded)
				return;
			else {
				update();
				postInvalidate();
				handler.postDelayed(this, (long) (1000 / avgFps));
				createLog("Updating");
			}

		}
	};

	public interface ParticleAnimationListener {

		public void onAnimationEnd();

	}

	private void createLog(String text) {
		if (IS_DEBUG) {
			Log.v(TAG, text);
		}

	}

}
