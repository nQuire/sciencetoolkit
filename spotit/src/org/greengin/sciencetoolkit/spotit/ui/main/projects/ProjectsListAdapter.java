package org.greengin.sciencetoolkit.spotit.ui.main.projects;

import java.util.Vector;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;
import org.greengin.sciencetoolkit.spotit.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ProjectsListAdapter extends BaseAdapter {

	LayoutInflater inflater;
	ProjectItemManager manager;
	Vector<Model> projects;
	String selectedProjectId;
	

	public ProjectsListAdapter(LayoutInflater inflater, ProjectItemManager manager) {
		this.inflater = inflater;
		this.manager = manager;
		this.projects = new Vector<Model>();
		updateProfileList(null, false);
	}

	public void updateProfileList(String selectedProfileId) {
		updateProfileList(selectedProfileId, true);
	}

	public void updateProfileList(String selectedProjectId, boolean notify) {
		this.selectedProjectId = selectedProjectId;
		projects.clear();
		for (String projectId : ProjectManager.get().getProjectIds()) {
			projects.add(ProjectManager.get().get(projectId));
		}

		if (notify) {
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return projects.size();
	}

	@Override
	public Model getItem(int position) {
		return projects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Model project = projects.get(position);
		String projectId = project.getString("id");

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_projects_item, parent, false) : convertView;

		manager.prepareView(view, project, ProjectManager.get().projectIdIsActive(projectId), projectId.equals(selectedProjectId));

		return view;
	}

}