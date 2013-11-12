package org.greengin.sciencetoolkit.ui.components.main.sensorlist.choose;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.os.Bundle;

public class SensorListSettingsActivity extends SettingsControlledActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_list_settings);
		SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.settings_container, "sensor_list");
	}
	
}
