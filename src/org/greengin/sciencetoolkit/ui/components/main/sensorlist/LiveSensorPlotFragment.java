package org.greengin.sciencetoolkit.ui.components.main.sensorlist;

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
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.NotificationListener;
import org.greengin.sciencetoolkit.ui.datafilters.SensorLiveXYSeries;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LiveSensorPlotFragment extends Fragment {
	public static final String ARG_SENSOR = "sensor";

	String sensorId;
	SensorWrapper sensor;
	DataPipe dataPipe;
	FixedRateDataFilter periodFilter;
	SensorLiveXYSeries series;
	XYPlot plot;
	String filter;
	BroadcastReceiver seriesReceiver;
	NotificationListener notificationListener;

	Model settings;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);

		this.filter = "liveplot:" + this.sensorId;

		this.settings = SettingsManager.getInstance().get(this.filter);

		this.series = new SensorLiveXYSeries(this.sensor, getActivity(), filter);
		this.plot = null;

		this.periodFilter = new FixedRateDataFilter(settings.getInt("period", ModelDefaults.LIVEPLOT_PERIOD));
		this.dataPipe = new DataPipe(sensor);
		this.dataPipe.append(this.periodFilter);
		this.dataPipe.setEnd(this.series);

		this.seriesReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				eventSeriesUpdated();
			}
		};

		this.notificationListener = new NotificationListener() {
			@Override
			public void notificationReveiced(String msg) {
				updatePlotConfig();
			}
		};
		SettingsManager.getInstance().registerDirectListener(this.filter, this.notificationListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_plot_sensor_live, container, false);

		plot = (XYPlot) rootView;

		plot.setRangeLowerBoundary(0, BoundaryMode.AUTO);
		plot.setRangeUpperBoundary(0, BoundaryMode.AUTO);

		plot.setTicksPerRangeLabel(3);
		plot.setDomainStep(XYStepMode.SUBDIVIDE, 5);
		plot.setDomainValueFormat(new LiveTimePlotDomainFormat());

		updatePlotConfig();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.seriesReceiver, new IntentFilter(filter));
		this.dataPipe.attach();

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		this.dataPipe.detach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.seriesReceiver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		SettingsManager.getInstance().unregisterDirectListener(this.filter, this.notificationListener);
	}

	public void eventSeriesUpdated() {
		if (plot != null) {
			plot.redraw();
		}
	}

	public void updatePlotConfig() {
		this.periodFilter.setPeriod(settings.getInt("period", ModelDefaults.LIVEPLOT_PERIOD));
		this.series.updateConfiguration();
		this.series.updateSeries(plot);
	}

	private class LiveTimePlotDomainFormat extends NumberFormat {

		private static final long serialVersionUID = 2941418063711671809L;

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