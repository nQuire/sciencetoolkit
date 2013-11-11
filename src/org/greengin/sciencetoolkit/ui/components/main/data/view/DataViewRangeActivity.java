package org.greengin.sciencetoolkit.ui.components.main.data.view;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.ControlledRotationActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.ProfileModelFragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class DataViewRangeActivity extends ControlledRotationActivity {

	String profileId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		profileId = getIntent().getExtras().getString("profile");
		
		setContentView(R.layout.activity_data_view_range);
		
		ProfileModelFragmentManager.insert(getSupportFragmentManager(), R.id.data_view_range_settings, new String[]{"datarange", profileId});
		setupActionBar();
	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_view_range, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
