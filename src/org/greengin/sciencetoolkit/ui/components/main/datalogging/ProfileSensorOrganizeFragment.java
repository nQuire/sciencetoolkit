package org.greengin.sciencetoolkit.ui.components.main.datalogging;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.SensorUIData;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ProfileSensorOrganizeFragment extends Fragment {
	public static final String ARG_SENSOR = "sensor";

	private int sensorType;
	private SensorWrapper sensor;
	private String profileSensorId;
	private Model profileSensor;
	private String profileId;
	private Model profile;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.profileId = getArguments().getString("profile");
		this.profileSensorId = getArguments().getString("sensor");

		this.profile = ProfileManager.getInstance().get(this.profileId);
		this.profileSensor = this.profile == null ? null : this.profile.getModel("sensors", true).getModel(this.profileSensorId);

		if (this.profileSensor == null) {
			this.sensor = null;
			this.sensorType = -1;
		} else {
			this.sensor = SensorWrapperManager.getInstance().getSensor(this.profileSensor.getString("id", null));
			if (this.sensor != null) {
				this.sensorType = this.sensor.getType();
			} else {
				this.sensorType = this.profileSensor.getInt("sensor_type", -1);
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_profile_sensor_organize, container, false);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.sensor_name);
		nameTextView.setText(this.sensor != null ? this.sensor.getName() : "no sensor");

		ToggleButton toggleButton = (ToggleButton) rootView.findViewById(R.id.sensor_value_toggle);
		toggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
			}
		});

		toggleButton.setBackgroundDrawable(this.getResources().getDrawable(SensorUIData.getSensorToggleResource(this.sensorType)));

		ImageButton editButton = (ImageButton) rootView.findViewById(R.id.sensor_config_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		Vector<Model> sensors = this.profile.getModel("sensors", true).getModels("weight");
		int index = sensors.indexOf(this.profileSensor);

		ImageButton upButton = (ImageButton) rootView.findViewById(R.id.sensor_config_up);
		if (index > 0) {
			upButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					moveSensorUp();
				}
			});
		} else {
			upButton.setEnabled(false);
			upButton.setVisibility(View.GONE);
		}

		ImageButton downButton = (ImageButton) rootView.findViewById(R.id.sensor_config_down);
		if (index >= 0 && index < sensors.size() - 1) {
			downButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					moveSensorDown();
				}
			});
		} else {
			downButton.setEnabled(false);
			downButton.setVisibility(View.GONE);
		}

		return rootView;
	}

	private void moveSensorUp() {
		Vector<Model> sensors = this.profile.getModel("sensors", true).getModels("weight");
		int index = sensors.indexOf(this.profileSensor);
		if (index > 0) {
			swapSensor(sensors, index, index - 1);
		}
	}

	private void moveSensorDown() {
		Vector<Model> sensors = this.profile.getModel("sensors", true).getModels("weight");
		int index = sensors.indexOf(this.profileSensor);
		if (index >= 0 && index < sensors.size() - 1) {
			swapSensor(sensors, index, index + 1);
		}
	}

	private void swapSensor(Vector<Model> sensors, int from, int to) {
		Model swap = sensors.get(to);
		sensors.set(to, this.profileSensor);
		sensors.set(from, swap);
		for (int i = 0; i < sensors.size(); i++) {
			sensors.get(i).setInt("weight", i, true);
		}
		profile.fireModifiedEvent();
	}

}
