package org.greengin.sciencetoolkit.spotit.logic.remote;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteJsonAction;
import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.ui.base.ToastMaker;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

public class UploadRemoteAction extends RemoteJsonAction {
    Activity context;
    Model observation;

    public UploadRemoteAction(Activity context, Model observation) {
        this.context = context;
        this.observation = observation;
        DataManager.get().markAsSent(observation, 1);
    }

    @Override
    public HttpRequestBase[] createRequests(String urlBase) {
        File image = new File(observation.getString("uri"));
        if (image.exists() && image.isFile() && ProjectManager.get().getActiveProject() != null) {
            String title = observation.getString("title");
            if (title.length() == 0) {
                title = image.getName();
            }

            HttpPost post = new HttpPost(String.format(
                    "%sproject/%s/spotit/data", urlBase,
                    ProjectManager.get().getActiveProjectId()));

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
                    .create();

            entityBuilder.addTextBody("title", title, ContentType.TEXT_PLAIN);
            entityBuilder.addTextBody("description", "", ContentType.TEXT_PLAIN);
            entityBuilder.addTextBody("geolocation", "", ContentType.TEXT_PLAIN);

            entityBuilder.addPart("image", new FileBody(image));
            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);
            return new HttpRequestBase[]{post};
        }

        return new HttpRequestBase[]{};

    }

    @Override
    public void result(int request, JSONObject result, JSONArray array) {
        Log.d("stk remote", "result: " + result.toString());

        try {
            String id = result.getString("newItemId");
            if (id != null && DataManager.get().imageSent(observation)) {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        ToastMaker.l(context, "Data uploaded successfully!");
                    }
                });
            } else {
                error(request, result.getString("create"));
            }
        } catch (Exception e) {
            error(request, "The server did not allow uploading data at this point.");
            e.printStackTrace();
        }
    }

    @Override
    public void error(int request, String error) {
        final String msg = "nologin".equals(error) ? "It was not possible to upload the data because you are not logged in." : error;
        context.runOnUiThread(new Runnable() {
            public void run() {
                ToastMaker.le(context, msg);
            }
        });

        DataManager.get().markAsSent(observation, 0);
    }
}
