package com.example.scrollviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class TestActvitiy extends Activity {

	LinearLayout l_x1, l_x2, l_x3, l_x4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.test);

		MyHorScorllView hs = (MyHorScorllView) findViewById(R.id.hs);
		hs.setImageView((ImageView)findViewById(R.id.imageView3));

		l_x1 = (LinearLayout) findViewById(R.id.l_x1);
		l_x2 = (LinearLayout) findViewById(R.id.l_x2);
		l_x3 = (LinearLayout) findViewById(R.id.l_x3);
		l_x4 = (LinearLayout) findViewById(R.id.l_x4);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		ImageView v = null;
		LinearLayout.LayoutParams params_viewroot = new LinearLayout.LayoutParams(
				(int) (dm.widthPixels),
				// LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		l_x1.setLayoutParams(params_viewroot);
		l_x1.setGravity(Gravity.CENTER);
		v = new ImageView(this);
		v.setScaleType(ScaleType.CENTER);
		v.setImageResource(R.drawable.help004_1);
		l_x1.addView(v);

		l_x2.setLayoutParams(params_viewroot);
		l_x2.setGravity(Gravity.CENTER);
		v = new ImageView(this);
		v.setScaleType(ScaleType.CENTER);
		v.setImageResource(R.drawable.help004_2);
		l_x2.addView(v);

		l_x3.setLayoutParams(params_viewroot);
		l_x3.setGravity(Gravity.CENTER);
		v = new ImageView(this);
		v.setScaleType(ScaleType.CENTER);
		v.setImageResource(R.drawable.help004_3);
		l_x3.addView(v);

		l_x4.setLayoutParams(params_viewroot);
		l_x4.setGravity(Gravity.CENTER);
		v = new ImageView(this);
		v.setScaleType(ScaleType.CENTER);
		v.setImageResource(R.drawable.help004_4);
		l_x4.addView(v);

	}

}
