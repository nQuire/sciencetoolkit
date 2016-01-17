package org.greengin.sciencetoolkit.common.ui.base.widgets.colorview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {

	public ColorView(Context context) {
		super(context);
	}

	public ColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setColorIndex(int n) {
		int color;
		if (n < 0) {
			color = Color.DKGRAY;
		} else {
			int[] rgb = ColorGenerator.getRbgColor(n);
			color = Color.rgb(rgb[0], rgb[1], rgb[2]);
		}
		
		this.setBackgroundColor(color);
	}
}
