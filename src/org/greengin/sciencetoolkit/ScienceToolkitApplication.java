package org.greengin.sciencetoolkit;



import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.settings.SettingsManager;

import android.app.Application;
import android.content.Context;

public class ScienceToolkitApplication extends Application {

    public void onCreate(){
        super.onCreate();
        Context context = this.getApplicationContext();
        
        SensorWrapperManager.init(context);
        SettingsManager.init(context);
        //DataManager.init(context);
    }

}