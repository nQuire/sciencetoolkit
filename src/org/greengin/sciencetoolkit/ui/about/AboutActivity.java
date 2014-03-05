package org.greengin.sciencetoolkit.ui.welcome;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.main.MainActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class WelcomeActivity extends SettingsControlledActivity {

	public WelcomeActivity() {
		super(0, false);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_welcome);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}
	
	public void actionStart(View view) {
		Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
	}

}
