package org.greengin.sciencetoolkit.ui.plotting;


import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class LiveXYSensorPlotFragment extends AbstractXYSensorPlotFragment {

	DataPipe dataPipe;
	FixedRateDataFilter periodFilter;
	
	String filter;
	BroadcastReceiver seriesReceiver;
	ModelNotificationListener notificationListener;

	

	@Override
	void fetchSettings() {
		this.filter = "liveplot:" + this.sensorId;
		this.seriesSettings = SettingsManager.getInstance().get(this.filter);
		this.seriesSettingPrefix = "show:";
	}

	@Override
	void createSeries() {
		this.series = new LiveXYSensorSeriesWrapper(this.sensor, seriesSettings, seriesSettingPrefix, getActivity(), filter);
		
		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter = new FixedRateDataFilter(period);
		this.dataPipe = new DataPipe(sensor);
		this.dataPipe.addFilter(this.periodFilter);
		this.dataPipe.setEnd((LiveXYSensorSeriesWrapper)this.series);
		
		this.seriesReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				eventSeriesUpdated();
			}
		};

		this.notificationListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReceived(String msg) {
				updatePlot();
			}
		};
	}

	
	@Override
	public void onResume() {
		super.onResume();
		SettingsManager.getInstance().registerDirectListener(this.filter, this.notificationListener);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.seriesReceiver, new IntentFilter(filter));
		this.dataPipe.attach();
		plot.redraw();
		updatePlot();
	}

	@Override
	public void onPause() {
		super.onPause();
		this.dataPipe.detach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.seriesReceiver);
		SettingsManager.getInstance().unregisterDirectListener(this.filter, this.notificationListener);
	}


	public void eventSeriesUpdated() {
		if (plot != null) {
			plot.redraw();
		}
	}

	public void updatePlot() {
		this.updateSeriesConfig(false);
		((LiveXYSensorSeriesWrapper)this.series).updateViewPeriod();
		
		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter.setPeriod(period);
	}

}