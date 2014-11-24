package org.greengin.sciencetoolkit.ui.base.dlgs.sensorinfo;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SensorInfoDlg {

	public static void open(Context context, SensorWrapper sensor) {
		new SensorInfoDlgBuilder(context, sensor).launch();
	}

	private static class SensorInfoDlgBuilder extends AlertDialog.Builder
			implements DialogInterface.OnClickListener {

		TextView info;
		AlertDialog dlg;

		public SensorInfoDlgBuilder(Context context, SensorWrapper sensor) {
			super(context);

			View layout = LayoutInflater.from(context).inflate(
					R.layout.dlg_sensor_info, null);
			info = (TextView) layout.findViewById(R.id.sensor_info);
			info.setText(Html.fromHtml(context.getString(SensorUIData
					.getSensorHelpResource(sensor.getType()))));

			setTitle(sensor.getName());
			setIcon(SensorUIData.getSensorSmallIconResource(sensor.getType()));
			setView(layout);
			setNeutralButton(context.getResources().getString(R.string.button_label_close), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dlg.dismiss();
		}
	}
}
