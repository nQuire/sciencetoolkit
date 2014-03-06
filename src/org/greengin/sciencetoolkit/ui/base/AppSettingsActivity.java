package org.greengin.sciencetoolkit.ui.base;

import org.apache.http.impl.client.DefaultHttpClient;
import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.modelconfig.SettingsFragmentManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class AppSettingsActivity extends SettingsControlledActivity {

	

	public static final String SELECT_ACCOUNT = "SELECT_ACCOUNT";

	DefaultHttpClient http_client = new DefaultHttpClient();
	protected AccountManager accountManager;
	protected Intent intent;
	Account[] accounts;

	Button selectAccount;
	TextView accountLabel;

	
	public AppSettingsActivity() {
		super(true);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_app_settings);

		SettingsFragmentManager.insert(getSupportFragmentManager(), R.id.app_settings, "app");
	}

	
}
