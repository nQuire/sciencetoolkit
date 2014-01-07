package org.greengin.sciencetoolkit.ui.components.main.profiles;

import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.CreateProfileDialogFragment;
import org.greengin.sciencetoolkit.ui.ParentListFragment;
import org.greengin.sciencetoolkit.ui.components.main.profiles.files.FileManagementActivity;
import org.greengin.sciencetoolkit.ui.remote.RemoteCapableActivity;
import org.greengin.sciencetoolkit.ui.remote.UpdateRemoteAction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

public class ProfileListFragment extends ParentListFragment implements DataLoggerStatusListener, ModelNotificationListener {

	public static final String REQUEST_SELECTED_PROFILE = "REQUEST_SELECTED_PROFILE";

	LinearLayout switchButtonBar;
	Button switchButton;
	LinearLayout updateButtonBar;
	Button updateButton;

	BroadcastReceiver requestReceiver;
	Vector<ProfileFragment> profileFragments;
	String selectedProfile;

	public ProfileListFragment() {
		super(R.id.profile_list);
		profileFragments = new Vector<ProfileFragment>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		requestReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				setSelectedProfile(intent.getStringExtra(Arguments.ARG_PROFILE), true);
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_profile_list, container, false);

		switchButtonBar = (LinearLayout) rootView.findViewById(R.id.switch_profile_button_bar);
		switchButton = (Button) rootView.findViewById(R.id.switch_profile);
		switchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!DataLogger.get().isRunning()) {
					ProfileManager.get().switchActiveProfile(selectedProfile);
				}
			}
		});
		updateButtonBar = (LinearLayout) rootView.findViewById(R.id.update_remote_profiles_button_bar);
		updateButton = (Button) rootView.findViewById(R.id.update_remote_profiles);
		updateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("stk update", "test");
				((RemoteCapableActivity)getActivity()).remoteRequest(new UpdateRemoteAction());
			}
		});

		updateChildrenList();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(requestReceiver, new IntentFilter(REQUEST_SELECTED_PROFILE));
		SettingsManager.get().registerDirectListener("profiles", this);
		ProfileManager.get().registerUIListener(this);
		DataLogger.get().registerStatusListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		DataLogger.get().unregisterStatusListener(this);
		ProfileManager.get().unregisterUIListener(this);
		SettingsManager.get().unregisterDirectListener("profiles", this);
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(requestReceiver);
	}

	@Override
	public void modelNotificationReceived(String msg) {
		if ("list".equals(msg)) {
			updateView();
		} else if ("profiles".equals(msg)) {
			updateButtonBars();
		}
	}

	private void updateView() {
		setSelectedProfile(ProfileManager.get().getActiveProfileId(), false);
		updateChildrenList();
		updateButtonBars();
	}

	private void updateSelectedChild() {
		for (ProfileFragment f : profileFragments) {
			f.updateRadioChecked(selectedProfile);
		}
	}

	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		profileFragments.clear();

		Vector<Fragment> fragments = new Vector<Fragment>();

		for (String profileId : ProfileManager.get().getProfileIds()) {
			ProfileFragment fragment = new ProfileFragment();
			Bundle args = new Bundle();
			args.putString(Arguments.ARG_PROFILE, profileId);
			fragment.setArguments(args);
			fragments.add(fragment);
			profileFragments.add(fragment);
		}

		return fragments;
	}

	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return child instanceof ProfileFragment;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.profiles, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_data_logging_new:
			CreateProfileDialogFragment.showCreateProfileDialog(getChildFragmentManager(), false, false);
			return true;

		case R.id.action_data_file_management: {
			Intent intent = new Intent(getActivity(), FileManagementActivity.class);
			startActivity(intent);
			return true;
		}

		}

		return super.onOptionsItemSelected(item);
	}

	private void setSelectedProfile(String profileId, boolean updateChildren) {
		if (!profileId.equals(selectedProfile)) {
			this.selectedProfile = profileId;
			
			updateButtonBars();
			
			if (updateChildren) {
				updateSelectedChild();
			}
		}
	}
	
	private void updateButtonBars() {
		boolean showButtonBar = !DataLogger.get().isRunning() && this.selectedProfile != null && !this.selectedProfile.equals(ProfileManager.get().getActiveProfileId());
		 
		LayoutParams switchlp = switchButtonBar.getLayoutParams();
		LayoutParams updatelp = updateButtonBar.getLayoutParams();
		if (showButtonBar) {
			switchlp.height = LayoutParams.WRAP_CONTENT;
			switchButton.setText(ProfileManager.get().profileIdIsDefault(this.selectedProfile) ? R.string.switch_profile_to_default : R.string.switch_profile);
			updatelp.height = 0;
		} else {
			switchlp.height = 0;
			updatelp.height = LayoutParams.WRAP_CONTENT;
		}
		switchButtonBar.setLayoutParams(switchlp);		
		updateButtonBar.setLayoutParams(updatelp);		
	}

	@Override
	public void dataLoggerStatusModified() {
		updateButtonBars();
	}
}
