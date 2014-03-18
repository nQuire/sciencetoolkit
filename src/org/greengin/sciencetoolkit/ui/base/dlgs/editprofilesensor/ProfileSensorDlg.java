package org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor;

import java.util.ArrayList;
import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class ProfileSensorDlg {

	public static void open(Context context, Model profile, Model profileSensor, double defaultRate, int defaultUnits, ProfileSensorActionListener listener) {
		new ProfileSensorDlgBuilder(context, profile, profileSensor, defaultRate, defaultUnits, listener).launch();
	}

	private static class ProfileSensorDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener, TextWatcher {
		
		Model profileSensor;
		boolean isRemote;
		String profileSensorId;
		EditText editRate;
		Spinner editUnits;
		Spinner sensorSelect;
		List<SensorWrapper> sensorOptions;
		AlertDialog dlg;
		ProfileSensorActionListener listener;
		Context context;

		public ProfileSensorDlgBuilder(Context context, Model profile, Model profileSensor, double defaultRate, int defaultUnits, ProfileSensorActionListener listener) {
			super(context);
			
			this.context = context;
			this.listener = listener;
			this.profileSensor = profileSensor;
			this.profileSensorId = profileSensor.getString("id");

			SensorWrapper sensor = SensorWrapperManager.get().getSensor(profileSensor.getString("sensorid"));

			if (sensor != null) {
				setTitle(sensor.getName());
			} else {
				setTitle(profileSensor.getString("sensor_type"));
			}

			isRemote = profile.getBool("is_remote");
			if (isRemote) {
				View layout = LayoutInflater.from(context).inflate(R.layout.dlg_sensor_for_type, null);
				sensorSelect = (Spinner) layout.findViewById(R.id.sensor_select);
				sensorOptions = SensorWrapperManager.get().getSensorsOfType(profileSensor.getString("sensor_type"));
				int currentSelection = sensorOptions.indexOf(sensor) + 1;
				ArrayList<String> sensorNames = new ArrayList<String>();
				sensorNames.add(context.getResources().getString(R.string.no_sensor_selected));
				for (SensorWrapper sw : sensorOptions) {
					sensorNames.add(sw.getName());
				}
				ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, sensorNames);
				sensorSelect.setAdapter(spinnerArrayAdapter);
				sensorSelect.setSelection(currentSelection);
				
				setView(layout);
				setPositiveButton(context.getResources().getString(R.string.sample_rate_ok), this);
				setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
			} else {
				View layout = LayoutInflater.from(context).inflate(R.layout.dlg_sample_rate, null);

				editRate = (EditText) layout.findViewById(R.id.sampling_rate);
				editRate.setText(String.valueOf(defaultRate));
				editUnits = (Spinner) layout.findViewById(R.id.sampling_units);
				editUnits.setSelection(defaultUnits);

				setView(layout);
				setPositiveButton(context.getResources().getString(R.string.sample_rate_ok), this);
				setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
				setNegativeButton(context.getResources().getString(R.string.sample_rate_delete), this);
			}

		}

		public void launch() {
			dlg = this.show();
			updatePositiveButton();
		}

		private void updatePositiveButton() {
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				if (isRemote) {

				} else {
					try {
						listener.profileSensorRateEditComplete(true, profileSensorId, Double.parseDouble(editRate.getText().toString()), editUnits.getSelectedItemPosition());
					} catch (NumberFormatException e) {
						listener.profileSensorRateEditComplete(false, null, 0, 0);
					}
				}
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				ProfileSensorDeleteDlg.open(context, profileSensor, listener);
			}
			dlg.dismiss();
		}

		@Override
		public void afterTextChanged(Editable s) {
			updatePositiveButton();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

	}

}
