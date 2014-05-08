package org.greengin.sciencetoolkit.common.ui.base.modelconfig;

public abstract class CheckEnabledModelFragment extends ModelFragment {
	
	
	@Override
	public void onResume() {
		super.onResume();
		checkSettingsEnabled();
	}
	
	protected void checkSettingsEnabled() {
		setSettingsEnabled(settingsShouldBeEnabled());
	}
	
	protected boolean settingsShouldBeEnabled() {
		return true;
	}
	
}
