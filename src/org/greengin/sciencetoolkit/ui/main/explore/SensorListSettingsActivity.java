package org.greengin.sciencetoolkit.ui.main.explore;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.base.modelconfig.SettingsFragmentManager;

import android.os.Bundle;

public class SensorListSettingsActivity extends SettingsControlledActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_explore_choose_sensors);
		SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.settings_container, "sensor_list");
	}
	
}
