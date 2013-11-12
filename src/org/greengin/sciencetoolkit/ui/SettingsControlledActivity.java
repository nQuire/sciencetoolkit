package org.greengin.sciencetoolkit.ui;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.components.appsettings.AppSettingsActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class SettingsControlledActivity extends ActionBarActivity {

	ModelNotificationListener controlledRotationListener;
	int controlledRotationLastValue;
	boolean controlledRotationActive;

	public SettingsControlledActivity() {
		this(-1);
		this.controlledRotationLastValue = -1;
	}

	public SettingsControlledActivity(int overrideSettings) {
		this.controlledRotationLastValue = overrideSettings;
		this.controlledRotationActive = overrideSettings < 0;

		if (this.controlledRotationActive) {
			this.controlledRotationListener = new ModelNotificationListener() {
				@Override
				public void modelNotificationReceived(String msg) {
					updateScreenOrientationValue();
				}
			};

			updateScreenOrientationValue();
		} else {
			this.controlledRotationListener = null;
			updateScreenOrientation();
		}
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_application_settings: {
			Intent intent = new Intent(getApplicationContext(), AppSettingsActivity.class);
			startActivity(intent);
			return true;
		}

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (controlledRotationActive) {
			updateScreenOrientationValue();
			SettingsManager.getInstance().registerDirectListener("app", this.controlledRotationListener);
		}
	}

	public void onPause() {
		super.onPause();
		if (controlledRotationActive) {
			SettingsManager.getInstance().unregisterDirectListener("app", this.controlledRotationListener);
		}
	}

	private void updateScreenOrientationValue() {
		int value = SettingsManager.getInstance().get("app").getInt("screen_orientation", ModelDefaults.APP_SCREEN_ORIENTATION);
		if (value != controlledRotationLastValue) {
			controlledRotationLastValue = value;
			updateScreenOrientation();
		}
	}

	private void updateScreenOrientation() {
		switch (controlledRotationLastValue) {
		case 0:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case 1:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case 2:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			break;
		}
	}

}
