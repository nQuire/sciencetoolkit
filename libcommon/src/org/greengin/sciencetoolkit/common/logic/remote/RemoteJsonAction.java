package org.greengin.sciencetoolkit.common.logic.remote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class RemoteJsonAction extends RemoteAction {
	
	public abstract void result(int request, JSONObject result, JSONArray array);
	
	
	final public void result(int request, String result) {
		JSONObject jobj = null;
		JSONArray jarray = null;
		
		try {
			jobj = new JSONObject(result);
		} catch (JSONException ignored) {}
		
		try {
			jarray = new JSONArray(result);
		} catch (JSONException ignored) {}
		
		
		if (jobj == null && jarray == null) {
			Log.e("stk remote", result);
			this.error(request, "json");
		} else {
			this.result(request, jobj, jarray);
		}
		
	}

}
