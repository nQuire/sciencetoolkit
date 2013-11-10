package org.greengin.sciencetoolkit.ui.components;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.ControlledRotationActivity;
import org.greengin.sciencetoolkit.ui.components.main.MainActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class WelcomeActivity extends ControlledRotationActivity {

	public WelcomeActivity() {
		super(0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome_activity, menu);
		return true;
	}
	
	public void actionStart(View view) {
		Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
	}

}
