package org.greengin.sciencetoolkit.sensors;

import java.util.List;

import android.os.Bundle;

public interface SettingsListener {
	List<Bundle> getOptions();

	Object getOptionValue(String key);

	boolean setOptionValue(String key, Object value);
}
