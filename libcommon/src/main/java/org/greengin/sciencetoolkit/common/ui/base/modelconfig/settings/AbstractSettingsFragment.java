package org.greengin.sciencetoolkit.common.ui.base.modelconfig.settings;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.common.ui.base.Arguments;
import org.greengin.sciencetoolkit.common.ui.base.modelconfig.CheckEnabledModelFragment;

 

public abstract class AbstractSettingsFragment extends CheckEnabledModelFragment {

	
	@Override
	public Model fetchModel() {
		return SettingsManager.get().get(getArguments().getString(Arguments.ARG_SETTINGS));
	}
}
