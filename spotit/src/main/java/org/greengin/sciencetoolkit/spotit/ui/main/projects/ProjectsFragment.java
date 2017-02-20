package org.greengin.sciencetoolkit.spotit.ui.main.projects;

import java.util.List;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.ui.base.RemoteCapableActivity;
import org.greengin.sciencetoolkit.spotit.logic.remote.UpdateRemoteAction;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventFragment;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventManagerListener;
import org.greengin.sciencetoolkit.spotit.ui.remote.SpotItProjectBrowserActivity;

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

public class ProjectsFragment extends SpotItEventFragment implements
		ProjectItemEventListener, OnClickListener {

	ProjectItemManager itemManager;
	ProjectsListAdapter adapter;

	ImageButton buttonAddProject;
	ImageButton buttonUpdateProject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.itemManager = new ProjectItemManager(this);
		eventManager.setListener(new EventListener());

		eventManager.listenToSettings("profiles");
		eventManager.listenToLoggedData();
		eventManager.listenToProfiles();

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.view_projects, container,
				false);

		this.adapter = new ProjectsListAdapter(inflater, this.itemManager);

		ListView list = (ListView) rootView.findViewById(R.id.project_list);
		list.setAdapter(adapter);

		buttonUpdateProject = (ImageButton) rootView
				.findViewById(R.id.share_project_cloud);
		buttonUpdateProject.setVisibility(View.VISIBLE);
		buttonUpdateProject.setOnClickListener(this);

		updateProfiles();

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuResource = R.menu.projects;
		inflater.inflate(menuResource, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_application_connect:
			Intent intent = new Intent(getActivity(),
					SpotItProjectBrowserActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateProfiles() {
		this.adapter.updateProfileList(null);
	}

	private class EventListener extends SpotItEventManagerListener {

		@Override
		public void events(List<String> settingsEvents,
				List<String> profileEvents, List<String> dataEvents,
				boolean whilePaused) {
			updateProfiles();
		}
	}

	@Override
	public void projectSelected(Model project) {
		ProjectManager.get().switchActiveProject(project.getString("id"));
	}

	@Override
	public void projectDelete(Model project) {
		if (!ProjectManager.get().projectIsActive(project)) {
			ProjectManager.get().deleteProject(project.getString("id"));
		}
	}

	@Override
	public void onClick(View v) {
		if (v == buttonUpdateProject) {
			Intent intent = new Intent(getActivity(),
					SpotItProjectBrowserActivity.class);
			startActivity(intent);
		}
	}
}