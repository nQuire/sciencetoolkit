package org.greengin.sciencetoolkit.ui.components.main.sensorlist;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.SensorUIData;
import org.greengin.sciencetoolkit.ui.components.main.sensorlist.config.SensorSettingsActivity;
import org.greengin.sciencetoolkit.ui.datafilters.DataUINotifier;
import org.greengin.sciencetoolkit.ui.plotting.LiveXYSensorPlotFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SensorFragment extends Fragment {

	private String sensorId;
	private SensorWrapper sensor;

	Model settings;

	FixedRateDataFilter periodFilter;
	DataPipe showValuePipe;
	String showValueIntentFilter;
	String[] showValueUnits;
	String currentValue;
	int showValueFormatMinInt;

	boolean showValue;

	BroadcastReceiver valueReceiver;
	ModelNotificationListener showListener;
	ModelNotificationListener profileListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(Arguments.ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);

		this.showValue = false;

		this.currentValue = null;

		this.showValueUnits = SensorUIData.getValueUnits(this.sensor.getType());

		this.showValueFormatMinInt = 1 + Math.max(0, (int) Math.ceil(Math.log(Math.abs(this.sensor.getMaxRange()))));

		this.showValueIntentFilter = "liveview:" + this.sensorId;
		this.settings = SettingsManager.get().get(this.showValueIntentFilter);

		int period = ModelOperations.rate2period(settings, "update_rate", ModelDefaults.LIVEVIEW_UPDATE_RATE, ModelDefaults.LIVEVIEW_UPDATE_RATE_MIN, ModelDefaults.LIVEVIEW_UPDATE_RATE_MAX);
		this.periodFilter = new FixedRateDataFilter(period);
		this.showValuePipe = new DataPipe(sensor);
		this.showValuePipe.addFilter(this.periodFilter);
		this.showValuePipe.setEnd(new DataUINotifier(activity.getApplicationContext(), this.showValueIntentFilter));
		this.valueReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				eventDataReceived(intent.getFloatArrayExtra("values"), intent.getIntExtra("valueCount", 0));
			}
		};

		this.showListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReceived(String msg) {
				int period = ModelOperations.rate2period(settings, "update_rate", ModelDefaults.LIVEVIEW_UPDATE_RATE, ModelDefaults.LIVEVIEW_UPDATE_RATE_MIN, ModelDefaults.LIVEVIEW_UPDATE_RATE_MAX);
				periodFilter.setPeriod(period);
				updateShowValue(getView(), false);
			}
		};

		this.profileListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReceived(String msg) {
				updateSensorInProfile(getView());
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();

		updateView(getView());

		SettingsManager.get().registerDirectListener(this.showValueIntentFilter, showListener);
		SettingsManager.get().registerDirectListener("profiles", profileListener);
		ProfileManager.get().registerDirectListener(profileListener);

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.valueReceiver, new IntentFilter(this.showValueIntentFilter));
	}

	@Override
	public void onPause() {
		super.onPause();

		this.showValuePipe.detach();
		destroyPlot();

		SettingsManager.get().unregisterDirectListener(this.showValueIntentFilter, showListener);
		SettingsManager.get().unregisterDirectListener("profiles", profileListener);
		ProfileManager.get().unregisterDirectListener(profileListener);
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
				actionToggleSensorInProfile(((ToggleButton) button).isChecked());
			}
		});

		toggleButton.setBackgroundDrawable(this.getResources().getDrawable(SensorUIData.getSensorToggleResource(sensor.getType())));

		TextView labelTextView = (TextView) rootView.findViewById(R.id.sensor_value_label);
		String label = SensorUIData.getValueLabelStr(sensor.getType());
		labelTextView.setText(label);

		ImageButton editButton = (ImageButton) rootView.findViewById(R.id.sensor_config_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SensorSettingsActivity.class);
				intent.putExtra(Arguments.ARG_SENSOR, sensorId);
				startActivity(intent);
			}
		});

		TextView showLabel = (TextView) rootView.findViewById(R.id.sensor_show_value);
		showLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				actionToogleShow(true);
			}
		});

		TextView hideLabel = (TextView) rootView.findViewById(R.id.sensor_hide_value);
		hideLabel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				actionToogleShow(false);
			}
		});

		updateShowValue(rootView, true);

		return rootView;
	}

	private void actionToggleSensorInProfile(boolean checked) {

		Model profile = ProfileManager.get().getActiveProfile();
		if (sensorId != null && profile != null) {
			if (ProfileManager.get().profileIsDefault(profile)) {
				if (checked) {
					ProfileManager.get().addSensorToActiveProfile(sensorId);
					actionToogleShow(true);
				} else {
					ProfileManager.get().removeSensorFromActiveProfile(sensorId);
					actionToogleShow(false);
				}
			} else {
				int msgId, titleId, positiveLabelId;
				DialogInterface.OnClickListener listener;

				if (checked) {
					msgId = R.string.sensor_not_in_profile_add_msg;
					titleId = R.string.sensor_not_in_profile_add_title;
					positiveLabelId = R.string.sensor_not_in_profile_add;
					listener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ProfileManager.get().addSensorToActiveProfile(sensorId);
						}
					};
				} else {
					msgId = R.string.sensor_in_profile_remove_msg;
					titleId = R.string.sensor_in_profile_remove_title;
					positiveLabelId = R.string.remove_sensor_dlg_yes;
					listener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ProfileManager.get().removeSensorFromActiveProfile(sensorId);
						}
					};

				}

				DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						updateSensorToggle(getView());
					}
				};

				DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						updateSensorToggle(getView());
					}
				};

				String msg = String.format(getResources().getString(msgId), sensorId, profile.getString("title"));
				CharSequence styledMsg = Html.fromHtml(msg);

				AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
				dlg.setIcon(android.R.drawable.ic_dialog_alert).setTitle(titleId).setMessage(styledMsg);
				dlg.setPositiveButton(positiveLabelId, listener);
				dlg.setNegativeButton(R.string.cancel, cancelButtonListener).setOnCancelListener(cancelListener);
				dlg.show();
			}
		}
	}

	protected void actionToogleShow(boolean checked) {
		settings.setBool("show", checked);
	}

	protected void createPlot() {
		LiveXYSensorPlotFragment fragment = new LiveXYSensorPlotFragment();
		Bundle args = new Bundle();
		args.putString(Arguments.ARG_SENSOR, sensorId);
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
		this.updateShowValue(view, false);
		this.updateValueView(view);
		this.updateSensorInProfile(view);
	}

	private void updateShowValue(View view, boolean forceUpdate) {
		boolean value = settings.getBool("show", false);
		if (value != this.showValue || forceUpdate) {
			this.showValue = value;

			if (showValue) {
				view.findViewById(R.id.sensor_value_section).setVisibility(View.VISIBLE);
				view.findViewById(R.id.sensor_show_value).setVisibility(View.GONE);
				view.findViewById(R.id.sensor_hide_value).setVisibility(View.VISIBLE);
				showValuePipe.attach();
				createPlot();
			} else {
				view.findViewById(R.id.sensor_value_section).setVisibility(View.GONE);
				view.findViewById(R.id.sensor_show_value).setVisibility(View.VISIBLE);
				view.findViewById(R.id.sensor_hide_value).setVisibility(View.GONE);
				showValuePipe.detach();
				destroyPlot();
			}
		}
	}

	private void updateValueView(View view) {
		if (settings.getBool("show") && view != null) {
			((TextView) view.findViewById(R.id.sensor_value)).setText(this.currentValue);
		}
	}

	private void updateSensorInProfile(View view) {
		if (view != null) {
			boolean inProfile = ProfileManager.get().sensorInActiveProfile(sensorId);
			TextView notice = (TextView) view.findViewById(R.id.in_profile_notice);
			if (ProfileManager.get().activeProfileIsDefault()) {
				notice.setVisibility(View.GONE);
			} else {
				notice.setText(inProfile ? R.string.sensor_in_profile : R.string.sensor_not_in_profile);
				notice.setVisibility(View.VISIBLE);
			}
			((ToggleButton) view.findViewById(R.id.sensor_value_toggle)).setChecked(inProfile);
		}
	}

	private void updateSensorToggle(View view) {
		if (view != null) {
			boolean inProfile = ProfileManager.get().sensorInActiveProfile(sensorId);
			((ToggleButton) view.findViewById(R.id.sensor_value_toggle)).setChecked(inProfile);
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
