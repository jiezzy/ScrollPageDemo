package com.example.scrollviewdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

public class MyHorScorllView extends HorizontalScrollView {

	private final String TAG = MyHorScorllView.class.getSimpleName();

	private View inner;// 孩子View

	private Activity activity;
	private float touchX;// 点击时Y坐标

	private float deltaX;// Y轴滑动的距离

	private float initTouchX;// 首次点击的Y坐标

	private boolean shutTouch = false;// 是否关闭ScrollView的滑动.

	private Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否需要动画.)

	private boolean isMoveing = false;// 是否开始移动.

	private ImageView imageView;// 背景图控件.
	private View line_up;// 上线
	private int line_up_top;// 上线的top
	private int line_up_bottom;// 上线的bottom

	private int initLeft, initRight;// 初始高度
	private int curIndex = 0;

	private int mleft, mright;

	private int current_Left, current_Right;// 拖动时时高度。

	private int lineUp_current_Top, lineUp_current_Bottom;// 上线

	private onTurnListener turnListener;

	// 状态：上部，下部，默认
	private enum State {
		UP, DOWN, LEFT, RIGHT, NOMAL
	};

	// 默认状态
	private State state = State.NOMAL;

	public void setTurnListener(onTurnListener turnListener) {
		this.turnListener = turnListener;
	}

	public void setLine_up(View line_up) {
		this.line_up = line_up;
	}

	// 注入背景图
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	/***
	 * 构造方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public MyHorScorllView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (Activity) context;
	}

	/***
	 * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
	 * 方法，也应该调用父类的方法，使该方法得以执行.
	 */
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);
		}
	}

	/** touch 事件处理 **/
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (inner != null) {
			commOnTouchEvent(ev);
		}
		// ture：禁止控件本身的滑动.
		if (shutTouch)
			return true;
		else
			return super.onTouchEvent(ev);

	}

	/***
	 * 触摸事件
	 * 
	 * @param ev
	 */
	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			initTouchX = ev.getX();
			current_Left = initLeft = imageView.getLeft();
			current_Right = initRight = imageView.getRight();
			// if (line_up_top == 0) {
			// lineUp_current_Top = line_up_top = line_up.getTop();
			// lineUp_current_Bottom = line_up_bottom = line_up.getBottom();
			// }
			break;
		case MotionEvent.ACTION_UP:
			/** 回缩动画 **/
			if (isNeedAnimation()) {
				animation();
			}

			if (getScrollX() == 0) {
				state = State.NOMAL;
			}

			isMoveing = false;
			touchX = 0;
			shutTouch = false;
			break;

		/***
		 * 排除出第一次移动计算，因为第一次无法得知deltaY的高度， 然而我们也要进行初始化，就是第一次移动的时候让滑动距离归0.
		 * 之后记录准确了就正常执行.
		 */
		case MotionEvent.ACTION_MOVE:

			touchX = ev.getX();
			deltaX = touchX - initTouchX;// 滑动距离

			/** 对于首次Touch操作要判断方位：UP OR DOWN **/
			if (deltaX < 0 && state == state.NOMAL) {
				state = State.LEFT;
			} else if (deltaX > 0 && state == state.NOMAL) {
				state = State.RIGHT;
			}

			shutTouch = true;
			isMoveing = true;

			if (isMoveing) {
				// 初始化头部矩形
				if (normal.isEmpty()) {
					// 保存正常的布局位置
					normal.set(inner.getLeft(), inner.getTop(),
							inner.getRight(), inner.getBottom());
				}
				// 移动布局(手势移动的1/3)
				float inner_move_H = deltaX / 5;
				mleft = (int) (normal.left + inner_move_H);
				mright = (int) (normal.right + inner_move_H);

				inner.layout(mleft, normal.top, mright, normal.bottom);

				// Log.i(TAG, "x:" + inner.getLeft());
				/** image_bg **/
				float image_move_H = deltaX / 10;
				current_Left = (int) (initLeft + image_move_H);
				current_Right = (int) (initRight + image_move_H);
				imageView.layout(current_Left, imageView.getTop(),
						current_Right, imageView.getBottom());

				/** line_up **/
				lineUp_current_Top = (int) (line_up_top + inner_move_H);
				lineUp_current_Bottom = (int) (line_up_bottom + inner_move_H);
				// line_up.layout(line_up.getLeft(), lineUp_current_Top,
				// line_up.getRight(), lineUp_current_Bottom);
			}
			break;

		default:
			break;

		}
	}

	/***
	 * 回缩动画
	 */
	public void animation() {
		int from = 0, to = 0;
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (state == State.LEFT) {
			curIndex++;

			if (curIndex > 3) {
				curIndex = 3;
				from = normal.left - dm.widthPixels * 3 + mleft;
				to = normal.left - dm.widthPixels * 3;
			} else {
				from = normal.left - (dm.widthPixels * (curIndex - 1)) + mleft;
				to = normal.left - dm.widthPixels * curIndex;
			}

			TranslateAnimation image_Anim = new TranslateAnimation(from, to, 0,
					0);
			image_Anim.setDuration(300);
			image_Anim.setFillAfter(true);
			imageView.startAnimation(image_Anim);

			// 开启移动动画
			TranslateAnimation inner_Anim = new TranslateAnimation(from, to, 0, 0);
			inner_Anim.setDuration(300);
			inner_Anim.setFillAfter(true);
			inner.startAnimation(inner_Anim);
		} else {
			curIndex--;
			if (curIndex < 0) {
				curIndex = 0;
				from = normal.left + mleft;
				to = 0;
			} else {
				from = normal.left - (dm.widthPixels * (curIndex + 1)) + mleft;
				to = -dm.widthPixels * curIndex;
			}
			
			TranslateAnimation image_Anim = new TranslateAnimation(from, to, 0,
					0);
			image_Anim.setDuration(300);
			image_Anim.setFillAfter(true);
			imageView.startAnimation(image_Anim);
			
			// 开启移动动画
			TranslateAnimation inner_Anim = new TranslateAnimation(from, to, 0,
					0);
			inner_Anim.setDuration(200);
			inner_Anim.setFillAfter(true);
			inner.startAnimation(inner_Anim);
		}
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);

		/** line_up **/
		// TranslateAnimation line_up_Anim = new TranslateAnimation(
		// Math.abs(line_up_top - lineUp_current_Top), 0,0, 0);
		// line_up_Anim.setDuration(200);
		// line_up.startAnimation(line_up_Anim);
		// line_up.layout(line_up.getLeft(), line_up_top, line_up.getRight(),
		// line_up_bottom);

		normal.setEmpty();

		/** 动画执行 **/
		if (current_Left > initLeft + 50 && turnListener != null)
			turnListener.onTurn();

	}

	/** 是否需要开启动画 **/
	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	/***
	 * 执行翻转
	 * 
	 * @author jia
	 * 
	 */
	public interface onTurnListener {

		/** 必须达到一定程度才执行 **/
		void onTurn();
	}

}