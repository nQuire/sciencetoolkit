package org.greengin.sciencetoolkit.ui.modelconfig.settings.dataview;

import java.util.Arrays;
import java.util.List;

import org.greengin.sciencetoolkit.ui.modelconfig.settings.AbstractSettingsFragment;

import android.view.View;

public class ProfileDataVisualizationSettingsFragment extends AbstractSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		List<String> options = Arrays.asList("Plot", "List" /*, "Map" */);
		addOptionSelect("visualization", null, null, options, 0);
	}
}
