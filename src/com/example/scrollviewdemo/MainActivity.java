package com.example.scrollviewdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.scrollviewdemo.PersonalScrollView.onTurnListener;
import com.example.scrollviewdemo.RotateAnimation.InterpolatedTimeListener;

public class MainActivity extends Activity implements InterpolatedTimeListener,
		onTurnListener {
	private ImageView iv_personal_bg;
	private ImageView image_header;
	private PersonalScrollView personalScrollView;
	private View line_up;

	private TableLayout tl_main;
	private int current_id;

	private int drawable_id[] = { R.drawable.default_user_hole,
			R.drawable.default_artist_hole,
			R.drawable.default_user_hole_information };

	protected void initView() {
		setContentView(R.layout.main);
		personalScrollView = (PersonalScrollView) findViewById(R.id.personalScrollView);
		iv_personal_bg = (ImageView) findViewById(R.id.iv_personal_bg);
		line_up = (View) findViewById(R.id.line_up);
		image_header = (ImageView) findViewById(R.id.image_header);
		tl_main = (TableLayout) findViewById(R.id.tl_main);

		personalScrollView.setTurnListener(this);

	}

	public void showTable() {
		TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
				android.widget.TableRow.LayoutParams.FILL_PARENT,
				android.widget.TableRow.LayoutParams.FILL_PARENT);
		layoutParams.gravity = Gravity.CENTER;

		for (int i = 0; i < 30; i++) {
			TableRow tableRow = new TableRow(this);
			TextView textView = new TextView(this);
			textView.setText("jjhappyforever is " + i);
			textView.setTextSize(20);
			textView.setPadding(10, 10, 10, 10);

			tableRow.addView(textView, layoutParams);
			tl_main.addView(tableRow);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		personalScrollView.setImageView(iv_personal_bg);// 背景
		personalScrollView.setLine_up(line_up);

		showTable();

	}

	@Override
	public void onTurn() {
		RotateAnimation animation = new RotateAnimation();
		animation.setFillAfter(true);
		animation.setInterpolatedTimeListener(this);
		image_header.startAnimation(animation);
		current_id = current_id < drawable_id.length - 1 ? ++current_id : 0;
	}

	@Override
	public void interpolatedTime(float interpolatedTime) {
		// 监听到翻转进度过半时，更新图片内容，
		if (interpolatedTime > 0.5f) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					drawable_id[current_id]);
			image_header.setImageBitmap(bitmap);
		}
	}
}
