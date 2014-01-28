package org.greengin.sciencetoolkit.ui.base.plot;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
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
import android.view.ScaleGestureDetector;

public class LiveXYSensorPlotFragment extends AbstractXYSensorPlotFragment {

	

	DataPipe dataPipe;
	FixedRateDataFilter periodFilter;

	BroadcastReceiver seriesReceiver;
	ModelNotificationListener notificationListener;

	public LiveXYSensorPlotFragment() {
		this.seriesReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				eventSeriesUpdated();
			}
		};
	}

	@Override
	protected String getDomainLabel() {
		return "Seconds ago";
	}

	public void close() {
		stopCurrentLivePlot();
		super.close();
	}

	private void stopCurrentLivePlot() {
		if (sensorId != null) {
			this.sensorId = null;
			this.dataPipe.detach();
			this.dataPipe = null;
			this.sensor = null;
			this.series.removeFromPlot(plot);
			this.series = null;
			stopListeningToLiveValues();
		}
	}

	private void listenToLiveValues() {
		if (this.sensorId != null) {
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.seriesReceiver, new IntentFilter(LiveXYSensorSeriesWrapper.EVENT_FILTER));
			this.dataPipe.attach();
			plot.redraw();
			updatePlot();
		}
	}

	private void stopListeningToLiveValues() {
		SettingsManager.get().unregisterDirectListener(LiveXYSensorSeriesWrapper.EVENT_FILTER, this.notificationListener);
	}

	public void open(String sensorId) {
		stopCurrentLivePlot();

		this.sensorId = sensorId;
		this.sensor = SensorWrapperManager.get().getSensor(sensorId);
		this.seriesSettings = SettingsManager.get().get("liveplot:" + sensorId);
		this.series = new LiveXYSensorSeriesWrapper(this.sensor, seriesSettings, getActivity());

		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter = new FixedRateDataFilter(period);
		this.dataPipe = new DataPipe(sensor);
		this.dataPipe.addFilter(this.periodFilter);
		this.dataPipe.setEnd((LiveXYSensorSeriesWrapper) this.series);
		
		this.series.initSeries(plot);
		plot.removeMarkers();
		
		listenToLiveValues();
		
		super.open();
	}

	@Override
	public void onResume() {
		super.onResume();
		listenToLiveValues();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopCurrentLivePlot();
	}

	public void eventSeriesUpdated() {
		if (plot != null) {
			plot.redraw();
		}
	}

	public void updatePlot() {
		this.updateSeriesConfig(false);
		((LiveXYSensorSeriesWrapper) this.series).updateViewPeriod();

		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter.setPeriod(period);
	}

	

}