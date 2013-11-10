package org.greengin.sciencetoolkit.ui;

import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;

public class ControlledRotationActivity extends ActionBarActivity {
	
	
	ModelNotificationListener controlledRotationListener;
	int controlledRotationLastValue;
	
	public ControlledRotationActivity() {
		this.controlledRotationLastValue = -1;
		this.controlledRotationListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReveiced(String msg) {
				updateScreenOrientation();
			}
		};
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		updateScreenOrientation();
		SettingsManager.getInstance().registerDirectListener("app", this.controlledRotationListener);
	}
	
	public void onPause(){
		super.onPause();
		SettingsManager.getInstance().unregisterDirectListener("app", this.controlledRotationListener);
	}
	
	private void updateScreenOrientation() {
		int value = SettingsManager.getInstance().get("app").getInt("screen_orientation", ModelDefaults.APP_SCREEN_ORIENTATION);
		if (value != controlledRotationLastValue) {
			controlledRotationLastValue = value;
			
			switch(value) {
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

}
