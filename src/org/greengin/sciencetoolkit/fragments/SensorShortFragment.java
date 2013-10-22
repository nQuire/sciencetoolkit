package org.greengin.sciencetoolkit.fragments;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.sensors.SensorWrapperListener;
import org.greengin.sciencetoolkit.sensors.SensorWrapperManager;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SensorShortFragment extends Fragment implements SensorWrapperListener {
	public static final String ARG_SENSOR = "sensor";

	private String sensorId;
	private SensorWrapper sensor;

	@SuppressWarnings("deprecation")
	public static int getSensorToggleResource(int type) {
		switch (type ) {
		case Sensor.TYPE_ACCELEROMETER:
		case Sensor.TYPE_LINEAR_ACCELERATION:
		case Sensor.TYPE_SIGNIFICANT_MOTION:
			return R.drawable.acceleration_selector;		
		case Sensor.TYPE_AMBIENT_TEMPERATURE:
		case Sensor.TYPE_TEMPERATURE:
			return R.drawable.temperature_selector;
		case Sensor.TYPE_GAME_ROTATION_VECTOR:
		case Sensor.TYPE_ROTATION_VECTOR:
			return R.drawable.rotate_selector;
		case Sensor.TYPE_GRAVITY:
			return R.drawable.gravity_selector;
		case Sensor.TYPE_GYROSCOPE:
		case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
			return R.drawable.gyroscope_selector;
		case Sensor.TYPE_LIGHT:
			return R.drawable.light_selector;
		case Sensor.TYPE_MAGNETIC_FIELD:
		case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
			return R.drawable.magnet_selector;
		case Sensor.TYPE_ORIENTATION:
			return R.drawable.compass_selector;
		case Sensor.TYPE_PRESSURE:
			return R.drawable.pressure_selector;
		case Sensor.TYPE_PROXIMITY:
			return R.drawable.proximity_selector;
		case Sensor.TYPE_RELATIVE_HUMIDITY:
			return R.drawable.humidity_selector;
		case SensorWrapperManager.CUSTOM_SENSOR_TYPE_SOUND:
			return R.drawable.sound_selector;
			default:
				return 0;
		}
	}

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

		View rootView = inflater.inflate(R.layout.fragment_short_sensor, container, false);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.sensor_name);
		nameTextView.setText(this.sensor.getName());
		nameTextView.setTag(this.sensorId);

		ToggleButton toggleButton = (ToggleButton) rootView.findViewById(R.id.sensor_enable);
		toggleButton.setBackgroundDrawable(this.getResources().getDrawable(SensorShortFragment.getSensorToggleResource(sensor.getType())));
		toggleButton.setChecked(this.sensor.isEnabled());
		toggleButton.setTag(this.sensorId);

		TextView labelTextView = (TextView) rootView.findViewById(R.id.sensor_value_label);
		StringBuffer labelstr = new StringBuffer();
		String[] labels = this.sensor.getValueLabels();
		for (int i = 0; i < labels.length; i++) {
			labelstr.append(labels[i]).append(":");
			if (i < labels.length - 1) {
				labelstr.append("\n");
			}
		}
		labelTextView.setText(labelstr);

		this.updateView(rootView);

		return rootView;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.sensor.removeListener(this);
	}

	private void updateView(View view) {
		((ToggleButton) view.findViewById(R.id.sensor_enable)).setChecked(sensor.isEnabled());
		view.findViewById(R.id.sensor_value_section).setVisibility(this.sensor.isEnabled() ? View.VISIBLE : View.GONE);

		this.updateValueView(view);
	}

	private void updateValueView(View view) {
		if (this.sensor.isEnabled()) {
			((TextView) view.findViewById(R.id.sensor_value)).setText(this.sensor.getValueStr());
		}
	}

	@Override
	public void sensorStateUpdated() {
		View rootView = this.getView();
		this.updateView(rootView);
	}

	@Override
	public void sensorValueUpdated() {
		View rootView = this.getView();
		this.updateValueView(rootView);
	}

	@Override
	public void sensorMonitorUpdated() {
	}

}
