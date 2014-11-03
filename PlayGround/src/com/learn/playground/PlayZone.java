package com.learn.playground;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.learn.playground.adapter.PlayZoneAdapter;

public class PlayZone extends BaseActivity {

	ListView playZoneMenuList;
	PlayZoneAdapter mAdapter;

	private static final int[] LINKS = {
			R.string.title_particle_animation_test,
			R.string.title_activity_paint_view,			
			R.string.title_activity_box2d_view,
			R.string.title_activity_register };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_zone);

		playZoneMenuList = (ListView) findViewById(R.id.playZoneMenuList);

		mAdapter = new PlayZoneAdapter(PlayZone.this, LINKS);

		playZoneMenuList.setAdapter(mAdapter);

		playZoneMenuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long index) {
				switch (LINKS[position]) {
				case R.string.title_particle_animation_test:
					startIntent(PlayZone.this, ParticleAnimationSample.class);
					break;

				case R.string.title_activity_paint_view:
					startIntent(PlayZone.this, PaintViewSample.class);
					break;				

				case R.string.title_activity_box2d_view:
					startIntent(PlayZone.this, Box2DViewSample.class);
					break;

				case R.string.title_activity_register:
					startIntent(PlayZone.this, CountryListSample.class);
					break;
				}

			}
		});
	}

}
