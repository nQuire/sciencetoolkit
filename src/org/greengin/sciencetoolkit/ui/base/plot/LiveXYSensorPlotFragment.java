package org.greengin.sciencetoolkit.ui.base.plot;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.SettingsManager;

public class LiveXYSensorPlotFragment extends AbstractXYSensorPlotFragment implements SensorBrowserListener {

	DataPipe dataPipe;
	FixedRateDataFilter periodFilter;

	@Override
	protected String getDomainLabel() {
		return "Seconds ago";
	}

	@Override
	protected void destroyPlot() {
		if (dataPipe != null) {
			dataPipe.detach();
			dataPipe = null;
			series.removeFromPlot(plot);
		}
		super.destroyPlot();
	}

	public void openPlot(String sensorId) {
		super.openPlot();

		this.sensorId = sensorId;
		this.sensor = SensorWrapperManager.get().getSensor(sensorId);
		this.seriesSettings = SettingsManager.get().get("liveplot:" + sensorId);
		this.series = new LiveXYSensorSeriesWrapper(plot, this.sensor, seriesSettings, getActivity());

		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter = new FixedRateDataFilter(period);
		this.dataPipe = new DataPipe(sensor);
		this.dataPipe.addFilter(this.periodFilter);
		this.dataPipe.setEnd((LiveXYSensorSeriesWrapper) this.series);
		this.dataPipe.attach();

		this.series.initSeries(plot);
		
		this.sensorBrowser.setSensors(this, SensorWrapperManager.get().getShownSensorIds(), sensorId);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}


	public void updatePlot() {
		//this.updateSeriesConfig(false);
		((LiveXYSensorSeriesWrapper) this.series).updateViewPeriod();

		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter.setPeriod(period);
	}

	@Override
	public void sensorBrowserSelected(String sensorId) {
		destroyPlot();
		openPlot(sensorId);
	}

}