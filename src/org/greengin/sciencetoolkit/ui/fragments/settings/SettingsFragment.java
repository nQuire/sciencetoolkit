package org.greengin.sciencetoolkit.ui.fragments.settings;

import java.util.HashMap;
import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.settings.Settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public abstract class SettingsFragment extends Fragment {

	Settings settings;
	private HashMap<String, View> optionViews;
	boolean settingsEnabled;
	LinearLayout rootContainer;

	public SettingsFragment() {
		this.settingsEnabled = true;
		this.optionViews = new HashMap<String, View>();
	}

	
	protected void setSettings(Settings settings) {
		this.settings = settings;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		rootContainer = (LinearLayout) rootView.findViewById(R.id.settings_panel);
		this.createConfigOptions(rootView);
		//this.updateView(rootView);
		return rootView;
	}
	
	protected abstract void createConfigOptions(View view);
	
	protected void addOptionText(String key, String label, String description) {
		EditText edit = new EditText(rootContainer.getContext());
		int inputtype = InputType.TYPE_CLASS_TEXT;
		edit.setInputType(inputtype);
		edit.setText(settings.getString(key));
		edit.addTextChangedListener(new SettingsTextWatcher(settings, key));		
		addRow(key, label, description, edit);
	}
	protected void addOptionNumber(String key, String label, String description, boolean decimal, boolean signed, Number defaultValue, Number min, Number max) {
		EditText edit = new EditText(rootContainer.getContext());
		int inputtype = InputType.TYPE_CLASS_NUMBER;
		if (signed) {
			inputtype |= InputType.TYPE_NUMBER_FLAG_SIGNED;
		}
		if (decimal) {
			inputtype |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
		}
		edit.setInputType(inputtype);
		edit.setText(settings.getNumber(key, defaultValue).toString());
		edit.addTextChangedListener(new SettingsTextWatcher(settings, key, true, decimal, signed, min, max));
		
		addRow(key, label, description, edit);
	}
	
	protected void addOptionToggle(String key, String label, String description, boolean defaultValue) {
		ToggleButton toggle = new ToggleButton(rootContainer.getContext());
		toggle.setChecked(settings.getBool(key, defaultValue));
		toggle.setTag(key);
		toggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String clickedkey = (String) view.getTag();
				settings.setBool(clickedkey, ((ToggleButton) view).isChecked());
			}
		});
		
		addRow(key, label, description, toggle);
	}
	
	protected void addOptionCheckbox(String key, String label, String description, boolean defaultValue) {
		CheckBox checkbox = new CheckBox(rootContainer.getContext());
		
		checkbox.setChecked(settings.getBool(key, defaultValue));
		checkbox.setTag(key);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				String clickedkey = (String) view.getTag();
				settings.setBool(clickedkey, checked);
			}
		});
		
		addRow(key, label, description, checkbox);
	}

	protected void addOptionSelect(String key, String label, String description, List<String> options, int defaultValue) {
		Spinner spinner = new Spinner(rootContainer.getContext());
		spinner.setTag(key);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(rootContainer.getContext(), android.R.layout.simple_spinner_item, options);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(settings.getInt(key, defaultValue));
		
		addRow(key, label, description, spinner);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String changekey = (String) parentView.getTag();
				settings.setInt(changekey, position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	protected void addOptionDateTime(String key, String label, String description, int defaultValue) {
		
		EditText date = new EditText(rootContainer.getContext());
		date.setClickable(true);
		date.setFocusable(false);
		date.setKeyListener(null);
		date.setOnClickListener(new DateTimePickerHelper(settings, key, "date"));
		date.setText(DateFormat.format("dd/MM/yy", settings.getLong(key)));

		EditText time = new EditText(rootContainer.getContext());
		time.setClickable(true);
		time.setFocusable(false);
		time.setKeyListener(null);
		time.setOnClickListener(new DateTimePickerHelper(settings, key, "time"));
		time.setText(DateFormat.format("hh:mm", settings.getLong(key)));

		
		addRow(key, label, description, new View[]{date, time});
	}

	private void addRow(String key, String label, String description, View widget) {
		addRow(key, label, description, new View[]{widget});
	
	}
		private void addRow(String key, String label, String description, View[] widgets) {
		LinearLayout row = new LinearLayout(rootContainer.getContext());
		row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		row.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView labelView = new TextView(rootContainer.getContext());
		labelView.setText(label);
		labelView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		row.addView(labelView);
		
		for (View widget : widgets) {
			row.addView(widget);
		}
		
		LinearLayout settingView = new LinearLayout(rootContainer.getContext());
		settingView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		settingView.setOrientation(LinearLayout.VERTICAL);
		settingView.addView(row);
		
		TextView descriptionView = new TextView(rootContainer.getContext());
		descriptionView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		descriptionView.setText(description);
		descriptionView.setTextSize(8);
		descriptionView.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
		settingView.addView(descriptionView);
		
		rootContainer.addView(settingView);
		this.optionViews.put(key, settingView);
	}

/*
	public void onResume() {
		super.onResume();
		this.updateView(this.getView());
	}

	private void updateView(View view) {
		this.enableSettings(this.settingsEnabled, view);
		updateVisibleViews();
	}

	private void updateVisibleViews() {
		for (Bundle option : options) {
			String requires = option.getString("requires");
			if (requires != null) {
				boolean show = (Boolean) this.listener.getOptionValue(requires);
				this.optionViews.get(option.getString("key")).setVisibility(show ? View.VISIBLE : View.GONE);
			}
		}
	}

	public void enableSettings(boolean enable) {
		this.settingsEnabled = enable;
		if (this.getView() != null) {
			this.enableSettings(this.settingsEnabled, this.getView());
		}
	}

	private void enableSettings(boolean enable, View view) {
		view.setEnabled(enable);
		if (view instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) view;
			for (int i = 0; i < group.getChildCount(); i++) {
				enableSettings(enable, group.getChildAt(i));
			}
		}
	}*/

}
