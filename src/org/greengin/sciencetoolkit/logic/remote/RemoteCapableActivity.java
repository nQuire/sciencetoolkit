package org.greengin.sciencetoolkit.logic.remote;


import android.support.v7.app.ActionBarActivity;

public class RemoteCapableActivity extends ActionBarActivity {
	
	
	public void remoteRequest(RemoteAction action) {
		RemoteApi.get().request(this, action);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		RemoteApi.get().resumeRequests(this);
	}

}
