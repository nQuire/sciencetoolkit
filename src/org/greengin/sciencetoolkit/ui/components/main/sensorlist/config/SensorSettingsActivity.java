package org.greengin.sciencetoolkit.ui.components.main.sensorlist.config;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class SensorSettingsActivity extends SettingsControlledActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_settings);
		
		String sensorId = this.getIntent().getExtras().getString("sensor");
		SensorWrapper sensor = SensorWrapperManager.getInstance().getSensor(sensorId);
		if (sensor != null) {
			TextView sensorNameView = (TextView)getWindow().getDecorView().findViewById(R.id.sensor_name);
			sensorNameView.setText(sensor.getName());
			SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.sensor_settings, "sensor:" + sensorId);
			SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.sensor_liveview_settings, "liveview:" + sensorId);
			SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.sensor_liveplot_settings, "liveplot:" + sensorId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sensor_settings, menu);
		return true;
	}
}
