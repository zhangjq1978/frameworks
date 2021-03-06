package com.nb.hall.floatwindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import com.android.systemui.R;
public class NbSlidingTab extends FrameLayout {
	private View mView;
	private ImageView mInsideAnswerPressCircle;
	private ImageView mInsideRejectPressCircle;
	private ImageView mOuterAnswerCircle, mOuterRejectCircle;
	private ImageView mAnswer, mReject;
	private final Rect mTmpRect;
	private boolean mAnswerHit = false, mRejectHit = false;
	Context mContext;
	private final static float OFFSET = 0;
	private final static int INSIDE_CIRCLE_WIDTH = 168;
	public final static String TAG = "LxtSlidingTab";

	private double mAnswerPointX, mAnswerPointY, mRadius;
	private double mRejectPointX, mRejectPointY;
	public final static float THRESHOLD = 250;
	public double mAlpha = 1.0;

	private float mScale = 1;
	private float mPreScale = 1;

	public NbSlidingTab(Context context) {
		this(context, null);
	}

	/**
	 * Constructor used when this widget is created from a layout file.
	 */
	public NbSlidingTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		LayoutInflater mLi = LayoutInflater.from(context);
		mView = mLi.inflate(R.layout.nb_incoming_call_widget, null);
		addView(mView);
		mTmpRect = new Rect();

		mOuterAnswerCircle = (ImageView) findViewById(R.id.answer_clear_circle);
		mOuterRejectCircle = (ImageView) findViewById(R.id.reject_clear_circle);

		mReject = (ImageView) findViewById(R.id.reject);
		mAnswer = (ImageView) findViewById(R.id.answer);

		mInsideAnswerPressCircle = (ImageView) findViewById(R.id.answer_press_circle);
		mInsideRejectPressCircle = (ImageView) findViewById(R.id.reject_press_circle);

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void reset() {
		mOuterAnswerCircle.clearAnimation();
		mOuterRejectCircle.clearAnimation();
	}

	protected void getAnswerAndRejectPoint() {
		// 取接听及拒绝按钮的中点坐标
		// mInsideAnswerCircle.getHitRect(mTmpRect);
		int[] position = new int[2];
		mAnswer.getHitRect(mTmpRect);
		mAnswer.getLocationOnScreen(position);
		mRadius = (mTmpRect.right - mTmpRect.left) / 2.0;
		mAnswerPointX = position[0] + OFFSET + mRadius;
		mAnswerPointY = mTmpRect.top + mRadius;

		// mInsideRejectCircle.getLocationOnScreen(position);
		mReject.getLocationOnScreen(position);
		mRejectPointX = position[0] + OFFSET + mRadius;
		mRejectPointY = mAnswerPointY;
		// Log.e(TAG,
		// "mA.x:"+mAnswerPointX+" mA.y"+mAnswerPointY+" mR.x:"+mRejectPointX+"
		// mR.y"+mRejectPointY);
	}

	protected void onHandleAnswerActionDown() {
		// 启动外环
		final Animation reverse = AnimationUtils.loadAnimation(mContext, R.anim.enlarge_outer_cricle);
		reverse.setDuration(300);
		reverse.setFillAfter(true);
		mOuterAnswerCircle.setVisibility(VISIBLE);
		mOuterAnswerCircle.setAnimation(reverse);

		// 改变拒绝按钮透明度
		AlphaAnimation alphaAnim = new AlphaAnimation((float) 1.0, (float) 0.2);
		alphaAnim.setDuration(10);
		alphaAnim.setFillAfter(true);
		// mReject.setAnimation(alphaAnim);

		// mAnswer.setImageResource(R.drawable.call_btn_receive_press);

	}

