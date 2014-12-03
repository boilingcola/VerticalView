package com.example.test_marquee;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * LinearLayout作为父View，必须有一个子TextView
 * 
 * 利用动画实现
 */
public class MarqueeView extends LinearLayout {

	private static final int TEXTVIEW_VIRTUAL_WIDTH = 2000;/* TextView默认宽度 */
	private static final int DEFAULT_SPEED = 35;/* 默认滚动速度 越大滚动越慢 */
	private static final int DEFAULT_ANIMATION_PAUSE = 0;/* 出去动画与进入动画的时间间隔 */
	private static final String TAG = MarqueeView.class.getSimpleName();

	private TextView mTextField;/* 该跑马灯的孙子View之TextView */
	private ScrollView mScrollView;/* 该跑马灯的子View之mScrollView */

	private Animation mMoveTextOut = null;/* 作用于TextView的动画 --出去 */
	private Animation mMoveTextIn = null;/* 作用于TextView的动画 --进入 */

	private Paint mPaint;
	private int mSpeed = DEFAULT_SPEED;
	private int mAnimationPause = DEFAULT_ANIMATION_PAUSE;
	private Interpolator mInterpolator = new LinearInterpolator();

	private Runnable mAnimationStartRunnable;

	/** 字符串之间的间隔 */
	private String interval = "     ";
	private String stringOfItem = "";
	/** str+interval的长度 */
	private float widthOfItem = 0;
	private float widthOfTextView;
	private String stringOfTextView = "";
	private float startXOfOut = 0;
	private float endXOfOut = 0;
	private float startXOfIn = 0;
	private float endXOfIn = 0;

	public MarqueeView(Context context) {
		super(context);
		init(context);
	}

	public MarqueeView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	public MarqueeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		// init helper
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(1);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mInterpolator = new LinearInterpolator();
	}

	// 当给子View分配位置和尺寸时调用。
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Logcat.d(TAG, "onLayout called!" + "changed: " + changed);
		if (getChildCount() == 0 || getChildCount() > 1) {
			throw new RuntimeException(
					"MarqueeView must have exactly one child element.");
		}

		//
		if (changed) {
			View v = getChildAt(0);
			if (!(v instanceof TextView)) {
				throw new RuntimeException(
						"The child view of this MarqueeView must be a TextView instance.");
			}
			initView(getContext());
			mTextField.setText(mTextField.getText());
		}
	}

	/** Starts the configured marquee effect. */
	public void startMarquee() {
		Logcat.d(TAG, "startMarquee called");
		startTextFieldAnimation();
	}

	// 一旦开始动画，动画结束开始由监听器负责。
	private void startTextFieldAnimation() {
		mAnimationStartRunnable = new Runnable() {
			public void run() {
				mTextField.startAnimation(mMoveTextOut);
			}
		};
		postDelayed(mAnimationStartRunnable, mAnimationPause);
	}

	/**
	 * Disables the animations.
	 */
	public void reset() {

		if (mAnimationStartRunnable == null)
			return;
		removeCallbacks(mAnimationStartRunnable);
		mTextField.clearAnimation();
		mMoveTextOut.reset();
		mMoveTextIn.reset();
		invalidate();
	}

	private void prepareAnimation() {
		// Measure
		mPaint.setTextSize(mTextField.getTextSize());
		mPaint.setTypeface(mTextField.getTypeface());
		float mTextWidth = mPaint.measureText(mTextField.getText().toString());

		float width = getMeasuredWidth();
		startXOfOut = -(mTextWidth - width) % widthOfItem;
		endXOfOut = -mTextWidth + width;
		startXOfIn = -(mTextWidth - width) % widthOfItem;
		endXOfIn = -mTextWidth + width;

		final int duration = ((int) Math.abs(startXOfOut - endXOfOut) * mSpeed);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "(int) Math.abs(startXOfOut - endXOfOut)       : "
					+ (int) Math.abs(startXOfOut - endXOfOut));
			Log.d(TAG, "mSpeed       : " + mSpeed);
			Log.d(TAG, "startXOfOut       : " + startXOfOut);
			Log.d(TAG, "endXOfOut         : " + endXOfOut);
			Log.d(TAG, "startXOfIn        : " + startXOfIn);
			Log.d(TAG, "endXOfIn  		  : " + endXOfIn);
			Log.d(TAG, "duration  		  : " + duration);
		}
		mMoveTextOut = new TranslateAnimation(startXOfOut, endXOfOut, 0, 0);
		mMoveTextOut.setDuration(duration);
		mMoveTextOut.setInterpolator(mInterpolator);
		mMoveTextOut.setFillAfter(true);

		mMoveTextIn = new TranslateAnimation(startXOfIn, endXOfIn, 0, 0);
		mMoveTextIn.setDuration(duration);
		mMoveTextIn.setStartOffset(mAnimationPause);
		mMoveTextIn.setInterpolator(mInterpolator);
		mMoveTextIn.setFillAfter(true);

		mMoveTextOut.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				mTextField.startAnimation(mMoveTextIn);
			}

			public void onAnimationRepeat(Animation animation) {
			}
		});

		mMoveTextIn.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				startTextFieldAnimation();
			}

			public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	/** 初始化子View */
	private void initView(Context context) {

		// Scroll View
		LayoutParams sv1lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		sv1lp.gravity = Gravity.CENTER_HORIZONTAL;
		mScrollView = new ScrollView(context);

		// Scroll View 1 - Text Field
		mTextField = (TextView) getChildAt(0);
		removeView(mTextField);

		mScrollView.addView(mTextField, new ScrollView.LayoutParams(
				TEXTVIEW_VIRTUAL_WIDTH, LayoutParams.WRAP_CONTENT));

		mTextField.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
			}

			@Override
			public void afterTextChanged(Editable editable) {

				Logcat.d(TAG, "afterTextChanged called");

				// 如果提供的字符串未被加工过，就先加工，否则就开始动画
				if (!stringOfTextView.equals(editable.toString())) {

					String str = editable.toString();
					mPaint.setTextSize(mTextField.getTextSize());
					mPaint.setTypeface(mTextField.getTypeface());

					stringOfItem = str + interval;
					widthOfItem = mPaint.measureText(stringOfItem);
					stringOfTextView = stringOfItem;
					widthOfTextView = widthOfItem;

					while (widthOfTextView <= 2 * getMeasuredWidth()) {
						stringOfTextView += stringOfItem;
						widthOfTextView = mPaint.measureText(stringOfTextView);
					}
					Logcat.d(TAG, "string of TextView deal ok!###");
					Logcat.d(TAG, "lengthOfll: " + getMeasuredWidth() + "###");
					Logcat.d(TAG, "lengthOfTextView: " + widthOfTextView
							+ "###");
					Logcat.d(TAG, "CONTENT: " + stringOfTextView + "###");
					// 设置起始
					mTextField.setText(stringOfTextView);
					return;
				}
				reset();
				prepareAnimation();
				expandTextView();
				post(new Runnable() {
					@Override
					public void run() {
						startMarquee();
					}
				});
			}
		});

		addView(mScrollView, sv1lp);
	}

	private void expandTextView() {
		ViewGroup.LayoutParams lp = mTextField.getLayoutParams();
		lp.width = (int) widthOfTextView + 5;
		mTextField.setLayoutParams(lp);
	}
}