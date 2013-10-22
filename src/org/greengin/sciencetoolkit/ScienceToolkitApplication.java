package org.greengin.sciencetoolkit;

import org.greengin.sciencetoolkit.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.sensors.SettingsManager;

import android.app.Application;

public class ScienceToolkitApplication extends Application {

    public void onCreate(){
        super.onCreate();
        SensorWrapperManager.init(this.getApplicationContext());
        SettingsManager.init();
    }

}