package org.greengin.sciencetoolkit.ui.components.main.profiles.edit;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.components.main.datalogging.ProfileSensorFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class ProfileSensorOrganizeFragment extends ProfileSensorFragment implements DataLoggerStatusListener {

	
	ImageButton upButton;
	ImageButton downButton;
	ImageButton discardButton;

	
	@Override
	protected void prepareView(View rootView) {
		super.prepareView(rootView);
		
		Vector<Model> sensors = this.profile.getModel("sensors", true).getModels("weight");
		int index = sensors.indexOf(this.profileSensor);

		upButton = (ImageButton) rootView.findViewById(R.id.sensor_config_up);
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

		downButton = (ImageButton) rootView.findViewById(R.id.sensor_config_down);
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

		discardButton = (ImageButton) rootView.findViewById(R.id.sensor_config_discard);
		discardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sensor != null) {
					String removeMsg = String.format(getResources().getString(R.string.remove_sensor_dlg_msg), sensor.getName());
					CharSequence styledRemoveMsg = Html.fromHtml(removeMsg);
					new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.remove_sensor_dlg_title).setMessage(styledRemoveMsg).setPositiveButton(R.string.remove_sensor_dlg_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ProfileManager.get().removeSensor(profile, profileSensorId);
						}
					}).setNegativeButton(R.string.cancel, null).show();
				}
			}
		});

		updateButtons();
	}

	@Override
	protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
		View rootView = inflater.inflate(R.layout.fragment_profile_sensor_organize, container, false);
		return rootView;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		updateButtons();
		DataLogger.get().registerStatusListener(this);
	}
	
	public void onPause() {
		super.onResume();
		DataLogger.get().unregisterStatusListener(this);
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

	private void updateButtons() {
		boolean enabled = !DataLogger.get().isRunning();
		upButton.setEnabled(enabled);
		downButton.setEnabled(enabled);
		discardButton.setEnabled(enabled);
	}

	@Override
	public void dataLoggerStatusModified() {
		updateButtons();
	}

}
