package org.greengin.sciencetoolkit.ui.base.plot.live;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.ui.base.plot.ClosableXYSensorPlotFragment;
import org.greengin.sciencetoolkit.ui.base.plot.SensorBrowserListener;

public class LiveXYSensorPlotFragment extends ClosableXYSensorPlotFragment implements SensorBrowserListener {
	


	LiveXYSensorDataWrapper series;
	
	DataPipe dataPipe;
	FixedRateDataFilter periodFilter;
	protected Model seriesSettings;

	
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
		
		setTitle(getResources().getString(R.string.plot_live_label));
		
		SensorWrapper sensor = SensorWrapperManager.get().getSensor(sensorId);
		this.seriesSettings = SettingsManager.get().get("liveplot:" + sensorId);
		this.series = new LiveXYSensorDataWrapper(plot, sensor, seriesSettings, getActivity());

		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter = new FixedRateDataFilter(period);
		this.dataPipe = new DataPipe(sensor);
		this.dataPipe.addFilter(this.periodFilter);
		this.dataPipe.setEnd((LiveXYSensorDataWrapper) this.series);
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
		((LiveXYSensorDataWrapper) this.series).updateViewPeriod();

		int period = ModelOperations.rate2period(seriesSettings, "sample_rate", ModelDefaults.LIVEPLOT_SAMPLING_RATE, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MIN, ModelDefaults.LIVEPLOT_SAMPLING_RATE_MAX);
		this.periodFilter.setPeriod(period);
	}

	@Override
	public void sensorBrowserSelected(String sensorId) {
		destroyPlot();
		openPlot(sensorId);
	}

	@Override
	protected NumberFormat createDomainNumberFormat() {
		return new LiveTimePlotDomainFormat();
	}
	
	
	private class LiveTimePlotDomainFormat extends NumberFormat {

		private static final long serialVersionUID = -1726050106365327017L;
		DecimalFormat df;

		public LiveTimePlotDomainFormat() {
			this.df = new DecimalFormat();
		}

		@Override
		public StringBuffer format(double d, StringBuffer sb, FieldPosition fp) {
			return format((long) d, sb, fp);
		}

		@Override
		public StringBuffer format(long l, StringBuffer sb, FieldPosition fp) {
			long dm = Math.max(0, System.currentTimeMillis() - l);
			int ds = (int) Math.log10(Math.max(1, dm));

			if (ds < 3) {
				return sb.append(dm).append(" ms");
			} else {
				df.setMaximumFractionDigits(Math.max(0, ds - 2));
				return sb.append(df.format(dm * .001)).append(" s");
			}
		}

		@Override
		public Number parse(String arg0, ParsePosition arg1) {
			return null;
		}
	}

}