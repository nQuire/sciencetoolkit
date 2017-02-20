package org.greengin.sciencetoolkit.common.ui.base.modelconfig.settings;

import java.util.Arrays;
import java.util.List;

import org.greengin.sciencetoolkit.common.model.ModelDefaults;

import android.view.View;

public class AppSettingsFragment extends AbstractSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		List<String> options = Arrays.asList("Portrait", "Landscape", "Rotate with device");
		addOptionSelect("screen_orientation", "Screen orientation", "Select the screen orientation", options, ModelDefaults.APP_SCREEN_ORIENTATION);
	}

}
