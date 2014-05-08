package org.greengin.sciencetoolkit.spotit;


import android.app.Application;
import android.content.Context;

public class SpotItApplication extends Application {

	public static final boolean REMOTE_ENABLED = true;
	
    public void onCreate(){
        super.onCreate();
        Context context = this.getApplicationContext();
        

        VersionManager.check(context);
    }
    
}
