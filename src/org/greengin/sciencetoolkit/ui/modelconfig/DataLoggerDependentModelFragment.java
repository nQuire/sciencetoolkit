package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;

public abstract class DataLoggerDependentModelFragment extends CheckEnabledModelFragment implements DataLoggerStatusListener {
	
	
	@Override
	public void onResume() {
		super.onResume();
		DataLogger.getInstance().registerStatusListener(this);
	}
	
	public void onPause() {
		super.onPause();
		DataLogger.getInstance().unregisterStatusListener(this);
	}
	
	@Override
	protected boolean settingsShouldBeEnabled() {
		return DataLogger.getInstance().isRunning() ? settingsEnabledWhileLoggingData() : true;
	}
	
	protected boolean settingsEnabledWhileLoggingData() {
		return true;
	}

	@Override
	public void dataLoggerStatusModified() {
		checkSettingsEnabled();		
	}
}
