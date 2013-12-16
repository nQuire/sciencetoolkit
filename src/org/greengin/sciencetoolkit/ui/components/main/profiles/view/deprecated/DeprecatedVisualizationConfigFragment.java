package org.greengin.sciencetoolkit.ui.components.main.profiles.view.deprecated;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

public class DeprecatedVisualizationConfigFragment extends Fragment {

	String profileId;
	String settingsId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.settingsId = "profile_data_visualization:" + this.profileId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_visualization_config, container, false);

		SettingsFragmentManager.insert(getChildFragmentManager(), R.id.visualization_type_settings, "profile_data_visualization", settingsId);
		SettingsFragmentManager.insert(getChildFragmentManager(), R.id.visualization_range_settings, "profile_data_range", settingsId);
		SettingsFragmentManager.insert(getChildFragmentManager(), R.id.visualization_variables_settings, "profile_data_variables", settingsId);

		return rootView;
	}

}
