package org.greengin.sciencetoolkit.ui.components.main.datalogging.config;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.ProfileModelFragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class ProfileSensorSettingsActivity extends SettingsControlledActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_sensor_settings);
		
		String profileId = this.getIntent().getExtras().getString("profile");
		String sensorId = this.getIntent().getExtras().getString("sensor");
		
		Model profile = ProfileManager.getInstance().get(profileId);
		
		SensorWrapper sensor = SensorWrapperManager.getInstance().getSensor(sensorId);

		if (sensor != null) {
			TextView sensorNameView = (TextView)getWindow().getDecorView().findViewById(R.id.profile_sensor_name);
			sensorNameView.setText(profile.getString("title") + " / " + sensor.getName());
			ProfileModelFragmentManager.insert(getSupportFragmentManager(), R.id.profile_sensor_period_settings, new String[]{"period", profileId, sensorId});
			ProfileModelFragmentManager.insert(getSupportFragmentManager(), R.id.profile_sensor_settings, new String[]{"sensor", profileId, sensorId});
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sensor_settings, menu);
		return true;
	}

}
