package org.greengin.sciencetoolkit.ui.main.share;

import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ShareFragment extends EventFragment implements OnClickListener, ProfileItemEventListener {

	View activeProfilePanel;
	TextView noActiveProfileNotice;
	ProfileItemManager itemManager;
	ShareListAdapter adapter;
	
	String selectedProfileId;

	ImageButton buttonCloseActive;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.itemManager = new ProfileItemManager(this);
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

		buttonCloseActive = (ImageButton) rootView.findViewById(R.id.share_active_project_close);
		buttonCloseActive.setOnClickListener(this);

		/*
		 * recordingPanel = (LinearLayout)
		 * rootView.findViewById(R.id.record_controls); updateButtonPanel();
		 */

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

	private void stopAnimation() {
		/*
		 * Animation anim = seriesPanel.getAnimation();
		 * 
		 * if (anim != null) { anim.cancel(); anim.reset(); }
		 */
	}

	private void animateraiseSeriesPanel(boolean up) {
		/*
		 * stopAnimation(); Animation anim = new HeightAnimation(seriesPanel, up
		 * ? recordingPanel.getHeight() : 0); anim.setDuration(500);
		 * anim.setFillAfter(true); seriesPanel.startAnimation(anim);
		 */
	}

	private void updateProfiles() {
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
		}
	}

	@Override
	public void profileSelected(String profileId) {
		this.selectedProfileId = profileId;
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

}