
package com.learn.playground;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.view.MenuItem;
import com.learn.playground.box2dview.Box2DView;

public class Box2DViewTest extends BaseActivity implements OnClickListener {  

    private Box2DView spinnerView;    
    private Button resetButton;
 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box2d_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerView = (Box2DView) findViewById(R.id.spinner);
        spinnerView.setup();       

        resetButton = (Button) findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this);

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
            case R.id.resetButton:               
                spinnerView.reset();
                break;
        }
   }

   
}
