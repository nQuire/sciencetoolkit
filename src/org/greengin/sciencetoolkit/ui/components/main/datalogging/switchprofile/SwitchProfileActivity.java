package org.greengin.sciencetoolkit.ui.components.main.datalogging.switchprofile;

import java.util.List;
import java.util.Set;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.os.Build;

public class SwitchProfileActivity extends ActionBarActivity implements ModelNotificationListener {

	String currentProfileId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch_profile);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.switch_profile, menu);
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
	
	@Override
	public void onResume() {
		super.onResume();
		updateList();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	private void updateList() {
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragments) {
				if (fragment instanceof SwitchProfileFragment) {
					getSupportFragmentManager().beginTransaction().remove(fragment).commit();
				}
			}
		}

		Set<String> profileIds = ProfileManager.getInstance().getProfileIds();
		for (String profileId : profileIds) {
			SwitchProfileFragment fragment = new SwitchProfileFragment();
			Bundle args = new Bundle();
			args.putString(SwitchProfileFragment.ARG_PROFILE, profileId);
			fragment.setArguments(args);
			getSupportFragmentManager().beginTransaction().add(R.id.profile_list, fragment).commit();
		}
	}

	@Override
	public void modelNotificationReveiced(String msg) {
	}

}
