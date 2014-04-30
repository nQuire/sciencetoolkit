package org.greengin.sciencetoolkit.ui.remote;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ProjectBrowserListAdapter extends BaseAdapter {

	ProjectMembershipListener listener;
	LayoutInflater inflater;

	boolean isRemote = false;
	Vector<ProjectData> projects;
	
	OnClickListener toggleListener;

	public ProjectBrowserListAdapter(ProjectMembershipListener listener, LayoutInflater inflater) {
		this.listener = listener;
		this.inflater = inflater;
		this.projects = null;
		
		toggleListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				ProjectData project = (ProjectData) v.getTag();
				ProjectBrowserListAdapter.this.listener.projectMembershipAction(project.id, !project.joined);
			}
		};
		
		updateProjectList(null);
	}
	
	public void updateProjectList(Vector<ProjectData> projects) {
		this.projects = projects;
	}

	@Override
	public int getCount() {
		return projects != null ? projects.size() : 0;
	}

	@Override
	public ProjectData getItem(int position) {
		return projects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return projects.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ProjectData project = projects.get(position);
		
		Log.d("stk projects", position + " " + project.id + " " + project.joined);

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_project_browser_item, parent, false) : convertView;
		
		Button toggle = (Button) view.findViewById(R.id.project_toggle);
		
		if (newView) {
			toggle.setOnClickListener(toggleListener);
		}
		
		toggle.setTag(project);
		toggle.setText(view.getResources().getString(project.joined ? R.string.project_browser_leave : R.string.project_browser_join));
		
		TextView projectTitleView = (TextView) view.findViewById(R.id.project_title);
		projectTitleView.setText(project.title);

		TextView projectAuthorView = (TextView) view.findViewById(R.id.project_author);
		projectAuthorView.setText(project.author);
		
		return view;
	}

}