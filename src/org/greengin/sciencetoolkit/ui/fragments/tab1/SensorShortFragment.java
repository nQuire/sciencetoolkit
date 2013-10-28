package org.greengin.sciencetoolkit.ui.fragments.tab1;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataTube;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.ui.SensorUIData;
import org.greengin.sciencetoolkit.ui.datafilters.DataUINotifier;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SensorShortFragment extends Fragment {
	public static final String ARG_SENSOR = "sensor";

	private String sensorId;
	private SensorWrapper sensor;

	private boolean showValue;
	DataTube showValueTube;
	String showValueIntentFilter;
	String[] showValueUnits;
	String currentValue;
	int showValueFormatMinInt;

	BroadcastReceiver valueReceiver;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.d("stk ssf", "attach");

		this.sensorId = getArguments().getString(ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);

		this.currentValue = null;

		this.showValueUnits = SensorUIData.getValueUnits(this.sensor.getType());

		this.showValueFormatMinInt = 1 + Math.max(0, (int) Math.ceil(Math.log(Math.abs(this.sensor.getMaxRange()))));

		this.showValue = false;
		this.showValueIntentFilter = "livevalue:" + this.sensorId;

		this.showValueTube = new DataTube(sensor);
		this.showValueTube.append(new FixedRateDataFilter(100));
		this.showValueTube.setEnd(new DataUINotifier(activity.getApplicationContext(), this.showValueIntentFilter));
		this.valueReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("stk ssf", "msg");
				eventDataReceived(intent.getFloatArrayExtra("values"), intent.getIntExtra("valueCount", 0));
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_short_sensor, container, false);

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
		toggleButton.setChecked(this.showValue);

		TextView labelTextView = (TextView) rootView.findViewById(R.id.sensor_value_label);
		String label = SensorUIData.getValueLabelStr(sensor.getType());
		labelTextView.setText(label);

		this.updateView(rootView);

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.valueReceiver, new IntentFilter(this.showValueIntentFilter));
		
		if (showValue) {
			showValueTube.attach();			
		}
		
		return rootView;
	}

	@Override 
	public void onDestroyView() {
		super.onDestroyView();
		
		this.showValueTube.detach();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(this.valueReceiver);
		
		Log.d("stk ssf", "ondestroyview");
	}

	protected void actionToogleSensorValueView(boolean checked) {
		this.showValue = checked;
		if (checked) {
			this.showValueTube.attach();
			createPlot();
		} else {
			this.showValueTube.detach();
			destroyPlot();
		}

		updateView(getView());
	}

	protected void createPlot() {		
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		LiveSensorPlotFragment fragment = new LiveSensorPlotFragment();
		Bundle args = new Bundle();
		args.putString(LiveSensorPlotFragment.ARG_SENSOR, sensorId);
		fragment.setArguments(args);
		
		fragmentTransaction.replace(R.id.sensor_plot_section, fragment, "plot");
		fragmentTransaction.commit();
	}

	protected void destroyPlot() {
		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		Fragment fragment = fragmentManager.findFragmentByTag("plot");

		if (fragment != null) {
			fragmentTransaction.remove(fragment);
		}
		
		fragmentTransaction.commit();
	}

	protected void eventDataReceived(float[] values, int valueCount) {
		currentValue = formatValue(values, valueCount);
		this.updateValueView(this.getView());
	}

	private void updateView(View view) {
		((ToggleButton) view.findViewById(R.id.sensor_value_toggle)).setChecked(this.showValue);
		view.findViewById(R.id.sensor_value_section).setVisibility(this.showValue ? View.VISIBLE : View.GONE);
		this.updateValueView(view);
	}

	private void updateValueView(View view) {
		if (this.showValue) {
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
