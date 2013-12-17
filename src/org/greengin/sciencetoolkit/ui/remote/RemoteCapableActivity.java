package org.greengin.sciencetoolkit.ui.remote;

import org.greengin.sciencetoolkit.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.ui.SettingsControlledActivity;

public class RemoteCapableActivity extends SettingsControlledActivity {
	
	private RemoteAction remoteOnResumeAction;
	
	public RemoteCapableActivity() {
		super();
		remoteOnResumeAction = null;
	}
	
	public void remoteRequest(RemoteAction action) {
		RemoteApi.get().request(this, action);
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
			
			RemoteApi.get().request(this, action);
		}
	}
}
