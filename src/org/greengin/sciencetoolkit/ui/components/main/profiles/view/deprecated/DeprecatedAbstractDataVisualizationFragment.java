package org.greengin.sciencetoolkit.ui.components.main.profiles.view.deprecated;

import java.util.List;

import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.ParentListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class DeprecatedAbstractDataVisualizationFragment extends ParentListFragment {

	public DeprecatedAbstractDataVisualizationFragment() {
		this(0);
	}

	public DeprecatedAbstractDataVisualizationFragment(int childrenContainerId) {
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
		SettingsManager.get().registerUIListener(settingsId, profileListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		SettingsManager.get().unregisterUIListener(settingsId, profileListener);
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
