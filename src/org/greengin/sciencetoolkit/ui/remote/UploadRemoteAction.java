package org.greengin.sciencetoolkit.ui.remote;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class UploadRemoteAction extends RemoteJsonAction {
	String profileId;
	File series;
	
	public UploadRemoteAction(String profileId, File series) {
		this.profileId = profileId;
		this.series = series;
		
		DataLogger.get().markAsSent(profileId, series, true);
	}
	
	@Override
	public HttpRequestBase[] createRequests(String urlBase) {
		HttpPost post = new HttpPost(urlBase + "upload");
		
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
	    ContentBody cbFile = new FileBody(series);
	    entityBuilder.addPart("file", cbFile);
	    HttpEntity entity = entityBuilder.build();
	    post.setEntity(entity);
	    
		return new HttpRequestBase[] { post };
	}
	

	@Override
	public void result(int request, JSONObject result) {
		try {
			if (result.getBoolean("ok")) {
				Log.d("stk upload", "ok!");
			} else {
				Log.d("stk upload", "not ok!");
				error(result.getString("reason"));
			}
		} catch (JSONException e) {
			error("json");
			e.printStackTrace();
		}
	}
	
	@Override
	public void error(String error) {
		Log.d("stk upload", error);
		DataLogger.get().markAsSent(profileId, series, false);
	}

}
