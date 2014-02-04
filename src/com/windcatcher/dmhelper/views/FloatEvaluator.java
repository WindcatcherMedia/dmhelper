package com.windcatcher.dmhelper.views;

import android.animation.TypeEvaluator;

public class FloatEvaluator implements TypeEvaluator<Float> {

	@Override
	public Float evaluate(float fraction, Float startValue, Float endValue) {
		
		return startValue + fraction * (endValue - startValue);
	}

	// ===========================================================
	// TODO Fields
	// ===========================================================

	// ===========================================================
	// TODO Constants
	// ===========================================================

	// ===========================================================
	// TODO Inherited Methods
	// ===========================================================

	// ===========================================================
	// TODO Methods
	// ===========================================================
}
