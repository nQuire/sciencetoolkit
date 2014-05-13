package org.greengin.sciencetoolkit.spotit.logic.data;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

import android.content.Context;

public class DataManager {
	private static DataManager instance;

	public static void init(Context applicationContext) {
		instance = new DataManager(applicationContext);
	}

	public static DataManager get() {
		return instance;
	}

	private DataManager(Context applicationContext) {

	}

	public void deleteData(String projectId) {

	}

	public int dataCount(String projectId) {
		return 0;
	}

	public void registerDataListener(DataLoggerDataListener listener) {
		
	}

	public void unregisterDataListener(DataLoggerDataListener listener) {

	}
	
	public void newData(String uri) {
		Model project = ProjectManager.get().getActiveProject();
		if (project != null && uri != null) {
			Model item = project.getModel("data", true, true).getModel(uri, true, true);
			item.setString("uri", uri);
			ProjectManager.get().forceSave();
		}
	}
	
	
	
}

