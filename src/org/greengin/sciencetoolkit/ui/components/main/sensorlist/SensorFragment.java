package org.greengin.sciencetoolkit.ui.components.main.sensorlist;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.SensorUIData;
import org.greengin.sciencetoolkit.ui.datafilters.DataUINotifier;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SensorFragment extends Fragment {
	public static final String ARG_SENSOR = "sensor";

	private String sensorId;
	private SensorWrapper sensor;

	Model settings;

	FixedRateDataFilter periodFilter;
	DataPipe showValuePipe;
	String showValueIntentFilter;
	String[] showValueUnits;
	String currentValue;
	int showValueFormatMinInt;

	BroadcastReceiver valueReceiver;
	ModelNotificationListener periodListener;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);

		this.currentValue = null;

		this.showValueUnits = SensorUIData.getValueUnits(this.sensor.getType());

		this.showValueFormatMinInt = 1 + Math.max(0, (int) Math.ceil(Math.log(Math.abs(this.sensor.getMaxRange()))));

		this.showValueIntentFilter = "liveview:" + this.sensorId;
		this.settings = SettingsManager.getInstance().get(this.showValueIntentFilter);

		this.periodFilter = new FixedRateDataFilter(settings.getInt("period", ModelDefaults.LIVEVIEW_PERIOD));
		this.showValuePipe = new DataPipe(sensor);
		this.showValuePipe.addFilter(this.periodFilter);
		this.showValuePipe.setEnd(new DataUINotifier(activity.getApplicationContext(), this.showValueIntentFilter));
		this.valueReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				eventDataReceived(intent.getFloatArrayExtra("values"), intent.getIntExtra("valueCount", 0));
			}
		};
		
		this.periodListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReveiced(String msg) {
				periodFilter.setPeriod(settings.getInt("period", ModelDefaults.LIVEVIEW_PERIOD));
			}
		};
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SettingsManager.getInstance().registerDirectListener(this.showValueIntentFilter, periodListener);		
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.valueReceiver, new IntentFilter(this.showValueIntentFilter));

		if (settings.getBool("show")) {
			showValuePipe.attach();
			createPlot();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		this.showValuePipe.detach();
		destroyPlot();
		SettingsManager.getInstance().unregisterDirectListener(this.showValueIntentFilter, periodListener);
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.valueReceiver);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.sensor_name);
		nameTextView.setText(this.sensor.getName());
		nameTextView.setTag(this.sensorId);

		ToggleButton toggleButton = (ToggleButton) rootView.findViewById(R.id.sensor_value_toggle);
		toggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View button) {
				actionToogleSensorValueView(((ToggleButton) button).isChecked());
			}
		});

		toggleButton.setBackgroundDrawable(this.getResources().getDrawable(SensorUIData.getSensorToggleResource(sensor.getType())));

		TextView labelTextView = (TextView) rootView.findViewById(R.id.sensor_value_label);
		String label = SensorUIData.getValueLabelStr(sensor.getType());
		labelTextView.setText(label);

		this.updateView(rootView);

		
		ImageButton editButton = (ImageButton) rootView.findViewById(R.id.sensor_config_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SensorSettingsActivity.class);
				intent.putExtra("sensor", sensorId);				
		    	startActivity(intent);
			}
		});

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

	}

	protected void actionToogleSensorValueView(boolean checked) {
		if (settings.setBool("show", checked)) {
			if (checked) {
				this.showValuePipe.attach();
				createPlot();
			} else {
				this.showValuePipe.detach();
				destroyPlot();
			}

			updateView(getView());
		}
	}

	protected void createPlot() {
		LiveSensorPlotFragment fragment = new LiveSensorPlotFragment();
		Bundle args = new Bundle();
		args.putString(LiveSensorPlotFragment.ARG_SENSOR, sensorId);
		fragment.setArguments(args);

		getChildFragmentManager().beginTransaction().replace(R.id.sensor_plot_section, fragment, "plot").commit();
	}

	protected void destroyPlot() {
		Fragment fragment = getChildFragmentManager().findFragmentByTag("plot");
		if (fragment != null) {
			getChildFragmentManager().beginTransaction().remove(fragment).commit();
		}
	}

	protected void eventDataReceived(float[] values, int valueCount) {
		currentValue = formatValue(values, valueCount);
		this.updateValueView(this.getView());
	}

	private void updateView(View view) {
		boolean show = settings.getBool("show");
		
		((ToggleButton) view.findViewById(R.id.sensor_value_toggle)).setChecked(show);
		view.findViewById(R.id.sensor_value_section).setVisibility(show ? View.VISIBLE : View.GONE);
		this.updateValueView(view);
	}

	private void updateValueView(View view) {
		if (settings.getBool("show") && view != null) {
			((TextView) view.findViewById(R.id.sensor_value)).setText(this.currentValue);
		}
	}

	protected String formatValue(float[] value, int count) {
		if (value == null) {
			return "";
		} else {
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < count; i++) {
				if (i > 0) {
					buff.append("\n");
				}

				String vstr = Float.toString(value[i]);
				int p = vstr.indexOf('.');
				if (p < 0) {
					p = vstr.length();
				}
				for (int j = p; j < this.showValueFormatMinInt; j++) {
					buff.append(' ');
				}

				buff.append(vstr).append(' ').append(this.showValueUnits[i]);
			}
			return buff.toString();
		}
	}

}
