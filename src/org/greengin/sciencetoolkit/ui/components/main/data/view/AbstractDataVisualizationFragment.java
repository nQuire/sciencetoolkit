package org.greengin.sciencetoolkit.ui.components.main.data.view;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class AbstractDataVisualizationFragment extends Fragment {

	String profileId;
	String settingsId;
	ModelNotificationListener profileListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.profileId = getArguments().getString("profile");
		this.settingsId = "profile_data_visualization:" + profileId;

		profileListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReceived(String msg) {
				updateDataRange();
			}
		};
	}

	protected abstract void updateDataRange();

	@Override
	public void onResume() {
		super.onResume();
		updateDataRange();
		SettingsManager.getInstance().registerUIListener(settingsId, profileListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		SettingsManager.getInstance().unregisterUIListener(settingsId, profileListener);
	}

	protected Cursor getCursor() {
		Model datarange = SettingsManager.getInstance().get(settingsId);
		long from = datarange.getLong("from", 0);
		long to = datarange.getBool("track_to", true) ? Long.MAX_VALUE : datarange.getLong("to", Long.MAX_VALUE);
		return DataLogger.getInstance().getListViewCursor(profileId, from, to);
	}

}
