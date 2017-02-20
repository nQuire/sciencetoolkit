package org.greengin.sciencetoolkit.common.ui.base;


import org.greengin.sciencetoolkit.common.logic.appstatus.ApplicationStatusActivity;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteAction;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;


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
