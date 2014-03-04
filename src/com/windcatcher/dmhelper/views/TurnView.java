package com.windcatcher.dmhelper.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.windcatcher.dmhelper.R;

public class TurnView extends View{

	public TurnView(Context context) {
		super(context);

	}

	public TurnView(Context context, AttributeSet attrs) {
		this(context, attrs, R.style.AppBaseTheme);
	}

	public TurnView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);


		// grab the resources for the dimensions
		Resources res = getResources();
		mVerticalPadding = res.getDimension(R.dimen.tv_vertpadding);
		mHorizontalPadding = res.getDimension(R.dimen.tv_sidepadding);
		mIndicatorPadding = res.getDimension(R.dimen.tv_indicatorpadding);
		mIndicatorHeight = res.getDimension(R.dimen.tv_indicatorheight);
		mIndicatorWidth = res.getDimension(R.dimen.tv_indicatorwidth);
		mLineWidth = res.getDimension(R.dimen.tv_linewidth);

		// create the paint objects
		mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mLinePaint.setColor(Color.BLACK);
		mLinePaint.setStrokeWidth(mLineWidth);


		mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintIndicator.setColor(Color.BLACK);
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	private int mMaxTurn = 6;
	private int mMinTurn = 2;
	private float mCurrentTurn;
	private int mCurrentTurnCount;
	private List<Short> mTurnValues = new ArrayList<Short>();

	private boolean mScrolling = false;

	private float mVerticalPadding;
	private float mHorizontalPadding;
	private float mLineWidth;

	private Path mPath;
	private float mIndicatorOffset;
	private float mIndicatorPadding;
	private float mIndicatorHeight;
	private float mIndicatorWidth;

	private Paint mLinePaint;
	private Paint mPaintIndicator;

	// ===========================================================
	// TODO Constants
	// ===========================================================

	public static final int TURN_POSITION = 0, TURN_INIT = 1;

	private final AnimatorListener mAnimationEnd = new AnimatorListener() {

		@Override
		public void onAnimationStart(Animator animation) {
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			mScrolling = false;
		}

		@Override
		public void onAnimationCancel(Animator animation) {
		}
	};

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Leave the default height
		final int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);


		float width = 0;
		final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		if(widthSpecMode == MeasureSpec.EXACTLY){
			// We were told how wide to be
			width = MeasureSpec.getSize(heightMeasureSpec);
		}else{
			// calculate width

			width = mHorizontalPadding + mIndicatorWidth + mIndicatorPadding + mLineWidth + mHorizontalPadding;
		}

		setMeasuredDimension((int)width, measuredHeight);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		// temp x,ys
		float x1, x2, y1, y2;
		// get the width and height
		final int height = getHeight();
		final int width = getWidth();


		// draw the base line
		x1 = mHorizontalPadding + mIndicatorWidth + mIndicatorPadding;
		y1 = mVerticalPadding;
		y2 = height - mVerticalPadding;
		canvas.drawLine(x1, y1, x1, y2, mLinePaint);

		// draw the position n markers
		for(int i = 0; i <= mMaxTurn; i ++){
			if(i == mMinTurn || i == mMaxTurn || mTurnValues.contains((short)i)){
				x1 = mHorizontalPadding + mIndicatorWidth;
				y1 = ((height - (mVerticalPadding * 2) - mIndicatorHeight) / mMaxTurn) * (mMaxTurn - i) + mVerticalPadding + mIndicatorHeight / 2;
				x2 = mHorizontalPadding + mIndicatorWidth + mIndicatorPadding;
				canvas.drawLine(x1, y1, x2, y1, mLinePaint);	
			}
		}


		// draw the triangle
		mIndicatorOffset = getIndicatorPosition(height);
		mPath = new Path();
		mPath.moveTo(mHorizontalPadding, mIndicatorOffset);
		mPath.lineTo(mHorizontalPadding, mIndicatorOffset + mIndicatorHeight);
		mPath.lineTo(mHorizontalPadding + mIndicatorWidth, mIndicatorOffset + (mIndicatorHeight / 2));
		mPath.close();
		canvas.drawPath(mPath, mLinePaint);
	}
	// ===========================================================
	// TODO Methods
	// ===========================================================

	public void init(List<Short> turns){
		// sort the turns
		Collections.sort(turns);
		// set the vars
		this.mCurrentTurnCount = turns.size() - 1;
		this.mMaxTurn = turns.get(mCurrentTurnCount);
		this.mMinTurn = turns.get(0);
		this.mCurrentTurn = mMaxTurn;
		this.mTurnValues = turns;

		invalidate();
	}

	/**
	 * Set the next turn and return the new turn's position relative to the list row
	 * @return A size 2 array containing:
	 * 0 position - A 0-based position of where the turn is relative from high initiative to low initiative.
	 * 1 position - The initiative value of the current turn
	 */
	public int[] nextTurn(){
		int[] turnInfo = new int[2];
		if(!mScrolling){
			// animate the turn indicator down
			if(mCurrentTurnCount - 1 >= 0){
				mCurrentTurnCount --;
			}else{
				mCurrentTurnCount = mTurnValues.size() - 1;
			}
			ObjectAnimator animator = ObjectAnimator.ofObject(this, "currentTurn", new FloatEvaluator(), mCurrentTurn, (float)Math.floor(mTurnValues.get(mCurrentTurnCount)));
			animator.setDuration(500);
			animator.start();
			animator.addListener(mAnimationEnd);
			mScrolling = true;
			// current turn starts high and works down, so we need to return the max - current to get the accurate return for the row position
			turnInfo[TURN_POSITION] = mTurnValues.size() - mCurrentTurnCount - 1;
			turnInfo[TURN_INIT] = mTurnValues.get(mCurrentTurnCount);
		}else{
			turnInfo[TURN_POSITION] = -1;
		}
		return turnInfo;
	}

	/**
	 * Set the next turn and return the new turn's position relative to the list row
	 * @return A 0-based position of where the turn is relative from high initiative to low initiative.
	 */
	public int previousTurn(){
		if(!mScrolling){
			// animate the turn indicator down
			if(mCurrentTurnCount + 1 < mTurnValues.size()){
				mCurrentTurnCount ++;
			}else{
				mCurrentTurnCount = 0;
			}
			ObjectAnimator animator = ObjectAnimator.ofObject(this, "currentTurn", new FloatEvaluator(), mCurrentTurn, (float)Math.floor(mTurnValues.get(mCurrentTurnCount)));
			animator.setDuration(500);
			animator.start();
			animator.addListener(mAnimationEnd);
			mScrolling = true;
			return mTurnValues.size() - mCurrentTurnCount - 1; // current turn starts high and works down, so we need to return the max - current to get the accurate return for the row position
		}
		return -1;
	}

	private float getIndicatorPosition(int height){
		// get base at 0
		float position = mVerticalPadding;

		position += ((height - (mVerticalPadding * 2) - mIndicatorHeight) / mMaxTurn) * (mMaxTurn - mCurrentTurn);

		return position;
	}

	public float getCurrentTurn() {
		return mCurrentTurn;
	}

	public void setCurrentTurn(float currentTurn) {
		this.mCurrentTurn = currentTurn;
		invalidate();
	}

	/**
	 * Sets the current turn on the indicator
	 * 
	 * @param currentTurn
	 * @return The initiative value of the new turn that was set
	 */
	public int setCurrentTurnCount(int currentTurn){
		mCurrentTurnCount = mTurnValues.size() - 1 - currentTurn;
		this.mCurrentTurn = (float)Math.floor(mTurnValues.get(mCurrentTurnCount));
		invalidate();
		
		return mTurnValues.get(mCurrentTurnCount);
	}

	public int getMaxTurns(){
		return mMaxTurn;
	}

	public int getTurnCount(){
		return mTurnValues.size();
	}

}
