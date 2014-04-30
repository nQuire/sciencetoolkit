package org.greengin.sciencetoolkit.logic.appstatus;

import android.support.v7.app.ActionBarActivity;

public class ApplicationStatusActivity extends ActionBarActivity {
	
	@Override
	protected void onResume() {
		super.onResume();		
		ApplicationStatusManager.get().setAwake(true);
	}

	@Override
	protected void onPause() {
		super.onPause();		
		ApplicationStatusManager.get().setAwake(false);
	}

}
