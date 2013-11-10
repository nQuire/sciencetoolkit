package org.greengin.sciencetoolkit.ui.components.main.datalogging.config;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.ControlledRotationActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.ProfileModelFragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ProfileSensorSettingsActivity extends ControlledRotationActivity {

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
		setupActionBar();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sensor_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
