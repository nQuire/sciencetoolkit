package org.greengin.sciencetoolkit.ui.fragments.tab1;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataTube;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LiveSensorPlotFragment extends Fragment {
	public static final String ARG_SENSOR = "sensor";

	String sensorId;
	SensorWrapper sensor;
	DataTube dataTube;
	SensorLiveXYSeries series;
	XYPlot plot;
	BroadcastReceiver seriesReceiver;
	String filter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);

		this.filter = "liveplot:" + this.sensorId;

		this.series = new SensorLiveXYSeries(this.sensor, getActivity(), filter);
		this.plot = null;

		this.dataTube = new DataTube(sensor);
		this.dataTube.append(new FixedRateDataFilter(250));
		this.dataTube.setEnd(this.series);

		this.seriesReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				eventSeriesUpdated();
			}
		};
		
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
		
		updateSeriesConfig();

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.seriesReceiver, new IntentFilter(filter));
		this.dataTube.attach();

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		Log.d("stk plot", "destroy");

		this.dataTube.detach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.seriesReceiver);
	}

	
	public void eventSeriesUpdated() {
		if (plot != null) {
			Log.d("stk plot", "redraw");
			plot.redraw();
		}
	}
	
	public void updateSeriesConfig() {
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