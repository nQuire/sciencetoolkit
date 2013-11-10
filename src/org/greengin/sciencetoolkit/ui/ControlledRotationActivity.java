package org.greengin.sciencetoolkit.ui;

import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;

public class ControlledRotationActivity extends ActionBarActivity {

	ModelNotificationListener controlledRotationListener;
	int controlledRotationLastValue;
	boolean controlledRotationActive;

	public ControlledRotationActivity() {
		this(-1);
		this.controlledRotationLastValue = -1;
	}

	public ControlledRotationActivity(int overrideSettings) {
		this.controlledRotationLastValue = overrideSettings;
		this.controlledRotationActive = overrideSettings < 0;

		if (this.controlledRotationActive) {
			this.controlledRotationListener = new ModelNotificationListener() {
				@Override
				public void modelNotificationReveiced(String msg) {
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
