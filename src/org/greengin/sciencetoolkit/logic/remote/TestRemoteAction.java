package org.greengin.sciencetoolkit.logic.remote;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.json.JSONObject;

public class TestRemoteAction extends RemoteJsonAction {

	@Override
	public HttpRequestBase[] createRequests(String urlBase) {
		return new HttpRequestBase[] { new HttpGet(urlBase + "subscriptions?action=myprofiles") };
	}

	@Override
	public void result(int request, JSONObject result) {
		ProfileManager.get().updateRemoteProfiles(result);
	}

	@Override
	public void close() {
	}
}
