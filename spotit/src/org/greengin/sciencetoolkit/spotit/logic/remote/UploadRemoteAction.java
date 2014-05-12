package org.greengin.sciencetoolkit.spotit.logic.remote;

import org.apache.http.client.methods.HttpRequestBase;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteJsonAction;
import org.greengin.sciencetoolkit.common.model.Model;
import org.json.JSONArray;
import org.json.JSONObject;

public class UploadRemoteAction extends RemoteJsonAction {
	String projectId;

	public UploadRemoteAction(Model project) {
		this.projectId = project.getModel("remote_info", true).getString("project");
	}

	@Override
	public HttpRequestBase[] createRequests(String urlBase) {
		return new HttpRequestBase[] { };
	}

	@Override
	public void result(int request, JSONObject result, JSONArray array) {
	}

	@Override
	public void error(int request, String error) {
	}

}
