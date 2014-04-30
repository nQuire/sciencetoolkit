package org.greengin.sciencetoolkit.logic.remote;


import org.greengin.sciencetoolkit.logic.appstatus.ApplicationStatusActivity;


public class RemoteCapableActivity extends ApplicationStatusActivity {
	
	
	public void remoteRequest(RemoteAction action) {
		RemoteApi.get().request(this, action);
	}
	
	@Override
	public void onResume() {
		super.onResume();

		RemoteApi.get().resumeRequests(this);
	}

}
