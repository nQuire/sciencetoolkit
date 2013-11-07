package org.greengin.sciencetoolkit.ui.modelconfig.profile;

import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.modelconfig.DataLoggerDependentModelFragment;
import org.greengin.sciencetoolkit.ui.modelconfig.ProfileModelFragmentManager;


public abstract class AbstractProfileConfigFragment extends DataLoggerDependentModelFragment {

	String[] arguments;
	String type;
	String profileId;
	Model profile;
	
	@Override
	protected Model fetchModel() {
		arguments = getArguments().getStringArray(ProfileModelFragmentManager.ARGS);
		type = arguments[0];
		profileId = arguments[1];
		profile = ProfileManager.getInstance().get(profileId);
		return fetchProfileConfigModel();
	}
	
	protected abstract Model fetchProfileConfigModel();
	
	@Override
	protected boolean settingsEnabledWhileLoggingData() {
		return !profileId.equals(ProfileManager.getInstance().getActiveProfileId());
	}
}