	protected void resetAnswer() {
		// 恢复接听外环
		final Animation reverse = AnimationUtils.loadAnimation(mContext, R.anim.reduce_outer_circle_ani);
		reverse.setDuration(300);
		reverse.setFillAfter(false);
		mOuterAnswerCircle.setAnimation(reverse);
		mOuterAnswerCircle.setVisibility(INVISIBLE);

		final ScaleAnimation animation = new ScaleAnimation(mScale, 1.0f, mScale, 1.0f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(5);// 设置动画持续时间
		animation.setFillAfter(false);
		mInsideAnswerPressCircle.startAnimation(animation);
		mInsideAnswerPressCircle.setVisibility(View.INVISIBLE);

		// 改变拒绝按钮透明度
		AlphaAnimation alphaAnim = new AlphaAnimation((float) 0.2, (float) 1.0);
		alphaAnim.setDuration(10);
		alphaAnim.setFillAfter(true);
		// mReject.setAnimation(alphaAnim);

		mPreScale = 1;
		mScale = 1;

	}

	protected void resetReject() {
		final Animation reverse = AnimationUtils.loadAnimation(mContext, R.anim.reduce_outer_circle_ani);
		reverse.setDuration(300);
		reverse.setFillAfter(false);
		mOuterRejectCircle.setAnimation(reverse);
		mOuterRejectCircle.setVisibility(INVISIBLE);

		final ScaleAnimation animation = new ScaleAnimation(mScale, 1.0f, mScale, 1.0f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(5);// 设置动画持续时间
		animation.setFillAfter(false);
		mInsideRejectPressCircle.startAnimation(animation);
		mInsideRejectPressCircle.setVisibility(View.INVISIBLE);
		// mReject.setImageResource(R.drawable.call_circle_reject_01);

		// 改变接听按钮透明度
		AlphaAnimation alphaAnim = new AlphaAnimation((float) 0.2, (float) 1.0);
		alphaAnim.setDuration(10);
		alphaAnim.setFillAfter(true);
		// mAnswer.setAnimation(alphaAnim);

		mPreScale = 1;
		mScale = 1;

	}

	protected void onHandleRejectActionDown() {
		// 启动外环
		final Animation reverse = AnimationUtils.loadAnimation(mContext, R.anim.enlarge_outer_cricle);
		reverse.setDuration(300);
		reverse.setFillAfter(true);
		mOuterRejectCircle.setVisibility(VISIBLE);
		mOuterRejectCircle.setAnimation(reverse);

		// mReject.setImageResource(R.drawable.call_btn_reject_press);

		// 改变接听按钮透明度
		AlphaAnimation alphaAnim = new AlphaAnimation((float) 1.0, (float) 0.2);
		alphaAnim.setDuration(10);
		alphaAnim.setFillAfter(true);
		// mAnswer.setAnimation(alphaAnim);
	}

	protected void onHandleAnswerActionMove(double value) {
		mScale = (float) ((value / THRESHOLD) * 2 + 1.0);

		final ScaleAnimation animation = new ScaleAnimation(mPreScale, mScale, mPreScale, mScale,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(5);// 设置动画持续时间
		animation.setFillAfter(true);
		mInsideAnswerPressCircle.startAnimation(animation);
		mPreScale = mScale;
		Log.e(TAG, "mPreScale:" + mPreScale + "  " + "mScale: " + mScale);

	}

	protected void onHandleRejectActionMove(double value) {

		mScale = (float) ((value / THRESHOLD) * 2 + 1.0);
		final ScaleAnimation animation = new ScaleAnimation(mPreScale, mScale, mPreScale, mScale,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(5);// 设置动画持续时间
		animation.setFillAfter(true);
		mInsideRejectPressCircle.startAnimation(animation);
		mPreScale = mScale;

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		float x = event.getX();
		final float y = event.getY();
		x += OFFSET;

		int[] position = new int[2];
		getAnswerAndRejectPoint();
		mAnswer.getHitRect(mTmpRect);
		mAnswer.getLocationOnScreen(position);
		mTmpRect.left = position[0] + (int) OFFSET;
		mTmpRect.right = mTmpRect.left + (int) mRadius * 2;

		mAnswerHit = mTmpRect.contains((int) x, (int) y);
		// Log.e(TAG, "x:" + x + " y:" + y + " rect.left:" + mTmpRect.left
		// + " rect.right:" + mTmpRect.right + " rect.top:" + mTmpRect.top
		// + " rect.bottom:" + mTmpRect.bottom);
		// Log.e(TAG, "x:" + position[0] + " y:" + position[1]);

		// mInsideRejectCircle.getLocationOnScreen(position);
		mReject.getLocationOnScreen(position);

		mTmpRect.left = position[0] + (int) OFFSET;
		mTmpRect.right = mTmpRect.left + (int) mRadius * 2;

		// Log.e(TAG, "===>x:" + position[0] + " y:" + position[1]);
		mRejectHit = mTmpRect.contains((int) x, (int) y);
		// Log.e(TAG, "answerHit: " + mAnswerHit + " rejectHit: " + mRejectHit);
		if (!(mAnswerHit || mRejectHit)) {
			return false;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			if (mAnswerHit) {
				onHandleAnswerActionDown();
				// setGrabbedState(OnTriggerListener.LEFT_HANDLE);
				mInsideAnswerPressCircle.setVisibility(View.VISIBLE);
			} else {
				onHandleRejectActionDown();
				// setGrabbedState(OnTriggerListener.RIGHT_HANDLE);
				mInsideRejectPressCircle.setVisibility(View.VISIBLE);

				double value = getAnswerRaduis(x, y);
			}
			break;
		}
		}

		return true;
	}

	public double getAnswerRaduis(double x, double y) {
		double xd = mAnswerPointX - x;
		double yd = mAnswerPointY - y;
		if (xd < 0)
			xd = -xd;
		if (yd < 0)
			yd = -yd;
		double ret = Math.sqrt(xd * xd + yd * yd);
		// Log.e(TAG,"answer radius value:"+ret);
		return ret;
	}

	public double getRejectRaduis(double x, double y) {
		double xd = mRejectPointX - x;
		double yd = mRejectPointY - y;
		if (xd < 0)
			xd = -xd;
		if (yd < 0)
			yd = -yd;
		double ret = Math.sqrt(xd * xd + yd * yd);
		// Log.e(TAG,"reject radius value:"+ret);
		return ret;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		float x = event.getX();
		final float y = event.getY();
		x += OFFSET;
		switch (action) {
		case MotionEvent.ACTION_MOVE:

			if (mAnswerHit) {
				double value = getAnswerRaduis(x, y);
				Log.d(TAG, " value: " + value);
				if (value > THRESHOLD) {
					answerCall();
				} else {
					onHandleAnswerActionMove(value);
				}
			}
			if (mRejectHit) {
				double value = getRejectRaduis(x, y);
				Log.d(TAG, " value: " + value);
				if (value > THRESHOLD) {
					declineCall();
				} else {
					onHandleRejectActionMove(value);
				}
			}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (mAnswerHit) {
				resetAnswer();
				mAnswerHit = false;
			}
			if (mRejectHit) {
				resetReject();
				mRejectHit = false;
			}
			mAlpha = 1.0;
			break;
		}

		return true;
	}

	private void answerCall() {
		Intent intent = new Intent();
		intent.setClassName("com.android.dialer", "com.android.incallui.NotificationBroadcastReceiver");
		intent.setAction("com.android.incallui.ACTION_ANSWER_VOICE_INCOMING_CALL");
		mContext.sendBroadcast(intent);
	}

	private void declineCall() {
		Intent intent = new Intent();
		intent.setClassName("com.android.dialer", "com.android.incallui.NotificationBroadcastReceiver");
		intent.setAction("com.android.incallui.ACTION_DECLINE_INCOMING_CALL");
		mContext.sendBroadcast(intent);
	}
}
