package org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect;

import java.util.Vector;

public interface SelectSensorActionListener {
	void sensorsSelected(Vector<String> selected);
	boolean sensorIsAvailable(String sensorId);
}
