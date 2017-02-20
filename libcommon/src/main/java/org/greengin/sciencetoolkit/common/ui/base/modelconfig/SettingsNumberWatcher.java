package org.greengin.sciencetoolkit.common.ui.base.modelconfig;


import org.greengin.sciencetoolkit.common.model.Model;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class SettingsNumberWatcher implements TextWatcher, OnItemSelectedListener {
	ModelKeyChangeListener listener;
	Model settings;
	String key;

	String unitsKey;
	boolean hasUnits;
	double[] unitMultipliers;
	TextView numberEdit;
	
	boolean number;
	boolean decimal;
	boolean signed;
	Number defaultValue;
	Number min;
	Number max;

	public SettingsNumberWatcher(Model settings, String key, ModelKeyChangeListener listener) {
		this(settings, key, null, false, false, false, null, null, null, listener, null);
	}

	public SettingsNumberWatcher(Model settings, String key, Number defaultValue, boolean number, boolean decimal, boolean signed, Number min, Number max, double[] unitMultipliers, ModelKeyChangeListener listener, TextView numberEdit) {
		this.settings = settings;
		this.key = key;
		this.number = number;
		this.decimal = decimal;
		this.signed = signed;
		this.defaultValue = defaultValue;
		this.min = min;
		this.max = max;
		this.listener = listener;

		this.unitMultipliers = unitMultipliers;
		this.hasUnits = unitMultipliers != null;
		this.unitsKey = this.hasUnits ? this.key + "_ux" : null;
		this.numberEdit = this.hasUnits ? numberEdit : null;
	}

	private double getUnitMultiplier() {
		if (hasUnits) {
			int units = settings.getInt(unitsKey, 0);
			return units < unitMultipliers.length ? unitMultipliers[units] : 1d;
		} else {
			return 1.d;
		}
	}

	private double checkValueInRange(double value) {
		double multiplier = getUnitMultiplier();
		double afterUnitsValue = value * multiplier;
		if (min != null && afterUnitsValue < min.doubleValue()) {
			value = min.doubleValue() / multiplier;
		}
		if (max != null && afterUnitsValue > max.doubleValue()) {
			value = max.doubleValue() / multiplier;
		}

		return value;
	}

	private int checkValueInRange(int value) {
		double multiplier = getUnitMultiplier();
		int afterUnitsValue = hasUnits ? (int) Math.floor(value * multiplier) : value;

		if (min != null && afterUnitsValue < min.doubleValue()) {
			value = hasUnits ? (int) Math.ceil(min.intValue() / multiplier) : min.intValue();
		}

		if (max != null && afterUnitsValue > max.doubleValue()) {
			value = hasUnits ? (int) Math.floor(max.intValue() / multiplier) : max.intValue();
		}

		return value;
	}
	
	private boolean update(Editable s) {
		boolean modified = false;

		if (number) {
			if (decimal) {
				double value;
				try {
					value = Double.parseDouble(s.toString());
				} catch (NumberFormatException e) {
					value = 0.;
				}
				value = checkValueInRange(value);
				modified = settings.setDouble(key, value);
			} else {
				int value;
				try {
					value = Integer.parseInt(s.toString());
				} catch (NumberFormatException e) {
					value = 0;
				}
				value = checkValueInRange(value);
				modified = settings.setInt(key, value);
			}
		} else {
			modified = settings.setString(key, s.toString());
		}

		return modified;
	}

	@Override
	public void afterTextChanged(Editable s) {
		boolean numberModified = update(s);
		if (numberModified && listener != null) {
			listener.modelKeyModified(key);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		
		if (hasUnits) {
			boolean unitsModified = settings.setInt(unitsKey, position);
			boolean numberModified = update(numberEdit.getEditableText());

			if (listener != null && (unitsModified || numberModified)) {
				listener.modelKeyModified(key);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parentView) {
	}
}
