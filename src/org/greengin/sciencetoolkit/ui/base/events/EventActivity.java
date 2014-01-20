package org.greengin.sciencetoolkit.ui.base.events;

import org.greengin.sciencetoolkit.logic.remote.RemoteCapableActivity;

import android.os.Bundle;

public class EventActivity extends RemoteCapableActivity {

	EventManager eventManager;

	protected void setEventListener(EventManagerListener listener) {
		this.eventManager.setListener(listener);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.eventManager = new EventManager();
	}

	@Override
	public void onPause() {
		super.onPause();
		this.eventManager.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.eventManager.resume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.eventManager.destroy();
	}
}
