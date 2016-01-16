package org.greengin.sciencetoolkit.common.ui.base.widgets.seekbar;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.ui.base.modelconfig.ModelKeyChangeListener;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class NumberModelSeekBarTransformWrapper implements OnSeekBarChangeListener {
	Model model;
	String modelKey;
	String widgetKey;
	String type;
	ModelKeyChangeListener listener;
	SeekBarTransform transform;
	Number defaultValue;

	public NumberModelSeekBarTransformWrapper(Model model, String modelKey, String widgetKey, String type, Number defaultValue, ModelKeyChangeListener listener, SeekBarTransform transform) {
		this.model = model;
		this.widgetKey = widgetKey;
		this.modelKey = modelKey;
		this.type = type;
		this.listener = listener;
		this.defaultValue = defaultValue;
		this.transform = transform;
	}
	
	protected boolean setValue(Number value) {
		if ("long".equals(type)) {
			return model.setLong(modelKey, (Long) value);
		} else if ("double".equals(type)) {
			return model.setDouble(modelKey, (Double) value);
		} else if ("int".equals(type)) {
			return model.setInt(modelKey, (Integer) value);
		} else {
			return false;
		}
	}
	
	protected int getPosForCurrentValue() {
		return transform.transformSeekBarValue2Pos(model.getNumber(modelKey, defaultValue));
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			Number value = transform.transformSeekBarPos2Value(progress);
			if (setValue(value) && listener != null) {
				listener.modelKeyModified(widgetKey);
			}			
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}
