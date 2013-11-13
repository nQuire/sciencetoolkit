package org.greengin.sciencetoolkit.ui.components.main.sensorlist.config;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.modelconfig.SettingsFragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SensorSettingsActivity extends SettingsControlledActivity implements ModelNotificationListener, DataLoggerStatusListener {

	String sensorId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sensor_settings);

		sensorId = this.getIntent().getExtras().getString(Arguments.ARG_SENSOR);
		SensorWrapper sensor = SensorWrapperManager.getInstance().getSensor(sensorId);
		if (sensor != null) {
			TextView sensorNameView = (TextView) getWindow().getDecorView().findViewById(R.id.sensor_name);
			sensorNameView.setText(sensor.getName());
			SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.sensor_settings, "sensor:" + sensorId);
			SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.sensor_liveview_settings, "liveview:" + sensorId);
			SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.sensor_liveplot_settings, "liveplot:" + sensorId);
		}

		TextView add = (TextView) getWindow().getDecorView().findViewById(R.id.not_in_profile_notice_add);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!DataLogger.getInstance().isRunning()) {
					showAddToProfileDlg();
				}
			}
		});

	}

	private void showAddToProfileDlg() {
		Model profile = ProfileManager.getInstance().getActiveProfile();
		if (sensorId != null && profile != null) {
			String msg = String.format(getResources().getString(R.string.sensor_not_in_profile_add_msg), sensorId, profile.getString("title"));
			CharSequence styledMsg = Html.fromHtml(msg);
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.sensor_not_in_profile_add_title).setMessage(styledMsg).setPositiveButton(R.string.sensor_not_in_profile_add, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					addToProfile();
				}
			}).setNegativeButton(R.string.cancel, null).show();
		}
	}

	private void addToProfile() {
		Model profile = ProfileManager.getInstance().getActiveProfile();
		ProfileManager.getInstance().addSensor(profile, sensorId);

		if (sensorId != null && profile != null && !DataLogger.getInstance().isRunning()) {
			ProfileManager.getInstance().addSensor(profile, sensorId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sensor_settings, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		updateSensorInProfileNotice();

		ProfileManager.getInstance().registerUIListener(this);
		SettingsManager.getInstance().registerUIListener("profiles", this);
		DataLogger.getInstance().registerStatusListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		ProfileManager.getInstance().unregisterUIListener(this);
		SettingsManager.getInstance().unregisterUIListener("profiles", this);
		DataLogger.getInstance().unregisterStatusListener(this);
	}

	private void updateSensorInProfileNotice() {
		boolean hasSensor = sensorId != null;

		Model profile = ProfileManager.getInstance().getActiveProfile();
		boolean inProfile = hasSensor && profile != null && profile.getModel("sensors", true).getModel(sensorId) != null;

		View root = getWindow().getDecorView();

		root.findViewById(R.id.in_profile_notice).setVisibility(hasSensor && inProfile ? View.VISIBLE : View.GONE);
		root.findViewById(R.id.not_in_profile_notice).setVisibility(hasSensor && !inProfile ? View.VISIBLE : View.GONE);

		if (hasSensor && !inProfile) {
			TextView add = (TextView) root.findViewById(R.id.not_in_profile_notice_add);
			add.setTextColor(DataLogger.getInstance().isRunning() ? getResources().getColor(android.R.color.darker_gray) : getResources().getColor(R.color.value_text));
		}
	}

	@Override
	public void modelNotificationReceived(String msg) {
		String profileId = ProfileManager.getInstance().getActiveProfileId();

		if (profileId != null && ("profiles".equals(msg) || profileId.equals(msg))) {
			updateSensorInProfileNotice();
		}
	}

	@Override
	public void dataLoggerStatusModified() {
		updateSensorInProfileNotice();
	}
}
