package org.greengin.sciencetoolkit.common.ui.base.events;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class EventFragment<T extends EventManager> extends Fragment {

	protected T eventManager;

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
