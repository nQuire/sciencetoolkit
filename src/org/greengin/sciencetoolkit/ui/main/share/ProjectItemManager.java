package org.greengin.sciencetoolkit.ui.main.share;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProjectItemManager {

	private enum EventType {
		VIEW, SELECT, EDIT
	};

	ProjectItemEventListener listener;
	OnClickListener viewListener;
	OnClickListener selectListener;
	OnClickListener editListener;

	public static void setProjectIcons(View containerView, Model profile) {
		boolean isDefault = ProfileManager.DEFAULT_PROFILE_ID.equals(profile.getString("id"));
		boolean isRemote = profile.getBool("is_remote");
		boolean geolocated = profile.getBool("requires_location");

		containerView.findViewById(R.id.project_type_device).setVisibility(!isDefault && !isRemote ? View.VISIBLE : View.GONE);
		containerView.findViewById(R.id.project_type_cloud).setVisibility(!isDefault && isRemote ? View.VISIBLE : View.GONE);
		containerView.findViewById(R.id.project_type_geolocated).setVisibility(!isDefault && geolocated ? View.VISIBLE : View.GONE);
	}

	public ProjectItemManager(ProjectItemEventListener listener) {
		this.listener = listener;
		this.viewListener = new EventClickListener(EventType.VIEW);
		this.selectListener = new EventClickListener(EventType.SELECT);
		this.editListener = new EventClickListener(EventType.EDIT);
	}

	public void prepareView(View profileView, Model profile, boolean active, boolean selected) {
		String profileId = profile.getString("id");
		int count = DataLogger.get().getSeriesCount(profileId);

		TextView title = (TextView) profileView.findViewById(R.id.profile_name);

		title.setTextAppearance(profileView.getContext(), R.style.boldText);
		title.setText(profile.getString("title"));

		LinearLayout activeLabelContainer = (LinearLayout) profileView.findViewById(R.id.active_project);
		if (active) {
			TextView activeLabel = (TextView) activeLabelContainer.findViewById(R.id.active_project_label);
			activeLabel.setText(R.string.share_project_active);
			activeLabelContainer.setVisibility(View.VISIBLE);
		} else {
			activeLabelContainer.setVisibility(View.GONE);
		}
		
		ImageButton activateButton = (ImageButton) profileView.findViewById(R.id.profile_activate);
		if (ProfileManager.get().profileIdIsActive(profileId)) {
			activateButton.setVisibility(View.GONE);
		} else {
			activateButton.setEnabled(DataLogger.get().isIdle());
			activateButton.setVisibility(View.VISIBLE);
		}

		ImageButton configButton = (ImageButton) profileView.findViewById(R.id.profile_config);
		configButton.setEnabled(DataLogger.get().isIdle() || !ProfileManager.get().profileIdIsActive(profileId));
		
		profileView.setOnClickListener(viewListener);
		configButton.setOnClickListener(editListener);
		activateButton.setOnClickListener(selectListener);

		profileView.setTag(profileId);
		configButton.setTag(profileId);
		activateButton.setTag(profileId);

		String dataText;
		switch (count) {
		case 0:
			dataText = profileView.getResources().getString(R.string.series_count_none);
			break;
		case 1:
			dataText = profileView.getResources().getString(R.string.series_count_one);
			break;
		default:
			dataText = String.format(profileView.getResources().getString(R.string.series_count_many), count);
			break;
		}

		((TextView) profileView.findViewById(R.id.profile_data)).setText(dataText);

		profileView.setBackgroundColor(profileView.getContext().getResources().getColor(selected ? R.color.list_item_selected
				: R.color.transparent));

		setProjectIcons(profileView, profile);
	}

	private class EventClickListener implements OnClickListener {
		EventType type;

		public EventClickListener(EventType type) {
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			switch (type) {
			case VIEW:
				listener.profileView((String) v.getTag());
				break;
			case SELECT:
				listener.profileSelected((String) v.getTag());
				break;
			case EDIT:
				listener.profileEdit((String) v.getTag());
				break;
			}
		}
	}

}
