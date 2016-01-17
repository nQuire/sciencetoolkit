package org.greengin.sciencetoolkit.ui.base.animations;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HeightAnimation extends Animation {
	View view;
	int startH;
	int endH;
	int diff;

	public HeightAnimation(View view, int endH) {
		this.view = view;
		this.startH = this.view.getLayoutParams().height;
		this.endH = endH;
		this.diff = this.endH - this.startH;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		view.getLayoutParams().height = startH + (int) (diff * interpolatedTime);
		view.requestLayout();
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	@Override
	public boolean willChangeBounds() {
		return true;
	}
}
