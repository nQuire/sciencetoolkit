package org.greengin.sciencetoolkit.ui.components.appsettings;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.ControlledRotationActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class AppSettingsActivity extends ControlledRotationActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);
		
		setupActionBar();
		
		SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.app_settings, "app");
	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
