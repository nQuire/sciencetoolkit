package org.greengin.sciencetoolkit.ui.components.main.profiles.view;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.components.main.profiles.view.visualizations.ListVisualizationFragment;
import org.greengin.sciencetoolkit.ui.components.main.profiles.view.visualizations.PlotVisualizationFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VisualizationContainerFragment extends Fragment implements ModelNotificationListener {

	String profileId;
	String settingsId;
	int visualization;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.settingsId = "profile_data_visualization:" + this.profileId;
		this.visualization = -1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_visualization_container, container, false);

		return rootView;
	}

	private void updateVisualization() {
		if (profileId != null) {
			int v = SettingsManager.getInstance().get(this.settingsId).getInt("visualization", 0);
			if (v != visualization) {
				visualization = v;

				Fragment fragment = null;
				switch (v) {
				case 0:
					fragment = new PlotVisualizationFragment();
					break;
				default:
					fragment = new ListVisualizationFragment();
					break;
				}

				if (fragment != null) {
					Bundle args = new Bundle();
					args.putString(Arguments.ARG_PROFILE, profileId);
					fragment.setArguments(args);

					getChildFragmentManager().beginTransaction().replace(R.id.visualization_container, fragment).commit();
				}
			}
		}
	}

	public void onResume() {
		super.onResume();
		updateVisualization();
		SettingsManager.getInstance().registerUIListener(settingsId, this);
	}

	public void onPause() {
		super.onPause();
		SettingsManager.getInstance().unregisterUIListener(settingsId, this);
	}

	@Override
	public void modelNotificationReceived(String msg) {
		updateVisualization();
	}

}