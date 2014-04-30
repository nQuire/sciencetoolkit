package org.greengin.sciencetoolkit.logic.remote;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class RemoteJsonAction extends RemoteAction {
	
	public abstract void result(int request, JSONObject result);
	
	
	final public void result(int request, String result) {
		try {
			Log.d("stk remote", "upload: " + result);
			JSONObject jobj = new JSONObject(result);
			this.result(request, jobj);
		} catch (JSONException e) {
			this.error(request, "json");
			Log.e("stk remote", result);
			e.printStackTrace();
		}
	}

}
