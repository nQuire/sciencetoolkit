package org.greengin.sciencetoolkit.common.ui.base.widgets.seekbar;

import android.content.Context;
import android.widget.SeekBar;

public class TransformSeekBar extends SeekBar {
	
	NumberModelSeekBarTransformWrapper transform;
	
	public TransformSeekBar(Context context) {
		super(context);
		this.transform = null;
	}
	
	public void setTransform(NumberModelSeekBarTransformWrapper transform) {
		this.transform = transform;
		setOnSeekBarChangeListener(transform);
		updateValue();
	}
	
	public void updateValue() {
		int pos = transform.getPosForCurrentValue();
		this.setProgress(pos);
	}

}
