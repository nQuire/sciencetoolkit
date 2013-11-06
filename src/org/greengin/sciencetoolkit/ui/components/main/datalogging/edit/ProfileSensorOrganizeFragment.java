package org.greengin.sciencetoolkit.ui.components.main.datalogging.edit;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.SensorUIData;
import org.greengin.sciencetoolkit.ui.components.main.datalogging.config.ProfileSensorSettingsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

		TextView periodTextView = (TextView) rootView.findViewById(R.id.sensor_period);
		periodTextView.setText(Integer.toString(this.profileSensor.getInt("period", ModelDefaults.DATA_LOGGING_PERIOD)) + " ms");

		ImageView image = (ImageView) rootView.findViewById(R.id.sensor_image);
		image.setImageResource(SensorUIData.getSensorResource(this.sensorType));

		ImageButton editButton = (ImageButton) rootView.findViewById(R.id.sensor_config_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ProfileSensorSettingsActivity.class);
				intent.putExtra("profile", profileId);
				intent.putExtra("sensor", profileSensorId);
				startActivity(intent);
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

		ImageButton discardButton = (ImageButton) rootView.findViewById(R.id.sensor_config_discard);
		discardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sensor != null) {
					String removeMsg = String.format(getResources().getString(R.string.remove_sensor_dlg_msg), sensor.getName());
					CharSequence styledRemoveMsg = Html.fromHtml(removeMsg);
					new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.remove_sensor_dlg_title).setMessage(styledRemoveMsg).setPositiveButton(R.string.remove_sensor_dlg_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ProfileManager.getInstance().removeSensor(profile, profileSensorId);
						}
					}).setNegativeButton(R.string.cancel, null).show();
				}
			}
		});

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
