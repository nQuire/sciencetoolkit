package org.greengin.sciencetoolkit;


import org.greengin.sciencetoolkit.logic.appstatus.ApplicationStatusManager;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.deprecated.DeprecatedDataLogger;
import org.greengin.sciencetoolkit.logic.location.CurrentLocation;
import org.greengin.sciencetoolkit.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;

import android.app.Application;
import android.content.Context;

public class ScienceToolkitApplication extends Application {

	public static final boolean REMOTE_ENABLED = true;
	
    public void onCreate(){
        super.onCreate();
        Context context = this.getApplicationContext();
        
        SettingsManager.init(context);
        SensorWrapperManager.init(context);
        ProfileManager.init(context);
        
        DeprecatedDataLogger.init(context);
        DataLogger.init(context);
        
        ApplicationStatusManager.init(context);
        
        CurrentLocation.init(context);
        
        RemoteApi.init(context);

        VersionManager.check(context);
    }
    
}
