package org.greengin.sciencetoolkit;


import android.app.Application;
import android.content.Context;

public class ScienceToolkitApplication extends Application {

    public void onCreate(){
        super.onCreate();
        Context context = this.getApplicationContext();
        
        SensorWrapperManager.init(context);
        SettingsManager.init();
        DataManager.init(context);
    }

}