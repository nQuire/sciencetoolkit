package org.greengin.sciencetoolkit.ui.base.modelconfig.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.greengin.sciencetoolkit.logic.remote.RemoteApi;
import org.greengin.sciencetoolkit.model.ModelDefaults;

import android.accounts.Account;
import android.view.View;

public class AppSettingsFragment extends AbstractSettingsFragment {

	@Override
	protected void createConfigOptions(View view) {
		List<String> options = Arrays.asList("Portrait", "Landscape", "Rotate with device");
		addOptionSelect("screen_orientation", "Screen orientation", "Select the screen orientation", options, ModelDefaults.APP_SCREEN_ORIENTATION);
		
		List<String> accountOptions = new ArrayList<String>();
		accountOptions.add("Do not log in");
		for (Account account : RemoteApi.get().availableAccounts()) {
			accountOptions.add(account.name);
		}
		addOptionSelectByValue("account", "Loggin account", "Select the account to upload your data.", accountOptions, null, true);
	}

}
