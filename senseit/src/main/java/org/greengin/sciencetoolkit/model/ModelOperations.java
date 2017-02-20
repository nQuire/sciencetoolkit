package org.greengin.sciencetoolkit.model;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;

import android.hardware.SensorManager;

public class ModelOperations {
	static final int[] delayOptions = new int[] { SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_GAME };
	static final int[] delays = new int[] { 200000, 60000, 20000 };

	public static double fitFateInRange(double rate, int units, Double min, Double max) {
		double unitK;

		switch (units) {

		case 1: // samples/min
			unitK = 60.;
			break;
		case 2: // samples/hour
			unitK = 3600.;
			break;
		default:
			unitK = 1;
			break;
		}

		double afterUnits = rate / unitK;

		if (min != null && afterUnits < min) {
			return min.doubleValue() * unitK;
		} else if (max != null && afterUnits > max) {
			return max.doubleValue() * unitK;
		} else {
			return rate;
		}
	}

	public static int rate2period(Model model, String key, double defaultValue, Double min, Double max) {
		double v = model.getDouble(key, defaultValue);
		int units = model.getInt(key + "_ux", 0);

		switch (units) {
		case 0: // samples/s
			break;
		case 1: // samples/min
			v /= 60.;
			break;
		case 2: // samples/hour
			v /= 3600.;
			break;
		}

		if (min != null && v < min) {
			v = min;
		}
		if (max != null && v > max) {
			v = max;
		}

		return v > 0 ? (int) (1000 / v) : 0;
	}

	public static int delayOption2deviceSensorDelay(Model model, String key, int defaultValue, SensorWrapper sensor) {
		int validOptions = getValidDeviceSensorDelayOptions(sensor);
		int value = model.getInt(key, defaultValue);
		if (value < validOptions) {
			return delayOptions[value];
		} else {
			return SensorManager.SENSOR_DELAY_FASTEST;
		}
	}

	public static int getValidDeviceSensorDelayOptions(SensorWrapper sensor) {
		int minDelay = sensor.getMinDelay();
		for (int i = 0; i < 3; i++) {
			if (minDelay >= delays[i]) {
				return i;
			}
		}

		return 3;
	}
}
