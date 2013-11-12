package org.greengin.sciencetoolkit.ui.components.appsettings;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.os.Bundle;

public class AppSettingsActivity extends SettingsControlledActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);
		
		SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.app_settings, "app");
	}
	
}
