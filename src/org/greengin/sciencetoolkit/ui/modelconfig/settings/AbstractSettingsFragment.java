package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.modelconfig.DataLoggerDependentModelFragment;


public abstract class AbstractSettingsFragment extends DataLoggerDependentModelFragment {

	
	@Override
	public Model fetchModel() {
		return SettingsManager.get().get(getArguments().getString(Arguments.ARG_SETTINGS));
	}
}
