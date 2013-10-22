package uihelpers;

import org.greengin.sciencetoolkit.sensors.SensorWrapper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.ToggleButton;

public class SensorDisableClickListener implements OnClickListener {

	public static void sensorToggled(Context context, SensorWrapper sensor, ToggleButton button) {
		if (sensor != null) {
			if (sensor.isEnabled() && sensor.getMonitor().isEnabled() && !button.isChecked()) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				dialog.setIcon(android.R.drawable.ic_dialog_alert).setTitle("Disabling sensor");
				dialog.setMessage("This sensor is currently recording data. Are you sure you want to disable it?");
				dialog.setPositiveButton("Yes", new SensorDisableClickListener(sensor, true, button));
				dialog.setNegativeButton("No", new SensorDisableClickListener(sensor, false, button));
				dialog.show();
			} else {
				sensor.setEnabled(button.isChecked());
			}
		}		
	}
	
	SensorWrapper sensor;
	boolean disable;
	ToggleButton button;

	public SensorDisableClickListener(SensorWrapper sensor, boolean disable, ToggleButton button) {
		this.sensor = sensor;
		this.disable = disable;
		this.button = button;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (disable) {
			this.sensor.setEnabled(false);
		} else {
			this.button.setChecked(true);
		}
	}
}
