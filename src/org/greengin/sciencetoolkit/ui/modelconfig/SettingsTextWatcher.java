package org.greengin.sciencetoolkit.ui.modelconfig;

import org.greengin.sciencetoolkit.model.Model;

import android.text.Editable;
import android.text.TextWatcher;

public class SettingsTextWatcher implements TextWatcher {
	ModelKeyChangeListener listener;
	Model settings;
	String key;
	boolean number;
	boolean decimal;
	boolean signed;
	Number min;
	Number max;

	public SettingsTextWatcher(Model settings, String key, ModelKeyChangeListener listener) {
		this(settings, key, false, false, false, null, null, listener);
	}

	public SettingsTextWatcher(Model settings, String key, boolean number, boolean decimal, boolean signed, Number min, Number max, ModelKeyChangeListener listener) {
		this.settings = settings;
		this.key = key;
		this.number = number;
		this.decimal = decimal;
		this.signed = signed;
		this.min = min;
		this.max = max;
		this.listener = listener;
	}

	@Override
	public void afterTextChanged(Editable s) {
		boolean modified = false;

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
				modified = settings.setDouble(key, value);
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

				modified = settings.setInt(key, value);
			}
		} else {
			modified = settings.setString(key, s.toString());
		}

		if (modified && listener != null) {
			listener.modelKeyModified(key);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
