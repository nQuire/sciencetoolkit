package org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor;

public interface ProfileSensorActionListener {
	void profileSensorRateDelete(String sensorId);
	void profileSensorRateEditComplete(boolean set, String profileSensorId, double rate, int units);
}
