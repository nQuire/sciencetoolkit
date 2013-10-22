package org.greengin.sciencetoolkit.fragments;

import java.util.HashMap;
import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.sensors.SettingsListener;
import org.greengin.sciencetoolkit.sensors.SettingsManager;

import uihelpers.DateTimePickerHelper;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

public class SettingsFragment extends Fragment {

	List<Bundle> options;
	SettingsListener listener;
	private HashMap<String, View> optionViews;
	boolean settingsEnabled;

	public SettingsFragment() {
		this.settingsEnabled = true;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.listener = SettingsManager.getInstance().getSettings(getArguments().getString("settings"));
		this.options = listener.getOptions();
		this.optionViews = new HashMap<String, View>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		this.createConfigOptions(rootView);
		this.updateView(rootView);
		return rootView;
	}

	private void createConfigOptions(View view) {
		LinearLayout container = (LinearLayout) view.findViewById(R.id.settings_panel);
		for (Bundle option : this.options) {
			String key = option.getString("key");
			String type = option.getString("type");

			LinearLayout row = new LinearLayout(container.getContext());
			row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			row.setOrientation(LinearLayout.HORIZONTAL);

			TextView label = new TextView(container.getContext());
			label.setText(option.getString("name"));
			label.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			row.addView(label);

			if ("toggle".equals(type)) {
				ToggleButton toggle = new ToggleButton(container.getContext());
				toggle.setChecked((Boolean) this.listener.getOptionValue(key));
				toggle.setTag(key);
				toggle.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						String clickedkey = (String) view.getTag();
						listener.setOptionValue(clickedkey, ((ToggleButton) view).isChecked());
						configOptionChanged(clickedkey);
					}
				});

				row.addView(toggle);
			} else if ("number".equals(type)) {
				EditText edit = new EditText(container.getContext());
				int inputtype = InputType.TYPE_CLASS_NUMBER;
				if (option.getBoolean("signed")) {
					inputtype |= InputType.TYPE_NUMBER_FLAG_SIGNED;
				}
				if (option.getBoolean("decimal")) {
					inputtype |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
				}
				edit.setInputType(inputtype);
				edit.setText(listener.getOptionValue(key).toString());
				edit.addTextChangedListener(new OptionTextWatcher(option));

				row.addView(edit);
			} else if ("checkbox".equals(type)) {
				CheckBox edit = new CheckBox(container.getContext());
				edit.setTag(key);
				edit.setChecked((Boolean) listener.getOptionValue(key));
				
				edit.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton view, boolean checked) {
						String clickedkey = (String) view.getTag();
						listener.setOptionValue(clickedkey, ((CheckBox) view).isChecked());
						configOptionChanged(clickedkey);
					}					
				});

				row.addView(edit);
			} else if ("select".equals(type)) {
				Spinner spinner = new Spinner(container.getContext());
				spinner.setTag(key);
				List<String> list = option.getStringArrayList("options");
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_spinner_item, list);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(dataAdapter);
				spinner.setSelection((Integer)listener.getOptionValue(key));
				
				row.addView(spinner);
				
				spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
						String changekey = (String) parentView.getTag();
						listener.setOptionValue(changekey, position);
						configOptionChanged(changekey);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

			} else if ("datetime".equals(type)) {
				EditText date = new EditText(container.getContext());
				date.setClickable(true);
				date.setFocusable(false);
				date.setKeyListener(null);
				date.setOnClickListener(new DateTimePickerHelper(this.listener, key, "date"));
				date.setText(DateFormat.format("dd/MM/yy", (Long) listener.getOptionValue(key)));
				row.addView(date);

				EditText time = new EditText(container.getContext());
				time.setClickable(true);
				time.setFocusable(false);
				time.setKeyListener(null);
				time.setOnClickListener(new DateTimePickerHelper(this.listener, key, "time"));
				time.setText(DateFormat.format("hh:mm", (Long) listener.getOptionValue(key)));
				row.addView(time);
			}
			
			LinearLayout setting = new LinearLayout(container.getContext());
			setting.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			setting.setOrientation(LinearLayout.VERTICAL);
			setting.addView(row);
			
			TextView description = new TextView(container.getContext());
			description.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			description.setText(option.getString("description"));
			description.setTextSize(8);
			description.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
			setting.addView(description);
			
			container.addView(setting);
			this.optionViews.put(key, setting);
		}
	}

	private void configOptionChanged(String key) {
		this.updateVisibleViews();
	}

	private class OptionTextWatcher implements TextWatcher {
		Bundle option;

		public OptionTextWatcher(Bundle option) {
			this.option = option;
		}

		@Override
		public void afterTextChanged(Editable s) {
			Object value = null;
			if (option.getBoolean("decimal")) {
				value = Double.parseDouble(s.toString());
			} else {
				try {
					value = Integer.parseInt(s.toString());
				} catch (Exception e) {
					value = 0;
				}
			}

			listener.setOptionValue(option.getString("key"), value);
			configOptionChanged(option.getString("key"));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}

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
	}

}
