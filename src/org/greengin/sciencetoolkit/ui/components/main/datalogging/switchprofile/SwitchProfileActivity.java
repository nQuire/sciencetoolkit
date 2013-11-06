package org.greengin.sciencetoolkit.ui.components.main.datalogging.switchprofile;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.ParentListActivity;
import org.greengin.sciencetoolkit.ui.components.main.datalogging.CreateProfileDialogFragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;

public class SwitchProfileActivity extends ParentListActivity implements ModelNotificationListener {

	public SwitchProfileActivity() {
		super(R.id.profile_list);
	}

	String selectedForChange;
	Button ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch_profile);
		ok = (Button) getWindow().getDecorView().findViewById(R.id.ok);
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
		case R.id.action_data_logging_new:
			CreateProfileDialogFragment.showCreateProfileDialog(getSupportFragmentManager(), false);
			break;
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
		updateView();
		ProfileManager.getInstance().registerDirectListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		ProfileManager.getInstance().unregisterDirectListener(this);
	}
	
	public void onClickOkButton(View view) {
		if (this.selectedForChange != null) {
			ProfileManager.getInstance().switchActiveProfile(this.selectedForChange);
		}
	}
	
	private void updateView() {
		selectedForChange = null;
		updateSwitchButton();
		updateChildrenList();
	}


	private void updateSwitchButton() {
		boolean enabled = selectedForChange != null && !selectedForChange.equals(ProfileManager.getInstance().getActiveProfileId());
		ok.setEnabled(enabled);
	}

	@Override
	public void modelNotificationReveiced(String msg) {
		if ("list".equals(msg)) {
			updateView();
		}
	}

	public void requestSelectedForChange(String profileId) {
		if (!profileId.equals(this.selectedForChange)) {
			this.selectedForChange = profileId;
			
			List<Fragment> fragments = getSupportFragmentManager().getFragments();
			if (fragments != null) {
				for (Fragment fragment : fragments) {
					if (fragment instanceof SwitchProfileFragment) {
						((SwitchProfileFragment)fragment).setSelectedForChangeProfile(this.selectedForChange);
					}
				}
			}
			updateSwitchButton();
		}
	}

	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		Vector<Fragment> fragments = new Vector<Fragment>();
		Set<String> profileIds = ProfileManager.getInstance().getProfileIds();
		for (String profileId : profileIds) {
			SwitchProfileFragment fragment = new SwitchProfileFragment();
			Bundle args = new Bundle();
			args.putString(SwitchProfileFragment.ARG_PROFILE, profileId);
			fragment.setArguments(args);
			fragments.add(fragment);
		}
		
		return fragments;
	}

}
