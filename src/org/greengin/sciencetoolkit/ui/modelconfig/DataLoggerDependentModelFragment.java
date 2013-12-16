package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.logic.datalogging.DeprecatedDataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;

public abstract class DataLoggerDependentModelFragment extends CheckEnabledModelFragment implements DataLoggerStatusListener {
	
	
	@Override
	public void onResume() {
		super.onResume();
		DeprecatedDataLogger.i().registerStatusListener(this);
	}
	
	public void onPause() {
		super.onPause();
		DeprecatedDataLogger.i().unregisterStatusListener(this);
	}
	
	@Override
	protected boolean settingsShouldBeEnabled() {
		return DeprecatedDataLogger.i().isRunning() ? settingsEnabledWhileLoggingData() : true;
	}
	
	protected boolean settingsEnabledWhileLoggingData() {
		return true;
	}

	@Override
	public void dataLoggerStatusModified() {
		checkSettingsEnabled();		
	}
}
