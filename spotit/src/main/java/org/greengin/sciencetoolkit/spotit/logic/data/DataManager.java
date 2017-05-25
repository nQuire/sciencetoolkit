package org.greengin.sciencetoolkit.spotit.logic.data;


import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.common.model.events.NotificationListenerAggregator;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

import android.content.Context;

import java.io.File;

public class DataManager {
    private static DataManager instance;

    NotificationListenerAggregator listeners;
    Context applicationContext;

    public static void init(Context applicationContext) {
        instance = new DataManager(applicationContext);
    }

    public static DataManager get() {
        return instance;
    }

    private DataManager(Context applicationContext) {
        this.applicationContext = applicationContext;
        listeners = new NotificationListenerAggregator(applicationContext, "data:notifications");
    }

    public void deleteProjectData(Model removed) {

    }

    public int dataCount(String projectId) {
        Model project = ProjectManager.get().getProject(projectId);
        Model data = project.getModel("data", true);
        return data.entries().size();
    }

    public void registerDataListener(ModelNotificationListener listener) {
        listeners.addUIListener(listener);
    }

    public void unregisterDataListener(ModelNotificationListener listener) {
        listeners.removeUIListener(listener);
    }

    public void fireStatusModified(String event) {
        listeners.fireEvent(event);
    }

    public Model newData(String uri) {
        Model item = ProjectManager.get().getNewImageContainer().getModel(uri, true, true);

        item.setString("uri", uri);
        item.setLong("date", System.currentTimeMillis());
        ProjectManager.get().forceSave();
        fireStatusModified("newdata");

        return item;
    }

    public void markAsSent(Model observation, int status) {
        if (observation != null) {
            observation.setInt("uploaded", status, true);
            ProjectManager.get().forceSave();
            fireStatusModified("upload");
        }
    }

    public void deleteData(Model observation) {
        Model parent = observation.getParent();
        if (parent != null) {
            String uri = observation.getString("uri");
            parent.clear(uri);
            File file = new File(uri);
            fireStatusModified("newdata");
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean imageSent(Model observation) {
        Model project = ProjectManager.get().getActiveProject();
        if (observation != null && project != null) {
            String uri = observation.getString("uri");
            ProjectManager.get().getNewImageContainer().clear(uri);
            project.getModel("data", true).setModel(uri, observation);
            DataManager.get().markAsSent(observation, 2);
            return true;
        }
        return false;
    }
}
