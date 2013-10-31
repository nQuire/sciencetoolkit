package org.greengin.sciencetoolkit.ui.modelconfig;

import java.util.HashMap;
import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;

import android.app.Activity;
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

public abstract class ModelFragment extends Fragment {

	Model model;
	private HashMap<String, View> optionViews;
	boolean settingsEnabled;
	LinearLayout rootContainer;

	public ModelFragment() {
		this.settingsEnabled = true;
		this.optionViews = new HashMap<String, View>();
	}
	
	protected abstract Model fetchModel();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		model = fetchModel();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		rootContainer = (LinearLayout) rootView.findViewById(R.id.settings_panel);
		this.createConfigOptions(rootView);
		// this.updateView(rootView);
		return rootView;
	}

	protected abstract void createConfigOptions(View view);

	protected void addOptionText(String key, String label, String description) {
		EditText edit = new EditText(rootContainer.getContext());
		int inputtype = InputType.TYPE_CLASS_TEXT;
		edit.setInputType(inputtype);
		edit.setText(model.getString(key));
		edit.addTextChangedListener(new SettingsTextWatcher(model, key));
		addRow(key, label, description, edit);
	}

	public void addOptionNumber(String key, String label, String description, boolean decimal, boolean signed, Number defaultValue, Number min, Number max) {
		EditText edit = new EditText(rootContainer.getContext());
		int inputtype = InputType.TYPE_CLASS_NUMBER;
		if (signed) {
			inputtype |= InputType.TYPE_NUMBER_FLAG_SIGNED;
		}
		if (decimal) {
			inputtype |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
		}
		edit.setInputType(inputtype);
		edit.setText(model.getNumber(key, defaultValue).toString());
		edit.addTextChangedListener(new SettingsTextWatcher(model, key, true, decimal, signed, min, max));

		addRow(key, label, description, edit);
	}

	protected void addOptionToggle(String key, String label, String description, boolean defaultValue) {
		ToggleButton toggle = new ToggleButton(rootContainer.getContext());
		toggle.setChecked(model.getBool(key, defaultValue));
		toggle.setTag(key);
		toggle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String clickedkey = (String) view.getTag();
				model.setBool(clickedkey, ((ToggleButton) view).isChecked());
			}
		});

		addRow(key, label, description, toggle);
	}

	protected void addOptionCheckbox(String key, String label, String description, boolean defaultValue) {
		CheckBox checkbox = new CheckBox(rootContainer.getContext());

		checkbox.setChecked(model.getBool(key, defaultValue));
		checkbox.setTag(key);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				String clickedkey = (String) view.getTag();
				model.setBool(clickedkey, checked);
			}
		});

		addRow(key, label, description, checkbox);
	}

	public void addText(String text) {
		LinearLayout row = new LinearLayout(rootContainer.getContext());
		row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		row.setOrientation(LinearLayout.HORIZONTAL);

		TextView labelView = new TextView(rootContainer.getContext());
		labelView.setText(text);
		labelView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		row.addView(labelView);

		rootContainer.addView(row);
	}

	public void addOptionSelect(String key, String label, String description, List<String> options, int defaultValue) {
		Spinner spinner = new Spinner(rootContainer.getContext());
		spinner.setTag(key);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(rootContainer.getContext(), android.R.layout.simple_spinner_item, options);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(model.getInt(key, defaultValue));

		addRow(key, label, description, spinner);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String changekey = (String) parentView.getTag();
				model.setInt(changekey, position);
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
		date.setOnClickListener(new DateTimePickerHelper(model, key, "date"));
		date.setText(DateFormat.format("dd/MM/yy", model.getLong(key)));

		EditText time = new EditText(rootContainer.getContext());
		time.setClickable(true);
		time.setFocusable(false);
		time.setKeyListener(null);
		time.setOnClickListener(new DateTimePickerHelper(model, key, "time"));
		time.setText(DateFormat.format("hh:mm", model.getLong(key)));

		addRow(key, label, description, new View[] { date, time });
	}

	private void addRow(String key, String label, String description, View widget) {
		addRow(key, label, description, new View[] { widget });

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
}
