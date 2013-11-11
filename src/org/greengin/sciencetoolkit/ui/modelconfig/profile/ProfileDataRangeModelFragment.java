package org.greengin.sciencetoolkit.ui.modelconfig.profile;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.modelconfig.widgets.datetime.DateTimeHelperPair;
import org.greengin.sciencetoolkit.ui.modelconfig.widgets.seekbar.SeekBarTransform;
import org.greengin.sciencetoolkit.ui.modelconfig.widgets.seekbar.TransformSeekBar;


import android.util.Log;
import android.view.View;

public class ProfileDataRangeModelFragment extends AbstractProfileConfigFragment {
	
	long timeMin;
	long timeMax;
	long timeGap;
	
	DateTimeHelperPair fromEdit;
	TransformSeekBar fromBar;
	
	DateTimeHelperPair toEdit;
	TransformSeekBar toBar;
	
	
	@Override
	protected Model fetchProfileConfigModel() {
		return profile.getModel("datarange", true);
	}

	@Override
	protected void createConfigOptions(View view) {
		long[] values = new long[2];
		
		boolean enabled = DataLogger.getInstance().getRange(values, profileId);
		
		if (enabled) {
			timeMin = values[0];
			timeMax = values[1];
			timeGap = timeMax - timeMin;
		} else {
			timeMin = timeMax = System.currentTimeMillis();
			timeGap = 0;
		}
		
		RangeTransform transform = new RangeTransform();
		
		if (model.getBool("track_to", true) && model.getLong("to", timeMax) < timeMax) {
			model.setLong("to", timeMax);
		}
		
		fromEdit = addOptionDateTimeMillis("from", null, null, timeMin);
		fromBar = addOptionSeekbar("from", "from_bar", null, null, timeMin, transform);
		
		toEdit = addOptionDateTimeMillis("to", null, null, timeMax);
		toBar = addOptionSeekbar("to", "to_bar", null, null, timeMax, transform);
		
	}
	
	@Override
	public boolean settingsShouldBeEnabled() {
		return timeGap > 0;
	}

	@Override
	public void modelKeyModified(String widgetTey) {
		super.modelKeyModified(widgetTey);

		String leading = null;
		
		if ("from".equals(widgetTey)) {
			updateTimeFromEdit("from", timeMin, fromEdit, fromBar);
			leading = "from";
		} else if ("from_bar".equals(widgetTey)) {
			fromEdit.updateValue();
			leading = "from";
		} else if ("to".equals(widgetTey)) {
			updateTimeFromEdit("to", timeMax, toEdit, toBar);
			leading = "to";
		} else if ("to_bar".equals(widgetTey)) {
			toEdit.updateValue();
			leading = "to";
		} 
		
		if (leading != null) {
			long from = model.getLong("from", timeMin);
			long to = model.getLong("to", timeMax);
			if (to < from) {
				if ("to".equals(leading)) {
					model.setLong("from", to);
					fromEdit.updateValue();
					fromBar.updateValue();
				} else {
					model.setLong("to", from);
					toEdit.updateValue();
					toBar.updateValue();
				}
			}
			
			if ("to".equals(leading)) {
				model.setBool("track_to", to >= timeMax);
			}
		}
		
	}
	
	private void updateTimeFromEdit(String modelKey, long defaultValue, DateTimeHelperPair sourceEdit, TransformSeekBar sourceBar) {
		long value = model.getLong(modelKey, defaultValue);
		long good = inRange(value);
		if (value != good) {
			model.setLong(modelKey, good);
			sourceEdit.updateValue();
		}
		
		sourceBar.updateValue();		
	}
	
	private long inRange(long value) {
		if (value < timeMin) {
			return timeMin;
		} else if (value > timeMax) {
			return timeMax;
		} else {
			return value;
		}
	}

	private class RangeTransform implements SeekBarTransform {

		@Override
		public int transformSeekBarValue2Pos(Number value) {
			return (int) ((1e6 * ((Long)value - timeMin)) / timeGap);
		}

		@Override
		public Number transformSeekBarPos2Value(int pos) {
			return timeMin + ((long)(timeGap * pos / 1e6));
		}		
	}
	
}
