package org.greengin.sciencetoolkit.common.logic.appstatus;

import android.support.v7.app.AppCompatActivity;

public class ApplicationStatusActivity extends AppCompatActivity {
	
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
