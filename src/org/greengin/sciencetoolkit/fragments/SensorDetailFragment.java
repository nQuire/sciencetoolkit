package org.greengin.sciencetoolkit.fragments;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.SensorWrapperManager;
import org.greengin.sciencetoolkit.SettingsManager;
import org.greengin.sciencetoolkit.plots.SensorLiveXYSeries;
import org.greengin.sciencetoolkit.plots.SensorLiveXYSeriesListener;
import org.greengin.sciencetoolkit.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.sensors.SensorWrapperListener;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SensorDetailFragment extends Fragment implements SensorWrapperListener, SensorLiveXYSeriesListener {
	public static final String ARG_SENSOR = "sensor";

	private String sensorId;
	private SensorWrapper sensor;
	private XYPlot plot;
	private SensorLiveXYSeries series;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);
		this.sensor.addListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_detail_sensor, container, false);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.sensor_name);
		nameTextView.setText(this.sensor.getName());

		ToggleButton toggleButton = (ToggleButton) rootView.findViewById(R.id.sensor_enable);
		toggleButton.setBackgroundDrawable(this.getResources().getDrawable(SensorShortFragment.getSensorToggleResource(sensor.getType())));
		toggleButton.setChecked(this.sensor.isEnabled());
		toggleButton.setTag(this.sensorId);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		SettingsFragment settings = new SettingsFragment();
		Bundle args = new Bundle();
		args.putString("settings", sensor.getName());
		settings.setArguments(args);
		fragmentTransaction.replace(R.id.sensor_config_panel, settings);

		fragmentTransaction.commit();

		this.preparePlot(rootView);

		return rootView;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		this.sensor.removeListener(this);
		if (this.series.isRunning()) {
			this.series.stop();
		}
	}

	@Override
	public void sensorStateUpdated() {
		((ToggleButton) getView().findViewById(R.id.sensor_enable)).setChecked(sensor.isEnabled());
		
		if (sensor.isEnabled() && !series.isRunning()) {
			series.start();
		} else if (!sensor.isEnabled() && series.isRunning()) {
			series.stop();
		}
		
		updateSeries();
	}

	@Override
	public void sensorValueUpdated() {
		series.updateCurrentValue();
	}
	
	public void updateSeries() {
		this.series.updateSeries(plot);
	}
	
	

	private void preparePlot(View view) {
		Log.d("stk plot", "create");
		plot = (XYPlot) view.findViewById(R.id.live_xy_plot);

		plot.setRangeLowerBoundary(0, BoundaryMode.AUTO);
		plot.setRangeUpperBoundary(0, BoundaryMode.AUTO);

		plot.setTicksPerRangeLabel(3);
		plot.setDomainStep(XYStepMode.SUBDIVIDE, 5);
		plot.setDomainValueFormat(new LiveTimePlotDomainFormat());

		LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				plot.redraw();
			}
		}, new IntentFilter(SensorLiveXYSeries.SENSOR_LIVE_SERIES_UPDATE));

		String settingsKey = "liveplot:" + sensor.getName();
		Log.d("stk plot series", settingsKey);
		if (!SettingsManager.getInstance().exists(settingsKey)) {
			Log.d("stk plot series", "create");
			SettingsManager.getInstance().registerSettings(settingsKey, new SensorLiveXYSeries(sensor));
		}
		
		this.series = (SensorLiveXYSeries) SettingsManager.getInstance().getSettings(settingsKey);

		if (series.isRunning()) {
			series.stop();
		}
		
		this.series.setContext(this, view.getContext());
		if (sensor.isEnabled()) {
			series.start();
		}
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		SettingsFragment settings = new SettingsFragment();
		Bundle args = new Bundle();
		args.putString("settings", settingsKey);
		settings.setArguments(args);
		fragmentTransaction.replace(R.id.plot_config_panel, settings);

		fragmentTransaction.commit();
		
		this.updateSeries();
	}

	@Override
	public void sensorMonitorUpdated() {
	}
	
	private class LiveTimePlotDomainFormat extends NumberFormat {
		
		private static final long serialVersionUID = 2941418063711671809L;
		
		DecimalFormat df;
		
		public LiveTimePlotDomainFormat() {
			this.df = new DecimalFormat();
		}

		@Override
		public StringBuffer format(double d, StringBuffer sb, FieldPosition fp) {
			return format((long)d, sb, fp);
		}

		@Override
		public StringBuffer format(long l, StringBuffer sb, FieldPosition fp) {
			long dm = Math.max(0, System.currentTimeMillis() - l);
			int ds = (int)Math.log10(Math.max(1, dm));
			
			if (ds < 3) {
				return sb.append(dm).append(" ms");
			} else {
				df.setMaximumFractionDigits(Math.max(0,  ds - 2));
				return sb.append(df.format(dm * .001)).append(" s");
			}
		}

		@Override
		public Number parse(String arg0, ParsePosition arg1) {
			return null;
		}
		

		
	}

	@Override
	public void seriesSettingsUpdated() {
		updateSeries();
	}

}
