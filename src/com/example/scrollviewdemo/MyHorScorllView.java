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

	private View inner;// ����View

	private Activity activity;
	private float touchX;// ���ʱY����

	private float deltaX;// Y�Ử���ľ���

	private float initTouchX;// �״ε����Y����

	private boolean shutTouch = false;// �Ƿ�ر�ScrollView�Ļ���.

	private Rect normal = new Rect();// ����(����ֻ�Ǹ���ʽ��ֻ�������ж��Ƿ���Ҫ����.)

	private boolean isMoveing = false;// �Ƿ�ʼ�ƶ�.

	private ImageView imageView;// ����ͼ�ؼ�.
	private View line_up;// ����
	private int line_up_top;// ���ߵ�top
	private int line_up_bottom;// ���ߵ�bottom

	private int initLeft, initRight;// ��ʼ�߶�
	private int curIndex = 0;

	private int mleft, mright;

	private int current_Left, current_Right;// �϶�ʱʱ�߶ȡ�

	private int lineUp_current_Top, lineUp_current_Bottom;// ����

	private onTurnListener turnListener;

	// ״̬���ϲ����²���Ĭ��
	private enum State {
		UP, DOWN, LEFT, RIGHT, NOMAL
	};

	// Ĭ��״̬
	private State state = State.NOMAL;

	public void setTurnListener(onTurnListener turnListener) {
		this.turnListener = turnListener;
	}

	public void setLine_up(View line_up) {
		this.line_up = line_up;
	}

	// ע�뱳��ͼ
	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	/***
	 * ���췽��
	 * 
	 * @param context
	 * @param attrs
	 */
	public MyHorScorllView(Context context, AttributeSet attrs) {
		super(context, attrs);
		activity = (Activity) context;
	}

	/***
	 * ���� XML ������ͼ�������.�ú�����������ͼ�������ã�����������ͼ�����֮��. ��ʹ���า���� onFinishInflate
	 * ������ҲӦ�õ��ø���ķ�����ʹ�÷�������ִ��.
	 */
	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);
		}
	}

	/** touch �¼����� **/
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (inner != null) {
			commOnTouchEvent(ev);
		}
		// ture����ֹ�ؼ�����Ļ���.
		if (shutTouch)
			return true;
		else
			return super.onTouchEvent(ev);

	}

	/***
	 * �����¼�
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
			/** �������� **/
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
		 * �ų�����һ���ƶ����㣬��Ϊ��һ���޷���֪deltaY�ĸ߶ȣ� Ȼ������ҲҪ���г�ʼ�������ǵ�һ���ƶ���ʱ���û��������0.
		 * ֮���¼׼ȷ�˾�����ִ��.
		 */
		case MotionEvent.ACTION_MOVE:

			touchX = ev.getX();
			deltaX = touchX - initTouchX;// ��������

			/** �����״�Touch����Ҫ�жϷ�λ��UP OR DOWN **/
			if (deltaX < 0 && state == state.NOMAL) {
				state = State.LEFT;
			} else if (deltaX > 0 && state == state.NOMAL) {
				state = State.RIGHT;
			}

			shutTouch = true;
			isMoveing = true;

			if (isMoveing) {
				// ��ʼ��ͷ������
				if (normal.isEmpty()) {
					// ���������Ĳ���λ��
					normal.set(inner.getLeft(), inner.getTop(),
							inner.getRight(), inner.getBottom());
				}
				// �ƶ�����(�����ƶ���1/3)
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
	 * ��������
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

			// �����ƶ�����
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
			
			// �����ƶ�����
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

		/** ����ִ�� **/
		if (current_Left > initLeft + 50 && turnListener != null)
			turnListener.onTurn();

	}

	/** �Ƿ���Ҫ�������� **/
	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	/***
	 * ִ�з�ת
	 * 
	 * @author jia
	 * 
	 */
	public interface onTurnListener {

		/** ����ﵽһ���̶Ȳ�ִ�� **/
		void onTurn();
	}

}