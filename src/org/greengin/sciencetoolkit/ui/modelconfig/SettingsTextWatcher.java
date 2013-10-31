package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.model.Model;

import android.text.Editable;
import android.text.TextWatcher;

public class SettingsTextWatcher implements TextWatcher {
	Model settings;
	String key;
	boolean number;
	boolean decimal;
	boolean signed;
	Number min;
	Number max;

	public SettingsTextWatcher(Model settings, String key) {
		this(settings, key, false, false, false, null, null);
	}

	public SettingsTextWatcher(Model settings, String key, boolean number, boolean decimal, boolean signed, Number min, Number max) {
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
					value = Double.parseDouble(s.toString());
					if (min != null) {
						value = Math.max(value, min.doubleValue());
					}
					if (max != null) {
						value = Math.min(value, max.doubleValue());
					}
				} catch (NumberFormatException e) {
					value = min != null ? min.doubleValue() : 0.;
				}
				settings.setDouble(key, value);
			} else {
				int value;
				try {
					value = Integer.parseInt(s.toString());
					if (min != null) {
						value = Math.max(value, min.intValue());
					}
					if (max != null) {
						value = Math.min(value, max.intValue());
					}
				} catch (NumberFormatException e) {
					value = min != null ? min.intValue() : 0;
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
