package org.greengin.sciencetoolkit.ui.main.share;

import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.animations.Animations;
import org.greengin.sciencetoolkit.ui.base.dlgs.EditTextActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.EditTextDlg;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ShareFragment extends EventFragment implements OnClickListener, ProjectItemEventListener, EditTextActionListener {

	View activeProfilePanel;
	TextView noActiveProfileNotice;
	ProjectItemManager itemManager;
	ShareListAdapter adapter;

	String selectedProfileId;

	View panelSwitchActive;
	int panelSwitchActiveheight;
	ImageButton buttonAddProject;
	ImageButton buttonUpdateProject;
	ImageButton buttonCloseActive;
	ImageButton buttonSwitchActive;
	ImageButton buttonCancelSwitch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.itemManager = new ProjectItemManager(this);
		eventManager.setListener(new EventListener());

		eventManager.listenToSettings("profiles");
		eventManager.listenToLoggerStatus();
		eventManager.listenToProfiles();

		this.selectedProfileId = null;

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.view_share, container, false);

		this.adapter = new ShareListAdapter(inflater, this.itemManager);

		this.activeProfilePanel = rootView.findViewById(R.id.active_profile);
		this.noActiveProfileNotice = (TextView) rootView.findViewById(R.id.no_active_profile);

		ListView list = (ListView) rootView.findViewById(R.id.profile_list);
		list.setAdapter(adapter);

		buttonAddProject = (ImageButton) rootView.findViewById(R.id.share_project_add);
		buttonAddProject.setOnClickListener(this);
		
		buttonUpdateProject = (ImageButton) rootView.findViewById(R.id.share_project_cloud);
		buttonUpdateProject.setOnClickListener(this);

		buttonCloseActive = (ImageButton) rootView.findViewById(R.id.share_active_project_close);
		buttonCloseActive.setOnClickListener(this);

		panelSwitchActive = rootView.findViewById(R.id.switch_profile_controls);
		panelSwitchActiveheight = Animations.measureHeight(panelSwitchActive);
		Log.d("stk share", "" + panelSwitchActiveheight);

		buttonSwitchActive = (ImageButton) panelSwitchActive.findViewById(R.id.switch_profile_confirm);
		buttonSwitchActive.setOnClickListener(this);
		buttonCancelSwitch = (ImageButton) panelSwitchActive.findViewById(R.id.switch_profile_cancel);
		buttonCancelSwitch.setOnClickListener(this);

		LayoutParams params = panelSwitchActive.getLayoutParams();
		params.height = 0;
		panelSwitchActive.setLayoutParams(params);

		updateProfiles();

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.record, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		}

		return super.onOptionsItemSelected(item);
	}

	private void animateSwitchControls(boolean shown) {
		Animations.animateHeight(panelSwitchActive, shown ? panelSwitchActiveheight : 0);
	}

	private void updateProfiles() {
		buttonCloseActive.setEnabled(!ProfileManager.get().activeProfileIsDefault());
		buttonSwitchActive.setEnabled(this.selectedProfileId != null);

		if (ProfileManager.DEFAULT_PROFILE_ID.equals(ProfileManager.get().getActiveProfileId())) {
			activeProfilePanel.setVisibility(View.GONE);
			noActiveProfileNotice.setVisibility(View.VISIBLE);
		} else {
			this.itemManager.prepareView(activeProfilePanel, ProfileManager.get().getActiveProfile(), false, false);
			noActiveProfileNotice.setVisibility(View.GONE);
			activeProfilePanel.setVisibility(View.VISIBLE);
		}

		this.adapter.updateProfileList(this.selectedProfileId);
	}

	private class EventListener extends EventManagerListener {

		@Override
		public void events(List<String> settingsEvents, List<String> profileEvents, List<String> dataEvents, boolean whilePaused) {
			updateProfiles();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == buttonCloseActive) {
			ProfileManager.get().switchActiveProfile(ProfileManager.DEFAULT_PROFILE_ID);
		} else if (v == buttonCancelSwitch) {
			animateSwitchControls(false);
			this.selectedProfileId = null;
			this.updateProfiles();
		} else if (v == buttonSwitchActive) {
			animateSwitchControls(false);
			if (selectedProfileId != null) {
				String profileId = selectedProfileId;
				selectedProfileId = null;
				ProfileManager.get().switchActiveProfile(profileId);
			}
		} else if (v == buttonAddProject) {
			EditTextDlg.open(this.getActivity(), R.string.new_project_dlg_title, R.string.new_project_dlg_message, R.string.new_project_dlg_oklabel, "", true, this);
		}
	}

	@Override
	public void profileSelected(String profileId) {
		boolean validSelection = profileId != null && !ProfileManager.DEFAULT_PROFILE_ID.equals(profileId) && !ProfileManager.get().profileIdIsActive(profileId);
		this.selectedProfileId = validSelection ? profileId : null;
		this.animateSwitchControls(validSelection);
		this.updateProfiles();
	}

	@Override
	public void profileView(String profileId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void profileDelete(String profileId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void editTextComplete(String value) {
		if (value != null) {
			ProfileManager.get().createProfile(value, false, false);
		}
	}

}