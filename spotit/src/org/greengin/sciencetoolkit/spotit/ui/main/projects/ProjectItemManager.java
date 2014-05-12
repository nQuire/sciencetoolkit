package org.greengin.sciencetoolkit.spotit.ui.main.projects;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;
import org.greengin.sciencetoolkit.spotit.ui.base.dlgs.ProjectDeleteDlg;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProjectItemManager {

	private enum EventType {
		SELECT, DELETE
	};

	ProjectItemEventListener listener;
	OnClickListener selectListener;
	OnClickListener deleteListener;

	public ProjectItemManager(ProjectItemEventListener listener) {
		this.listener = listener;
		this.selectListener = new EventClickListener(EventType.SELECT);
		this.deleteListener = new EventClickListener(EventType.DELETE);
	}

	public void prepareView(View projectView, Model project, boolean active, boolean selected) {
		String projectId = project.getString("id");
		int count = DataManager.get().dataCount(projectId);

		TextView title = (TextView) projectView.findViewById(R.id.project_name);

		title.setTextAppearance(projectView.getContext(), R.style.boldText);
		title.setText(project.getString("title"));

		TextView activeLabel = (TextView) projectView.findViewById(R.id.active_project_label);
		activeLabel.setText(active ? R.string.project_active : R.string.project_inactive);
		activeLabel.setTextColor(projectView.getResources().getColor(active ? R.color.active_project_label : R.color.inactive_project_label));
		
		ImageButton activateButton = (ImageButton) projectView.findViewById(R.id.profile_activate);
		ImageView activeIcon = (ImageView) projectView.findViewById(R.id.profile_active_big);
		if (ProjectManager.get().projectIdIsActive(projectId)) {
			activateButton.setVisibility(View.GONE);
			activeIcon.setVisibility(View.VISIBLE);
		} else {
			activateButton.setEnabled(true);
			activeIcon.setVisibility(View.GONE);
			activateButton.setVisibility(View.VISIBLE);
		}

		activateButton.setOnClickListener(selectListener);
		activateButton.setTag(project);
		
		ImageButton deleteButton = (ImageButton) projectView.findViewById(R.id.project_delete);
		deleteButton.setEnabled(!active);
		deleteButton.setTag(project);
		deleteButton.setOnClickListener(deleteListener);
		

		String dataText;
		switch (count) {
		case 0:
			dataText = projectView.getResources().getString(R.string.data_count_none);
			break;
		case 1:
			dataText = projectView.getResources().getString(R.string.data_count_one);
			break;
		default:
			dataText = String.format(projectView.getResources().getString(R.string.data_count_many), count);
			break;
		}

		((TextView) projectView.findViewById(R.id.profile_data)).setText(dataText);
	}

	private class EventClickListener implements OnClickListener {
		EventType type;

		public EventClickListener(EventType type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			switch (type) {
			case SELECT:
				listener.projectSelected((Model) v.getTag());
				break;
			case DELETE:
				ProjectDeleteDlg.open(v.getContext(), (Model) v.getTag(), listener);
				break;
			}
		}
	}

}
