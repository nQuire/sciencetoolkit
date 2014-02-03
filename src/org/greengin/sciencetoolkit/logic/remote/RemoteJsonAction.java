package org.greengin.sciencetoolkit.logic.remote;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class RemoteJsonAction extends RemoteAction {
	
	public abstract void result(int request, JSONObject result);
	
	
	final public void result(int request, String result) {
		try {
			JSONObject jobj = new JSONObject(result);
			this.result(request, jobj);
		} catch (JSONException e) {
			this.error("json");
			e.printStackTrace();
		}
	}

}