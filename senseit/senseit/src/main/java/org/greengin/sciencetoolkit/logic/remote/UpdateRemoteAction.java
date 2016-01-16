package org.greengin.sciencetoolkit.logic.remote;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteJsonAction;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;


public class UpdateRemoteAction extends RemoteJsonAction {
	
	@Override
	public HttpRequestBase[] createRequests(String urlBase) {
		Log.d("stk profile", "update profiles");
		return new HttpRequestBase[] { new HttpGet(urlBase + "senseit/profiles") };
	}

	@Override
	public void result(int request, JSONObject result, JSONArray array) {
		ProfileManager.get().updateRemoteProfiles(result);
	}
	

	@Override
	public void close() {
	}
}
