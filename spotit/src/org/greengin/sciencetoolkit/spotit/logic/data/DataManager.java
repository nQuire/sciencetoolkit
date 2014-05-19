package org.greengin.sciencetoolkit.spotit.logic.data;


import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.common.model.events.NotificationListenerAggregator;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

import android.content.Context;

public class DataManager {
	private static DataManager instance;
	NotificationListenerAggregator listeners;

	public static void init(Context applicationContext) {
		instance = new DataManager(applicationContext);
	}

	public static DataManager get() {
		return instance;
	}

	private DataManager(Context applicationContext) {
		listeners = new NotificationListenerAggregator(applicationContext, "data:notifications");
	}

	public void deleteData(String projectId) {

	}

	public int dataCount(String projectId) {
		return 0;
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

	public void newData(String uri) {
		Model project = ProjectManager.get().getActiveProject();
		if (project != null && uri != null) {
			Model item = project.getModel("data", true, true).getModel(uri,
					true, true);
			item.setString("uri", uri);
			item.setLong("date", System.currentTimeMillis());
			ProjectManager.get().forceSave();
			fireStatusModified("newdata");
		}
	}

	public void markAsSent(Model project, Model observation, int status) {
		if (observation != null) {
			observation.setInt("uploaded", status, true);
			ProjectManager.get().forceSave();
			fireStatusModified("upload");
		}
	}

}
