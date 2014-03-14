package org.greengin.sciencetoolkit.logic.remote;


import android.support.v7.app.ActionBarActivity;

public class RemoteCapableActivity extends ActionBarActivity {
	
	private RemoteAction remoteOnResumeAction;
	
	public RemoteCapableActivity() {
		super();
		remoteOnResumeAction = null;
	}
	
	public void remoteRequest(RemoteAction action) {
		this.remoteOnResumeAction = null;
		RemoteApi.get().request(this, action, false);
	}
	
	public void remoteSetOnResumeAction(RemoteAction remoteOnResumeAction) {
		this.remoteOnResumeAction = remoteOnResumeAction;	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (remoteOnResumeAction != null) {
			RemoteAction action = remoteOnResumeAction;
			remoteOnResumeAction = null;
			
			RemoteApi.get().request(this, action, true);
		}
	}

}
