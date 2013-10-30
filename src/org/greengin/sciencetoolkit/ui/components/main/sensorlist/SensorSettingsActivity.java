package org.greengin.sciencetoolkit.ui.components.main.sensorlist;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.settings.SettingsFragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class SensorSettingsActivity extends FragmentActivity {

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
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
