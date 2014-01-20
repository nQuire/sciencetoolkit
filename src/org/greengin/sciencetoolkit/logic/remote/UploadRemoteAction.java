package org.greengin.sciencetoolkit.logic.remote;

import java.io.File;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
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

	private String createJsonBody() {
		try {

			Hashtable<String, String> metadata = DataLogger.get().getSeriesMetadata(profileId, series).getStrings();

			JSONObject jsobj = new JSONObject();
			jsobj.put("id", profileId);
			JSONObject jsmetadata = new JSONObject();
			for (Entry<String, String> entry : metadata.entrySet()) {
				Object v;
				try {
					v = new JSONObject(entry.getValue());
				} catch (JSONException e) {
					e.printStackTrace();
					v = entry.getValue();
				}
				jsmetadata.put(entry.getKey(), v);
			}
			
			jsobj.put("metadata", jsmetadata);

			return jsobj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public HttpRequestBase[] createRequests(String urlBase) {
		HttpPost post = new HttpPost(urlBase + "upload");

		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		ContentBody cbFile = new FileBody(series);
		entityBuilder.addPart("file", cbFile);
		entityBuilder.addTextBody("body", createJsonBody(), ContentType.APPLICATION_JSON);
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
