package org.greengin.sciencetoolkit.ui.main.record;

import java.io.File;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.remote.RemoteCapableActivity;
import org.greengin.sciencetoolkit.logic.remote.UploadRemoteAction;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.animations.Animations;
import org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor.ProfileSensorActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor.ProfileSensorDlg;
import org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect.SelectSensorActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect.SensorSelectDlg;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;
import org.greengin.sciencetoolkit.ui.base.plot.record.RecordXYSensorPlotFragment;
import org.greengin.sciencetoolkit.ui.base.widgets.BlinkingImageView;
import org.greengin.sciencetoolkit.ui.main.share.ProjectItemManager;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RecordFragment extends EventFragment implements OnClickListener, SelectSensorActionListener, ProfileSensorActionListener, RecordSensorListener {

	private enum RecordState {
		IDLE, RECORDING, DECIDING
	};

	RecordXYSensorPlotFragment plotFragment;

	RecordSensorListAdapter adapter;

	RecordState state;
	File currentSeries;
	boolean canUploadSeries;

	BlinkingImageView recordingIcon;

	ImageButton buttonAdd;

	ImageButton buttonStart;
	ImageButton buttonStop;
	//ImageButton buttonView;
	ImageButton buttonDiscard;
	//ImageButton buttonShare;
	//ImageButton buttonUpload;
	ImageButton buttonKeep;

	//View buttonUploadContainer;

	LinearLayout recordingPanel;
	LinearLayout seriesPanel;
	
	TextView recordingLabel;
	TextView recordedLabel;

	View projectTitlePanel;
	TextView projectTitleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.state = DataLogger.get().isRunning() ? RecordState.RECORDING : RecordState.IDLE;
		this.currentSeries = null;

		eventManager.setListener(new EventListener());

		eventManager.listenToSettings("profiles");
		eventManager.listenToLoggerStatus();
		eventManager.listenToLoggedData();
		eventManager.listenToProfiles();

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.view_record, container, false);

		plotFragment = new RecordXYSensorPlotFragment();
		getChildFragmentManager().beginTransaction().add(R.id.record_content_panel, plotFragment).addToBackStack(null).commit();

		adapter = new RecordSensorListAdapter(this, inflater);
		ListView list = (ListView) rootView.findViewById(R.id.sensor_list);
		list.setAdapter(adapter);

		recordingIcon = (BlinkingImageView) rootView.findViewById(R.id.recording_icon);
		recordingLabel = (TextView) rootView.findViewById(R.id.recording_label);
		recordedLabel = (TextView) rootView.findViewById(R.id.recorded_label);

		buttonAdd = (ImageButton) rootView.findViewById(R.id.record_sensor_add);
		buttonAdd.setOnClickListener(this);

		buttonStart = (ImageButton) rootView.findViewById(R.id.record_series_start);
		buttonStart.setOnClickListener(this);

		buttonStop = (ImageButton) rootView.findViewById(R.id.record_series_stop);
		buttonStop.setOnClickListener(this);

		/*
		 * buttonView = (ImageButton)
		 * rootView.findViewById(R.id.record_series_view);
		 * buttonView.setOnClickListener(this);
		 */

		buttonKeep = (ImageButton) rootView.findViewById(R.id.record_series_keep);
		buttonKeep.setOnClickListener(this);

		/*buttonUpload = (ImageButton) rootView.findViewById(R.id.record_series_upload);
		buttonUpload.setOnClickListener(this);
		buttonUploadContainer = rootView.findViewById(R.id.record_series_upload_container);
*/
		buttonDiscard = (ImageButton) rootView.findViewById(R.id.record_series_discard);
		buttonDiscard.setOnClickListener(this);

		recordingPanel = (LinearLayout) rootView.findViewById(R.id.record_controls);

		seriesPanel = (LinearLayout) rootView.findViewById(R.id.complete_series_controls);

		ViewGroup.LayoutParams params = seriesPanel.getLayoutParams();
		params.height = 0;
		seriesPanel.setLayoutParams(params);

		projectTitlePanel = rootView.findViewById(R.id.record_project_title_panel);
		projectTitleView = (TextView) projectTitlePanel.findViewById(R.id.project_title);

		updateProfileView();

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.record, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		}

		return super.onOptionsItemSelected(item);
	}

	private void startSeries() {
		if (this.state == RecordState.IDLE) {
			DataLogger.get().startNewSeries();
			this.state = RecordState.RECORDING;
			this.currentSeries = DataLogger.get().getCurrentSeriesFile();
			updateSamplesCount();
			updateButtonPanel();
		}
	}

	private void animateraiseSeriesPanel(boolean up) {
		int height = up ? recordingPanel.getHeight() : 0;
		Animations.animateHeight(seriesPanel, height);
	}

	private void stopSeries() {
		if (this.state == RecordState.RECORDING) {
			DataLogger.get().stopSeries();
			this.state = RecordState.DECIDING;
			updateButtonPanel();

			int count = DataLogger.get().getCurrentSeriesSampleCount();
			String label = count == 1 ? getResources().getString(R.string.recorded_1) : String.format(getResources().getString(R.string.recorded_many), count);
			recordedLabel.setText(label);

			animateraiseSeriesPanel(true);
		}
	}

	private void discardSeries() {
		if (this.state == RecordState.DECIDING) {
			DataLogger.get().deleteData(currentSeries);
			this.state = RecordState.IDLE;
			this.currentSeries = null;
			updateButtonPanel();
			animateraiseSeriesPanel(false);
		}
	}

	private void uploadSeries() {
		Model profile = ProfileManager.get().getActiveProfile();
		int uploadState = DataLogger.get().currentUploadedStatus();

		if (this.state == RecordState.DECIDING && profile.getBool("is_remote") && uploadState == 0) {
			File series = DataLogger.get().getCurrentSeriesFile();
			UploadRemoteAction action = new UploadRemoteAction(profile, series);
			((RemoteCapableActivity) getActivity()).remoteRequest(action);
		}
	}

	private void keepSeries() {
		if (this.state == RecordState.DECIDING) {
			this.state = RecordState.IDLE;
			ProfileManager.get().getActiveProfile().getModel("dataviewer", true).setString("series", currentSeries.getName());
			this.currentSeries = null;
			updateButtonPanel();
			animateraiseSeriesPanel(false);
		}
	}

	private void updateButtonPanel() {
		buttonStart.setEnabled(state == RecordState.IDLE && ProfileManager.get().getActiveProfile().getModel("sensors", true).getModels().size() > 0);
		buttonStop.setEnabled(state == RecordState.RECORDING);

		buttonAdd.setEnabled(state != RecordState.RECORDING);

		boolean remote = ProfileManager.get().getActiveProfile().getBool("is_remote");
		int remoteStatus = remote ? DataLogger.get().currentUploadedStatus() : 0;
		//buttonUploadContainer.setVisibility(remote ? View.VISIBLE : View.GONE);
		//buttonUpload.setEnabled(remote && remoteStatus == 0);
		buttonDiscard.setEnabled(remoteStatus != 1);

		recordingIcon.setBlinking(state == RecordState.RECORDING);

		if (state != RecordState.RECORDING) {
			recordingLabel.setText("");
		}
	}

	private void updateProfileView() {
		this.adapter.updateSensorList();
		this.updateButtonPanel();

		Model profile = ProfileManager.get().getActiveProfile();
		ProjectItemManager.setProjectIcons(projectTitlePanel, profile);
		projectTitleView.setText(profile.getString("title"));
		projectTitlePanel.setVisibility(View.VISIBLE);
	}

	private void switchProfile() {
		this.state = RecordState.IDLE;
		this.currentSeries = null;

		updateProfileView();
	}

	private void updateSamplesCount() {
		int count = DataLogger.get().getCurrentSeriesSampleCount();
		String label = count == 1 ? getResources().getString(R.string.recording_1) : String.format(getResources().getString(R.string.recording_many), count);
		recordingLabel.setText(label);
	}

	private class EventListener extends EventManagerListener {

		@Override
		public void eventSetting(String event, boolean whilePaused) {
			switchProfile();
		}

		@Override
		public void eventProfile(String event, boolean whilePaused) {
			if (event != null && event.equals(ProfileManager.get().getActiveProfileId())) {
				updateProfileView();
			}
		}

		@Override
		public void eventNewData(String event, boolean whilePaused) {
			updateSamplesCount();
		}

		@Override
		public void eventDataStatus(String event, boolean whilePaused) {
			updateProfileView();
		}
	}

	@Override
	public void onClick(View v) {
		if (v == buttonAdd) {
			if (DataLogger.get().isIdle()) {
				SensorSelectDlg.open(getActivity(), R.string.add_profile_sensor_title, R.string.add_profile_sensor_msg, null, false, this);
			}
		}
		if (v == buttonStart) {
			startSeries();
		} else if (v == buttonStop) {
			stopSeries();
		} else if (v == buttonKeep) {
			keepSeries();
		} /*else if (v == buttonUpload) {
			uploadSeries();
		} */else if (v == buttonDiscard) {
			discardSeries();
		} /*else if (v == buttonView) {
			Intent intent = new Intent(getActivity(), DataViewerActivity.class);
			intent.putExtra(Arguments.ARG_PROFILE, ProfileManager.get().getActiveProfileId());
			startActivity(intent);
		}*/
	}

	@Override
	public void sensorsSelected(Vector<String> selected) {
		if (selected != null && DataLogger.get().isIdle()) {
			Model profile = ProfileManager.get().getActiveProfile();
			ProfileManager.get().addSensors(profile, selected);
		}
	}

	@Override
	public boolean sensorIsAvailable(String sensorId) {
		return !ProfileManager.get().sensorInActiveProfile(sensorId);
	}


	@Override
	public void profileSensorRateEditComplete(boolean set, String profileSensorId, double rate, int units) {
		if (set) {
			Model profile = ProfileManager.get().getActiveProfile();
			int newUnits = Math.min(2, Math.max(0, units));
			double newRate = ModelOperations.fitFateInRange(rate, newUnits, null, ModelDefaults.DATA_LOGGING_RATE_MAX);
			Model profileSensor = profile.getModel("sensors", true).getModel(profileSensorId, true);
			profileSensor.setDouble("sample_rate", newRate);
			profileSensor.setInt("sample_rate_ux", newUnits);
			profile.setBool("initial_edit", false);
		}
	}

	@Override
	public void profileSensorRateDelete(String sensorId) {
		if (DataLogger.get().isIdle()) {
			ProfileManager.get().removeSensorFromActiveProfile(sensorId);
		}
	}

	@Override
	public void recordSensorEdit(String profileSensorId) {
		if (DataLogger.get().isIdle()) {
			Model profile = ProfileManager.get().getActiveProfile();

			Model profileSensor = ProfileManager.get().getActiveProfile().getModel("sensors", true).getModel(profileSensorId);
			double rate = profileSensor.getDouble("sample_rate", ModelDefaults.DATA_LOGGING_RATE);
			int units = profileSensor.getInt("sample_rate_ux", ModelDefaults.DATA_LOGGING_UNITS);
			ProfileSensorDlg.open(getActivity(), profile, profileSensor, rate, units, this);
		}
	}

	@Override
	public void recordSensorSelected(String profileSensorId) {
		plotFragment.openPlot(profileSensorId);		
	}

}