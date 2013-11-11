package org.greengin.sciencetoolkit.ui.components.main.data.view;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class DataViewFragment extends Fragment {

	String profileId;
	ModelNotificationListener profileListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.profileId = getArguments().getString("profile");
		profileListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReveiced(String msg) {
				if (profileId.equals(msg)) {
					updateDataRange();
				}
			}
		};
	}

	protected abstract void updateDataRange();

	@Override
	public void onResume() {
		super.onResume();
		updateDataRange();
		ProfileManager.getInstance().registerDirectListener(profileListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		ProfileManager.getInstance().unregisterDirectListener(profileListener);
	}
	
	
	protected Cursor getCursor() {
		Model datarange = ProfileManager.getInstance().get(profileId).getModel("datarange");
		long from = datarange.getLong("from", 0);
		long to = datarange.getBool("track_to", true) ? Long.MAX_VALUE : datarange.getLong("to", Long.MAX_VALUE);
		return DataLogger.getInstance().getListViewCursor(profileId, from, to);
	}

}
