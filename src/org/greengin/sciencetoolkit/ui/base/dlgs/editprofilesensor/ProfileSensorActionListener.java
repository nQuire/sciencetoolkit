package org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor;

public interface ProfileSensorActionListener {
	void profileSensorRateDelete();
	void profileSensorRateEditComplete(boolean set, String profileId, double rate, int units);
}
