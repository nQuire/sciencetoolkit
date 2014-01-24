package org.greengin.sciencetoolkit.ui.main.record;


import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.ui.base.animations.Animations;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;

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

public class RecordFragment extends EventFragment   implements OnClickListener {
	
	private enum RecordState {
		IDLE, RECORDING, DECIDING
	};
	
	RecordSensorListAdapter adapter;
	
	RecordState state;
	int currentSeries;
	boolean canUploadSeries;
	
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
		
		buttonStart = (ImageButton) rootView.findViewById(R.id.record_series_start);
		buttonStart.setOnClickListener(this);

		buttonStop = (ImageButton) rootView.findViewById(R.id.record_series_stop);
		buttonStop.setOnClickListener(this);

		buttonView = (ImageButton) rootView.findViewById(R.id.record_series_view);
		buttonView.setOnClickListener(this);

		
		buttonKeep = (ImageButton) rootView.findViewById(R.id.record_series_keep);
		buttonKeep.setOnClickListener(this);
		
		seriesPanel = (LinearLayout) rootView.findViewById(R.id.complete_series_controls);
		seriesPanelHeight = Animations.measureHeight(seriesPanel);
		Log.d("stk record", "" + seriesPanelHeight);
		
		ViewGroup.LayoutParams params = seriesPanel.getLayoutParams();
		params.height = 0;
		seriesPanel.setLayoutParams(params);
		
		recordingPanel = (LinearLayout) rootView.findViewById(R.id.record_controls);
		updateButtonPanel();
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
		buttonStart.setEnabled(state == RecordState.IDLE);
		buttonStop.setEnabled(state == RecordState.RECORDING);
		
	}
	
	
	private void switchProfile() {
		this.state = RecordState.IDLE;
		this.currentSeries = 0;
		
		this.adapter.updateSensorList();
		this.updateButtonPanel();
	}
	
	private void updateSamplesCount() {
		
	}
	
	private class EventListener extends EventManagerListener {
		public void eventSetting(String event, boolean whilePaused) {
			switchProfile();
		}

		public void eventProfile(String event, boolean whilePaused) {
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
		if (v == buttonStart) {
			startSeries();
		} else if (v == buttonStop) {
			stopSeries();
		} else if (v == buttonKeep) {
			keepSeries();
		}
	}

}