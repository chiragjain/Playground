
package com.learn.playground;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.view.MenuItem;
import com.learn.playground.particleanim.ParticleAnimationHandlerView;
import com.learn.playground.particleanim.ParticleAnimationView;

public class ParticleAnimationTest extends BaseActivity implements
        OnClickListener {

    private Button playBothButton;
    private Button playNormally;
    private Button playHandler;

    private ParticleAnimationHandlerView handlerAnim;
    private ParticleAnimationView normalAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle_test);

        playBothButton = (Button) findViewById(R.id.playBoth);
        playBothButton.setOnClickListener(this);

        playNormally = (Button) findViewById(R.id.playNormal);
        playNormally.setOnClickListener(this);

        playHandler = (Button) findViewById(R.id.playHandler);
        playHandler.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setParticleAnimation();
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
     * Particle Animation testing
     */
    private void setParticleAnimation() {

        Bitmap src = BitmapFactory.decodeResource(getResources(),
                R.drawable.img_test_star);

        handlerAnim = (ParticleAnimationHandlerView) findViewById(R.id.particleAnimHandler);
        handlerAnim.setVisibility(View.VISIBLE);
        handlerAnim.setBitmap(src);

        normalAnim = (ParticleAnimationView) findViewById(R.id.particleAnim);
        normalAnim.setVisibility(View.VISIBLE);
        normalAnim.setBitmap(src);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBoth:
                handlerAnim.startAnimation();
                normalAnim.startAnimation();
                break;

            case R.id.playHandler:
                handlerAnim.startAnimation();
                break;

            case R.id.playNormal:
                normalAnim.startAnimation();
                break;
        }
    }

}
