package com.learn.playground;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.learn.playground.utils.FlipImageAnimation;

public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// Animating Logo
		ImageView logo = (ImageView) findViewById(R.id.logo);

		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setDuration(100);

		FlipImageAnimation flipAnimation = new FlipImageAnimation(
				SplashActivity.this, logo, R.drawable.img_logo);
		flipAnimation.setRepetition(2);
		flipAnimation.setDuration(800);

		flipAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						startIntent(SplashActivity.this, PlayZone.class);
					}
				}, 100);
			}
		});
		logo.startAnimation(fadeIn);
		logo.startAnimation(flipAnimation);

	}
}
