package org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor;

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
import android.widget.EditText;
import android.widget.Spinner;

public class SamplingRateDlg {

	public static void open(Context context, Model profileSensor, double defaultRate, int defaultUnits, ProfileSensorActionListener listener) {
		new SamplingRateDlgBuilder(context, profileSensor, defaultRate, defaultUnits, listener).launch();
	}

	private static class SamplingRateDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener, TextWatcher {

		String profileSensorId;
		EditText editRate;
		Spinner editUnits;
		AlertDialog dlg;
		ProfileSensorActionListener listener;

		public SamplingRateDlgBuilder(Context context, Model profileSensor, double defaultRate, int defaultUnits, ProfileSensorActionListener listener) {
			super(context);

			this.listener = listener;
			this.profileSensorId = profileSensor.getString("id");
			View layout = LayoutInflater.from(context).inflate(R.layout.dlg_sample_rate, null);

			editRate = (EditText) layout.findViewById(R.id.sampling_rate);
			editRate.setText(String.valueOf(defaultRate));
			editUnits = (Spinner) layout.findViewById(R.id.sampling_units);
			editUnits.setSelection(defaultUnits);

			setView(layout);

			SensorWrapper sensor = SensorWrapperManager.get().getSensor(profileSensor.getString("sensorid"));
			if (sensor != null) {
				setTitle(sensor.getName());
			} else {
				setTitle(profileSensor.getString("sensor_type"));
			}

			//setMessage(context.getResources().getString(R.string.sample_rate_dlg_msg));
			setPositiveButton(context.getResources().getString(R.string.sample_rate_ok), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
			setNegativeButton(context.getResources().getString(R.string.sample_rate_delete), this);
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
				try {
					listener.profileSensorRateEditComplete(true, profileSensorId, Double.parseDouble(editRate.getText().toString()), editUnits.getSelectedItemPosition());
				} catch (NumberFormatException e) {
					listener.profileSensorRateEditComplete(false, null, 0, 0);
				}
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
