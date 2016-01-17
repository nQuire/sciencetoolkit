package org.greengin.sciencetoolkit.ui.about;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.common.ui.base.SettingsControlledActivity;
import org.greengin.sciencetoolkit.ui.main.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;


public class AboutActivity extends SettingsControlledActivity {

	public AboutActivity() {
		super(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_welcome);
		
		TextView about = (TextView) findViewById(R.id.about_text);
		about.setText(Html.fromHtml(getString(R.string.about_text)));
		
		TextView disclaimer = (TextView) findViewById(R.id.about_disclaimer);
		disclaimer.setText(Html.fromHtml(getString(R.string.about_disclaimer)));
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
