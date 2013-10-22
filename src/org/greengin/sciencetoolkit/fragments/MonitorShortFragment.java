package org.greengin.sciencetoolkit.fragments;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.sensors.SensorWrapperListener;
import org.greengin.sciencetoolkit.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.sensors.SensorWrapperMonitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MonitorShortFragment extends Fragment implements SensorWrapperListener {
	public static final String ARG_SENSOR = "sensor";

	private String sensorId;
	private SensorWrapper sensor;
	private SensorWrapperMonitor monitor;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);
		this.sensor.addListener(this);
		this.monitor = this.sensor.getMonitor();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_short_monitor, container, false);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.sensor_name);
		nameTextView.setText(this.sensor.getName());
		nameTextView.setTag(this.sensorId);

		this.updateView(rootView);

		return rootView;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.sensor.removeListener(this);
	}

	private void updateView(View view) {
		((TextView) view.findViewById(R.id.monitor_status)).setText(this.monitor.getStatusStr());		
	}

	
	@Override
	public void sensorStateUpdated() {
		View rootView = this.getView();
		this.updateView(rootView);
	}

	@Override
	public void sensorValueUpdated() {
	}

	@Override
	public void sensorMonitorUpdated() {
		View rootView = this.getView();
		this.updateView(rootView);
	}

}
