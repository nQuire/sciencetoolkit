package org.greengin.sciencetoolkit.ui.fragments.settings;

import org.greengin.sciencetoolkit.settings.Settings;


import android.text.Editable;
import android.text.TextWatcher;

public class SettingsTextWatcher implements TextWatcher {
	Settings settings;
	String key;
	boolean number;
	boolean decimal;
	boolean signed;
	Number min;
	Number max;

	public SettingsTextWatcher(Settings settings, String key) {
		this(settings, key, false, false, false, null, null);
	}

	public SettingsTextWatcher(Settings settings, String key, boolean number, boolean decimal, boolean signed, Number min, Number max) {
		this.settings = settings;
		this.key = key;
		this.number = number;
		this.decimal = decimal;
		this.signed = signed;
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (number) {
			if (decimal) {
				double value;
				try {
					value = Math.max(Math.min(max.doubleValue(), Double.parseDouble(s.toString())), min.doubleValue());
				} catch (NumberFormatException e) {
					value = min.doubleValue();
				}
				settings.setDouble(key, value);
			} else {
				int value;
				try {
					value = Math.max(Math.min(max.intValue(), Integer.parseInt(s.toString())), min.intValue());
				} catch (NumberFormatException e) {
					value = min.intValue();
				}
				settings.setInt(key, value);
			}
		} else {
			settings.setString(key, s.toString());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
