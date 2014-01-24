package org.greengin.sciencetoolkit.ui.main.share;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ShareListAdapter extends BaseAdapter {

	LayoutInflater inflater;
	ProfileItemManager manager;
	Vector<Model> profiles;
	String selectedProfileId;

	public ShareListAdapter(LayoutInflater inflater, ProfileItemManager manager) {
		this.inflater = inflater;
		this.manager = manager;
		this.profiles = new Vector<Model>();
		updateProfileList(null, false);
	}

	public void updateProfileList(String selectedProfileId) {
		updateProfileList(selectedProfileId, true);
	}

	public void updateProfileList(String selectedProfileId, boolean notify) {
		this.selectedProfileId = selectedProfileId;
		String activeProfileId = ProfileManager.get().getActiveProfileId();
		profiles.clear();
		for (String profileId : ProfileManager.get().getProfileIds()) {
			if (!ProfileManager.DEFAULT_PROFILE_ID.equals(profileId) && !profileId.equals(activeProfileId)) {
				profiles.add(ProfileManager.get().get(profileId));
			}
		}

		int defaultCount = DataLogger.get().getSeriesCount(ProfileManager.DEFAULT_PROFILE_ID);
		if (defaultCount > 0) {
			profiles.add(ProfileManager.get().get(ProfileManager.DEFAULT_PROFILE_ID));
		}

		if (notify) {
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return profiles.size();
	}

	@Override
	public Model getItem(int position) {
		return profiles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Model profile = profiles.get(position);

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_share_item, parent, false) : convertView;

		manager.prepareView(view, profile, profile.getString("id").equals(selectedProfileId), true);

		return view;
	}

}