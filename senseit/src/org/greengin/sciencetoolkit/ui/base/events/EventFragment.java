package org.greengin.sciencetoolkit.ui.base.events;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class EventFragment extends Fragment {

	protected EventManager eventManager;


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
