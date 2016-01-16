package org.greengin.sciencetoolkit.ui.base.animations;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;

public class Animations extends Animation {
	
	public static final int DURATION = 500;
	
	public static int measureHeight(View view) {
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY);
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.UNSPECIFIED);
		view.measure(widthMeasureSpec, heightMeasureSpec);
		return view.getMeasuredHeight();
	}
	
	public static void animateHeight(View view, int endH) {
		animate(view, new HeightAnimation(view, endH));
	}
	
	private static void animate(View view, Animation anim) {
		Animation current = view.getAnimation();

		if (current != null) {
			current.cancel();
			current.reset();
		}
		
		anim.setDuration(DURATION);
		view.startAnimation(anim);
	}

}
