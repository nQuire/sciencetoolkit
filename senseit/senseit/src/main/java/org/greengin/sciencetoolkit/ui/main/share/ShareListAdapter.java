package org.greengin.sciencetoolkit.ui.main.share;

import java.util.Vector;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ShareListAdapter extends BaseAdapter {

	LayoutInflater inflater;
	ProjectItemManager manager;
	Vector<Model> profiles;
	String selectedProfileId;
	

	public ShareListAdapter(LayoutInflater inflater, ProjectItemManager manager) {
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
		profiles.clear();
		for (String profileId : ProfileManager.get().getProfileIds()) {
			profiles.add(ProfileManager.get().get(profileId));
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
		String profileId = profile.getString("id");

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_share_item, parent, false) : convertView;

		manager.prepareView(view, profile, ProfileManager.get().profileIdIsActive(profileId), profileId.equals(selectedProfileId));

		return view;
	}

}