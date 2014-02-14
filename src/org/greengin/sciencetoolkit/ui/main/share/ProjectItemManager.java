package org.greengin.sciencetoolkit.ui.main.share;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProjectItemManager {

	private enum EventType {
		VIEW, DELETE, SELECT, EDIT
	};

	ProjectItemEventListener listener;
	OnClickListener viewListener;
	OnClickListener deleteListener;
	OnClickListener selectListener;
	OnClickListener editListener;

	int editablePaddingVertical;
	int editablePaddingHorizontal;
	int editableBackground;
	int transparentBackground;
	
	public static void setProjectIcons(View containerView, Model profile) {
		boolean isDefault = ProfileManager.DEFAULT_PROFILE_ID.equals(profile.getString("id"));
		boolean isRemote = profile.getBool("is_remote");
		boolean geolocated = profile.getBool("requires_location");

		containerView.findViewById(R.id.project_type_device).setVisibility(!isDefault && !isRemote ? View.VISIBLE : View.GONE);
		containerView.findViewById(R.id.project_type_cloud).setVisibility(!isDefault && isRemote ? View.VISIBLE : View.GONE);
		containerView.findViewById(R.id.project_type_geolocated).setVisibility(!isDefault && geolocated ? View.VISIBLE : View.GONE);
	}

	public ProjectItemManager(Context context, ProjectItemEventListener listener) {
		this.listener = listener;
		this.viewListener = new EventClickListener(EventType.VIEW);
		this.deleteListener = new EventClickListener(EventType.DELETE);
		this.selectListener = new EventClickListener(EventType.SELECT);
		this.editListener = new EventClickListener(EventType.EDIT);
		
		editablePaddingVertical = context.getResources().getDimensionPixelSize(R.dimen.dark_text_button_padding_vertical);
		editablePaddingHorizontal = context.getResources().getDimensionPixelSize(R.dimen.dark_text_button_padding_horizontal);
		editableBackground = context.getResources().getColor(R.color.dark_text_button);
		transparentBackground = context.getResources().getColor(R.color.transparent);
	}

	public void prepareView(View profileView, Model profile, boolean active, boolean selected, boolean canDelete) {
		String profileId = profile.getString("id");
		int count = DataLogger.get().getSeriesCount(profileId);

		boolean canView = count > 0;

		TextView title = (TextView) profileView.findViewById(R.id.profile_name);
		boolean isDefault = ProfileManager.DEFAULT_PROFILE_ID.equals(profileId);
		
		if (isDefault) {
			title.setTextAppearance(profileView.getContext(), R.style.italicText);
			title.setText(profileView.getContext().getResources().getString(R.string.share_default_profile_title));
			title.setPadding(0, 0, 0, 0);
			title.setBackgroundColor(transparentBackground);
			title.setOnClickListener(null);
			canDelete = canDelete && count > 0;
		} else {
			title.setTextAppearance(profileView.getContext(), R.style.boldText);
			title.setText(profile.getString("title"));
			title.setPadding(editablePaddingHorizontal, editablePaddingVertical, editablePaddingHorizontal, editablePaddingVertical);
			title.setBackgroundColor(editableBackground);
			title.setOnClickListener(editListener);
			title.setTag(profileId);
		}
		
		LinearLayout activeLabelContainer = (LinearLayout)  profileView.findViewById(R.id.active_project);
		if (active) {
			TextView activeLabel = (TextView) activeLabelContainer.findViewById(R.id.active_project_label);
			activeLabel.setText(isDefault ? R.string.share_default_active : R.string.share_project_active);
			activeLabelContainer.setVisibility(View.VISIBLE);
		} else {
			activeLabelContainer.setVisibility(View.GONE);
		}
		

		ImageButton viewButton = (ImageButton) profileView.findViewById(R.id.profile_view);
		ImageButton deleteButton = (ImageButton) profileView.findViewById(R.id.profile_discard);

		profileView.setOnClickListener(selectListener);
		viewButton.setOnClickListener(viewListener);
		deleteButton.setOnClickListener(deleteListener);
		title.setOnClickListener(editListener);

		profileView.setTag(profileId);
		viewButton.setTag(profileId);
		deleteButton.setTag(profileId);

		viewButton.setEnabled(canView);
		deleteButton.setEnabled(canDelete);

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

		profileView.setBackgroundColor(profileView.getContext().getResources().getColor(selected ? R.color.profile_selected_in_list : R.color.transparent));
		
		
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
			case DELETE:
				listener.profileDelete((String) v.getTag());
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
