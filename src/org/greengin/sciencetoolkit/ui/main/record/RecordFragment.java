package org.greengin.sciencetoolkit.ui.main.record;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.animations.Animations;
import org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect.SelectSensorActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect.SensorSelectDlg;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;
import org.greengin.sciencetoolkit.ui.base.widgets.BlinkingImageView;
import org.greengin.sciencetoolkit.ui.main.share.ProjectItemManager;

import android.os.Bundle;
import android.util.Log;
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

public class RecordFragment extends EventFragment implements OnClickListener, SelectSensorActionListener {

	private enum RecordState {
		IDLE, RECORDING, DECIDING
	};

	RecordSensorListAdapter adapter;

	RecordState state;
	int currentSeries;
	boolean canUploadSeries;

	BlinkingImageView recordingIcon;

	ImageButton buttonAdd;

	ImageButton buttonStart;
	ImageButton buttonStop;
	ImageButton buttonView;
	ImageButton buttonDiscard;
	ImageButton buttonShare;
	ImageButton buttonUpload;
	ImageButton buttonKeep;

	LinearLayout recordingPanel;
	LinearLayout seriesPanel;
	int seriesPanelHeight;

	View projectTitlePanel;
	TextView projectTitleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.state = DataLogger.get().isRunning() ? RecordState.RECORDING : RecordState.IDLE;
		this.currentSeries = 0;

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

		adapter = new RecordSensorListAdapter(inflater);
		ListView grid = (ListView) rootView.findViewById(R.id.sensor_list);
		grid.setAdapter(adapter);

		recordingIcon = (BlinkingImageView) rootView.findViewById(R.id.recording_icon);

		buttonAdd = (ImageButton) rootView.findViewById(R.id.record_sensor_add);
		buttonAdd.setOnClickListener(this);

		buttonStart = (ImageButton) rootView.findViewById(R.id.record_series_start);
		buttonStart.setOnClickListener(this);

		buttonStop = (ImageButton) rootView.findViewById(R.id.record_series_stop);
		buttonStop.setOnClickListener(this);

		buttonView = (ImageButton) rootView.findViewById(R.id.record_series_view);
		buttonView.setOnClickListener(this);

		buttonKeep = (ImageButton) rootView.findViewById(R.id.record_series_keep);
		buttonKeep.setOnClickListener(this);

		recordingPanel = (LinearLayout) rootView.findViewById(R.id.record_controls);

		seriesPanel = (LinearLayout) rootView.findViewById(R.id.complete_series_controls);
		seriesPanelHeight = 288;

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
			this.currentSeries = DataLogger.get().getCurrentSeries();
			updateButtonPanel();
		}
	}

	private void animateraiseSeriesPanel(boolean up) {
		Animations.animateHeight(seriesPanel, up ? seriesPanelHeight : 0);
	}

	private void stopSeries() {
		if (this.state == RecordState.RECORDING) {
			DataLogger.get().stopSeries();
			this.state = RecordState.DECIDING;
			updateButtonPanel();
			animateraiseSeriesPanel(true);
		}
	}

	private void discardSeries() {
		if (this.state == RecordState.DECIDING) {
			DataLogger.get().deleteData(this.currentSeries);
			this.state = RecordState.IDLE;
			this.currentSeries = 0;
			updateButtonPanel();
			animateraiseSeriesPanel(false);
		}
	}

	private void keepSeries() {
		if (this.state == RecordState.DECIDING) {
			this.state = RecordState.IDLE;
			this.currentSeries = 0;
			updateButtonPanel();
			animateraiseSeriesPanel(false);
		}
	}

	private void updateButtonPanel() {
		buttonStart.setEnabled(state == RecordState.IDLE && ProfileManager.get().getActiveProfile().getModel("sensors", true).getModels().size() > 0);
		buttonStop.setEnabled(state == RecordState.RECORDING);

		buttonAdd.setEnabled(state != RecordState.RECORDING);
		
		recordingIcon.setBlinking(state == RecordState.RECORDING);
	}

	private void updateProfileView() {
		this.adapter.updateSensorList();
		this.updateButtonPanel();

		if (ProfileManager.get().activeProfileIsDefault()) {
			projectTitlePanel.setVisibility(View.GONE);
		} else {
			Model profile = ProfileManager.get().getActiveProfile();
			ProjectItemManager.setProjectIcons(projectTitlePanel, profile);
			projectTitleView.setText(profile.getString("title"));
			projectTitlePanel.setVisibility(View.VISIBLE);
		}
	}

	private void switchProfile() {
		this.state = RecordState.IDLE;
		this.currentSeries = 0;

		updateProfileView();
	}

	private void updateSamplesCount() {

	}

	private class EventListener extends EventManagerListener {
		public void eventSetting(String event, boolean whilePaused) {
			switchProfile();
		}

		public void eventProfile(String event, boolean whilePaused) {
			if (event != null && event.equals(ProfileManager.get().getActiveProfileId())) {
				updateProfileView();
			}
		}

		public void eventData(String event, boolean whilePaused) {
			if ("data".equals(event)) {
				updateSamplesCount();
			} else {
				updateButtonPanel();
			}
		}

	}

	@Override
	public void onClick(View v) {
		if (v == buttonAdd) {
			if (!DataLogger.get().isRunning()) {
				SensorSelectDlg.open(getActivity(), R.string.add_profile_sensor_title, R.string.add_profile_sensor_msg, null, false, this);
			}
		}
		if (v == buttonStart) {
			startSeries();
		} else if (v == buttonStop) {
			stopSeries();
		} else if (v == buttonKeep) {
			keepSeries();
		}
	}

	@Override
	public void sensorsSelected(Vector<String> selected) {
		if (selected != null && !DataLogger.get().isRunning()) {
			Model profile = ProfileManager.get().getActiveProfile();
			ProfileManager.get().addSensors(profile, selected);
		}
	}

	@Override
	public boolean sensorIsAvailable(String sensorId) {
		return ! ProfileManager.get().sensorInActiveProfile(sensorId);
	}

}