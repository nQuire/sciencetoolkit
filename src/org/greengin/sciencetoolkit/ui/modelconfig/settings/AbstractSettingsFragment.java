package org.greengin.sciencetoolkit.ui.modelconfig.settings;

import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.ui.modelconfig.DataLoggerDependentModelFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;


public abstract class AbstractSettingsFragment extends DataLoggerDependentModelFragment {

	
	@Override
	public Model fetchModel() {
		return SettingsManager.getInstance().get(getArguments().getString(SettingsFragmentManager.ARG_SETTINGS));
	}
}
