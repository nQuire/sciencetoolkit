package org.greengin.sciencetoolkit.ui.about;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.main.MainActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class AboutActivity extends SettingsControlledActivity {

	public AboutActivity() {
		super(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_welcome);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}
	
	public void actionStart(View view) {
		Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
	}

}
