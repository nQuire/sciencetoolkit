package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;

public abstract class DataLoggerDependentModelFragment extends ModelFragment implements DataLoggerStatusListener {
	
	
	@Override
	public void onResume() {
		super.onResume();
		checkSettingsEnabled();
		DataLogger.getInstance().registerStatusListener(this);
	}
	
	public void onPause() {
		super.onPause();
		DataLogger.getInstance().unregisterStatusListener(this);
	}
	
	private void checkSettingsEnabled() {
		boolean enabled = DataLogger.getInstance().isRunning() ? settingsEnabledWhileLoggingData() : true;
		setSettingsEnabled(enabled);
	}
	
	protected boolean settingsEnabledWhileLoggingData() {
		return true;
	}

	@Override
	public void dataLoggerStatusModified() {
		checkSettingsEnabled();		
	}
}
