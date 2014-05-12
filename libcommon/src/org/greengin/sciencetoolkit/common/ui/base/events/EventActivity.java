package org.greengin.sciencetoolkit.common.ui.base.events;

import org.greengin.sciencetoolkit.common.ui.base.RemoteCapableActivity;

import android.os.Bundle;

public abstract class EventActivity<T extends EventManager> extends RemoteCapableActivity {

	T eventManager;
	
	protected abstract T createEventManager(); 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.eventManager = createEventManager();
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
