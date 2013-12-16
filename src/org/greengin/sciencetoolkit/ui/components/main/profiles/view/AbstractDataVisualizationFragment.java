package org.greengin.sciencetoolkit.ui.components.main.profiles.view;

import java.util.List;

import org.greengin.sciencetoolkit.logic.datalogging.DeprecatedDataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.ParentListFragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class AbstractDataVisualizationFragment extends ParentListFragment {

	public AbstractDataVisualizationFragment() {
		this(0);
	}

	public AbstractDataVisualizationFragment(int childrenContainerId) {
		super(childrenContainerId);
	}

	protected String profileId;
	protected String settingsId;
	ModelNotificationListener profileListener;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
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
		SettingsManager.i().registerUIListener(settingsId, profileListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		SettingsManager.i().unregisterUIListener(settingsId, profileListener);
	}

	protected Cursor getCursor() {
		Model datarange = SettingsManager.i().get(settingsId);
		long from = datarange.getLong("from", 0);
		long to = datarange.getBool("track_to", true) ? Long.MAX_VALUE : datarange.getLong("to", Long.MAX_VALUE);
		return DeprecatedDataLogger.i().getListViewCursor(profileId, from, to);
	}


	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		return null;
	}

	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return false;
	}

}
