package org.greengin.sciencetoolkit.fragments;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.SensorWrapperManager;
import org.greengin.sciencetoolkit.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.sensors.SensorWrapperListener;
import org.greengin.sciencetoolkit.sensors.SensorWrapperMonitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MonitorDetailFragment extends Fragment implements SensorWrapperListener {
	public static final String ARG_SENSOR = "sensor";

	private String sensorId;
	private SensorWrapper sensor;
	private SensorWrapperMonitor monitor;
	private SettingsFragment settingsPanel;

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

		View rootView = inflater.inflate(R.layout.fragment_detail_monitor, container, false);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.sensor_name);
		nameTextView.setText(this.sensor.getName());
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		settingsPanel = new SettingsFragment();
		Bundle args = new Bundle();
		args.putString("settings", "monitor:" + sensor.getName());
		settingsPanel.setArguments(args);
		fragmentTransaction.replace(R.id.monitor_config_panel, settingsPanel);
		fragmentTransaction.commit();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		View rootView = this.getView();
		this.updateView(rootView);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		this.sensor.removeListener(this);
	}
	
	private void updateView(View view) {
		this.updateButtonView(view);
		this.settingsPanel.enableSettings(!monitor.isEnabled());
	}

	private void updateButtonView(View view) {
		String buttonLabel = null;
		switch(monitor.getStatus()) {
		case SensorWrapperMonitor.MANUAL_STOPPED:
			buttonLabel = "Start logging";
			break;
		case SensorWrapperMonitor.MANUAL_RECORDING:
			buttonLabel = "Stop logging";
			break;
		case SensorWrapperMonitor.SCHEDULED_STOPPED:
			buttonLabel = "Set schedule";
			break;
		case SensorWrapperMonitor.SCHEDULED_WAITING:
			buttonLabel = "Cancel schedule";
			break;
		case SensorWrapperMonitor.SCHEDULED_RECORDING:
			buttonLabel = "Stop recording";
			break;
		}
		((Button) view.findViewById(R.id.monitor_toggle)).setText(buttonLabel);
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
