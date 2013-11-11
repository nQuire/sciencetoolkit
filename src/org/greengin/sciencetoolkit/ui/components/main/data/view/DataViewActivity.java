package org.greengin.sciencetoolkit.ui.components.main.data.view;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.ui.ControlledRotationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;

public class DataViewActivity extends ControlledRotationActivity {

	String profileId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_data_view);

		Model viewSettings = SettingsManager.getInstance().get("view_data_activity");

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			profileId = getIntent().getExtras().getString("profile");
			viewSettings.setString("profile", profileId);
		} else {
			profileId = viewSettings.getString("profile", null);
		}

		if (profileId != null) {
			updateFragment();
		}


		setupActionBar();
	}

	private void updateFragment() {
		Bundle args = new Bundle();
		args.putString("profile", profileId);
		Fragment fragment = new ListViewFragment();
		fragment.setArguments(args);

		getSupportFragmentManager().beginTransaction().replace(R.id.view_container, fragment).commit();
	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.data_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.action_data_view_range: {
			Intent intent = new Intent(this, DataViewRangeActivity.class);
			intent.putExtra("profile", profileId);
			startActivity(intent);
		}

		}
		return super.onOptionsItemSelected(item);
	}

}
