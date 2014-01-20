package org.greengin.sciencetoolkit.ui.base.modelconfig.widgets.seekbar;

public interface SeekBarTransform {
	public int transformSeekBarValue2Pos(Number value);
	public Number transformSeekBarPos2Value(int pos);
}
