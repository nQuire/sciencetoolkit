package org.greengin.sciencetoolkit.ui.main.share;

import java.util.List;

import org.greengin.sciencetoolkit.logic.remote.UpdateRemoteAction;
import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.common.ui.base.RemoteCapableActivity;
import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.ui.base.SenseItArguments;
import org.greengin.sciencetoolkit.ui.base.dlgs.editprofile.ProfileActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.editprofile.ProfileDlg;
import org.greengin.sciencetoolkit.ui.base.dlgs.edittext.EditTextActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.edittext.EditTextDlg;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;
import org.greengin.sciencetoolkit.ui.dataviewer.DataViewerActivity;
import org.greengin.sciencetoolkit.ui.remote.SenseItProjectBrowserActivity;

import android.content.Intent;
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

public class ShareFragment extends EventFragment implements OnClickListener,
		ProjectItemEventListener, EditTextActionListener, ProfileActionListener {

	ProjectItemManager itemManager;
	ShareListAdapter adapter;

	ImageButton buttonAddProject;
	ImageButton buttonUpdateProject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.itemManager = new ProjectItemManager(this);
		eventManager.setListener(new EventListener());

		eventManager.listenToSettings("profiles");
		eventManager.listenToLoggerStatus();
		eventManager.listenToProfiles();

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.view_share, container, false);

		this.adapter = new ShareListAdapter(inflater, this.itemManager);

		ListView list = (ListView) rootView.findViewById(R.id.profile_list);
		list.setAdapter(adapter);

		buttonAddProject = (ImageButton) rootView
				.findViewById(R.id.share_project_add);
		buttonAddProject.setOnClickListener(this);

		buttonUpdateProject = (ImageButton) rootView
				.findViewById(R.id.share_project_cloud);
		buttonUpdateProject.setVisibility(View.VISIBLE);
		buttonUpdateProject.setOnClickListener(this);

		updateProfiles();

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuResource = R.menu.share;
		inflater.inflate(menuResource, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_application_connect:
			Intent intent = new Intent(getActivity(),
					SenseItProjectBrowserActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateProfiles() {
		this.adapter.updateProfileList(null);
	}

	private class EventListener extends EventManagerListener {

		@Override
		public void events(List<String> settingsEvents,
				List<String> profileEvents, List<String> dataEvents,
				List<String> dataStatusEvents, boolean whilePaused) {
			updateProfiles();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == buttonAddProject) {
			EditTextDlg.open(this.getActivity(),
					R.string.new_project_dlg_title,
					R.string.new_project_dlg_message,
					R.string.new_project_dlg_oklabel, "", true, this);
		} else if (v == buttonUpdateProject) {
			((RemoteCapableActivity) getActivity())
					.remoteRequest(new UpdateRemoteAction());
		}
	}

	@Override
	public void profileSelected(String profileId) {
		if (DataLogger.get().isIdle() && profileId != null
				&& !ProfileManager.get().profileIdIsActive(profileId)) {
			ProfileManager.get().switchActiveProfile(profileId);
			// this.updateProfiles();
		}
	}

	@Override
	public void profileView(String profileId) {
		Intent intent = new Intent(getActivity(), DataViewerActivity.class);
		intent.putExtra(SenseItArguments.ARG_PROFILE, profileId);
		startActivity(intent);
	}

	@Override
	public void editTextComplete(String value) {
		if (value != null) {
			ProfileManager.get().createProfile(value, true);
		}
	}

	@Override
	public void profileEdit(String profileId) {
		Model profile = ProfileManager.get().get(profileId);
		if (profile != null) {
			boolean canDelete = canDeleteProfile(profile);
			ProfileDlg.open(getActivity(), profile, canDelete, this);
		}
	}

	@Override
	public void profileDelete(Model profile) {
		if (canDeleteProfile(profile)) {
			ProfileManager.get().deleteProfile(profile.getString("id"));
		}
	}

	private boolean canDeleteProfile(Model profile) {
		return !ProfileManager.get().profileIsActive(profile);
	}

	@Override
	public void profileTitleEditComplete(Model profile, String title) {
		if (title != null) {
			profile.setString("title", title);
		}
	}

}