package org.greengin.sciencetoolkit.ui.base.modelconfig;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;

public abstract class DataLoggerDependentModelFragment extends CheckEnabledModelFragment implements DataLoggerStatusListener {
	
	
	@Override
	public void onResume() {
		super.onResume();
		DataLogger.get().registerStatusListener(this);
	}
	
	public void onPause() {
		super.onPause();
		DataLogger.get().unregisterStatusListener(this);
	}
	
	@Override
	protected boolean settingsShouldBeEnabled() {
		return DataLogger.get().isRunning() ? settingsEnabledWhileLoggingData() : true;
	}
	
	protected boolean settingsEnabledWhileLoggingData() {
		return true;
	}

	@Override
	public void dataLoggerStatusModified(String msg) {
		checkSettingsEnabled();		
	}
}
