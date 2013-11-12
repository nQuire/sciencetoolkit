package org.greengin.sciencetoolkit.ui.plotting;


import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.Model;

import android.database.Cursor;

public class ProfileDataXYSensorSeriesWrapper extends AbstractXYSensorSeriesWrapper {

	Cursor cursor;

	public ProfileDataXYSensorSeriesWrapper(SensorWrapper sensor, Model settings, String seriesSettingPrefix, Cursor cursor) {
		super(sensor, settings, seriesSettingPrefix);

		this.cursor = null;
		setCursor(cursor);
	}

	public void setCursor(Cursor cursor) {
		if (this.cursor != null) {
			this.cursor.close();
		}

		this.cursor = cursor;
	}

	@Override
	Number getDataX(int i) {
		cursor.moveToPosition(i);
		return cursor.getLong(0);
	}

	@Override
	Number getDataY(int i, int seriesIndex) {
		try {
			cursor.moveToPosition(i);
			return Float.parseFloat(cursor.getString(1).split("\\|")[seriesIndex]);
		} catch (Exception e) {
			return 0.f;
		}
	}

	@Override
	int getDataSize() {
		return cursor != null ? cursor.getCount() : 0;
	}
}
