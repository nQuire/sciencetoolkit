package org.greengin.sciencetoolkit;


import org.greengin.sciencetoolkit.common.logic.appstatus.ApplicationStatusManager;
import org.greengin.sciencetoolkit.common.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.deprecated.DeprecatedDataLogger;
import org.greengin.sciencetoolkit.logic.location.CurrentLocation;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.app.Application;
import android.content.Context;

public class ScienceToolkitApplication extends Application {

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
