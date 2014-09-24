package org.greengin.sciencetoolkit.spotit;

import org.greengin.sciencetoolkit.common.logic.appstatus.ApplicationStatusManager;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

import android.app.Application;
import android.content.Context;

public class SpotItApplication extends Application {

	public static final boolean REMOTE_ENABLED = true;

	public void onCreate() {
		super.onCreate();
		Context context = this.getApplicationContext();

		SettingsManager.init(context);
		ProjectManager.init(context);
		DataManager.init(context);
		ApplicationStatusManager.init(context);

		RemoteApi.init(context);

		VersionManager.check(context);
	}

}
