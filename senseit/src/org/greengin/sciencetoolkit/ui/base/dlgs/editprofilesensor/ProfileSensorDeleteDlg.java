package org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor;


import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ProfileSensorDeleteDlg {

	public static void open(Context context, Model profileSensor, ProfileSensorActionListener listener) {
		new ProfileSensorDeleteDlgBuilder(context, profileSensor, listener).launch();
	}

	private static class ProfileSensorDeleteDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {

		String sensorId;
		AlertDialog dlg;
		ProfileSensorActionListener listener;

		public ProfileSensorDeleteDlgBuilder(Context context, Model profileSensor, ProfileSensorActionListener listener) {
			super(context);

			this.listener = listener;
			this.sensorId = profileSensor.getString("sensorid");
			SensorWrapper sensor = SensorWrapperManager.get().getSensor(sensorId);

			setTitle(context.getString(R.string.delete_profile_sensor_dlg_title));
			setMessage(String.format(context.getString(R.string.delete_profile_sensor_dlg_msg), sensor.getName()));

			setPositiveButton(context.getResources().getString(R.string.button_label_delete), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				listener.profileSensorRateDelete(sensorId);
			}
			dlg.dismiss();
		}

	}

}
