package org.greengin.sciencetoolkit.ui.plotting;


import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;

public class ProfileDataXYSensorPlotFragment extends AbstractXYSensorPlotFragment {

	
	String profileId;
	String settingsId;
	
	ModelNotificationListener notificationListener;
	
	@Override
	void fetchSettings() {
		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.settingsId = "profile_data_visualization:" + this.profileId;
		this.seriesSettings = SettingsManager.get().get(settingsId);
		this.seriesSettingPrefix = "show:" + sensorId + ":";
	}

	@Override
	void createSeries() {
		//this.series = new ProfileDataXYSensorSeriesWrapper(this.sensor, seriesSettings, seriesSettingPrefix, getCursor());

		this.notificationListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReceived(String msg) {
				updatePlot();
			}
		};
	}

	@Override
	public void onDetach() {
		super.onDetach();
		((ProfileDataXYSensorSeriesWrapper)this.series).setCursor(null);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SettingsManager.get().registerUIListener(this.settingsId, this.notificationListener);
		updatePlot();
	}

	@Override
	public void onPause() {
		super.onPause();
		SettingsManager.get().unregisterUIListener(this.settingsId, this.notificationListener);
	}



	public void updatePlot() {
		this.updateSeriesConfig(false);
		//((ProfileDataXYSensorSeriesWrapper)this.series).setCursor(getCursor());
		plot.redraw();
	}
	

}