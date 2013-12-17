package org.greengin.sciencetoolkit.ui.components.main.datalogging;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerDataListener;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.CreateProfileDialogFragment;
import org.greengin.sciencetoolkit.ui.ParentListFragment;
import org.greengin.sciencetoolkit.ui.components.main.profiles.edit.AddSensorDialogFragment;
import org.greengin.sciencetoolkit.ui.components.main.profiles.edit.ProfileEditActivity;
import org.greengin.sciencetoolkit.ui.components.main.profiles.view.SeriesViewActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class DataLoggingFragment extends ParentListFragment implements ModelNotificationListener, DataLoggerDataListener, DataLoggerStatusListener {

	public DataLoggingFragment() {
		super(R.id.sensor_list);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_data_logging, container, false);

		rootView.findViewById(R.id.data_logging_start).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DataLogger.get().startNewSeries();
				updateSeriesButtons(view.getRootView());
			}
		});

		rootView.findViewById(R.id.data_logging_stop).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DataLogger.get().stopSeries();
				updateSeriesButtons(view.getRootView());
			}
		});

		rootView.findViewById(R.id.profile_edit).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
				intent.putExtra(Arguments.ARG_PROFILE, ProfileManager.get().getActiveProfileId());
				startActivity(intent);
			}
		});

		rootView.findViewById(R.id.profile_data_view).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String profileId = ProfileManager.get().getActiveProfileId();

				if (profileId != null) {
					Intent intent = new Intent(getActivity(), SeriesViewActivity.class);
					intent.putExtra(Arguments.ARG_PROFILE, profileId);
					startActivity(intent);
				}
			}
		});

		rootView.findViewById(R.id.profile_data_export).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				Model profile = ProfileManager.i().getActiveProfile();
				String profileId = ProfileManager.i().getActiveProfileId();

				if (profile != null) {
					File exportFile = DataLogger.i().exportData(profileId);
					if (exportFile != null) {
						String exportMsg = String.format(getResources().getString(R.string.export_data_dlg_msg), profile.getString("title"), exportFile.getAbsolutePath());
						CharSequence styledExportMsg = Html.fromHtml(exportMsg);
						new AlertDialog.Builder(v.getContext()).setIcon(R.drawable.ic_action_save).setTitle(R.string.export_data_dlg_title).setMessage(styledExportMsg).setPositiveButton(R.string.export_data_dlg_yes, new ShareClickListener(getActivity(), profile.getString("title"), exportFile)).setNegativeButton(R.string.export_data_dlg_no, null).show();
					}
				}
				*/
			}
		});

		rootView.findViewById(R.id.profile_data_discard).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Model profile = ProfileManager.get().getActiveProfile();

				if (profile != null) {
					String deleteMsg = String.format(getResources().getString(R.string.delete_profile_data_dlg_msg), profile.getString("title"));
					CharSequence styledDeleteMsg = Html.fromHtml(deleteMsg);
					new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_profile_data_dlg_title).setMessage(styledDeleteMsg).setPositiveButton(R.string.delete_dlg_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String profileId = ProfileManager.get().getActiveProfileId();
							DataLogger.get().deleteData(profileId);
						}
					}).setNegativeButton(R.string.cancel, null).show();
				}
			}
		});

		rootView.findViewById(R.id.create_profile_from_default_link).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateProfileDialogFragment.showCreateProfileDialog(getChildFragmentManager(), true, true);
			}
		});

		return rootView;
	}

	private void updateView(View rootView) {
		if (rootView != null) {
			Model profile = ProfileManager.get().getActiveProfile();

			if (ProfileManager.get().activeProfileIsDefault()) {
				rootView.findViewById(R.id.current_profile_bar).setVisibility(View.GONE);

			} else {
				rootView.findViewById(R.id.current_profile_bar).setVisibility(View.VISIBLE);
				((TextView) rootView.findViewById(R.id.current_profile_name)).setText(profile.getString("title"));
			}

			TextView noSensorsNotice = (TextView) rootView.findViewById(R.id.no_sensors_notice);
			View buttonsContainer = rootView.findViewById(R.id.data_logging_buttons);

			int sensorCount = profile.getModel("sensors", true).getModels().size();
			if (sensorCount == 0) {
				buttonsContainer.setVisibility(View.GONE);

				CharSequence text;
				BufferType type;

				if (ProfileManager.get().activeProfileIsDefault()) {
					text = getResources().getString(R.string.no_sensor_notice_w_o_profile);
					type = BufferType.NORMAL;
				} else {
					String pre = getResources().getString(R.string.no_sensor_notice_with_profile_pre);
					String post = getResources().getString(R.string.no_sensor_notice_with_profile_post);

					SpannableString spannabletext = new SpannableString(pre + " " + post);
					Drawable d = getResources().getDrawable(R.drawable.ic_overflow);
					d.setBounds(0, 0, 32, 32);
					ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
					spannabletext.setSpan(span, pre.length(), pre.length() + 1, 0);

					text = spannabletext;
					type = BufferType.SPANNABLE;
				}

				noSensorsNotice.setText(text, type);
				noSensorsNotice.setVisibility(View.VISIBLE);
			} else {
				noSensorsNotice.setVisibility(View.GONE);
				buttonsContainer.setVisibility(View.VISIBLE);
				updateSeriesButtons(rootView);
			}

			getActivity().supportInvalidateOptionsMenu();

			updateChildrenList();
			updateValueCount(rootView);
		}
	}

	private void updateSeriesButtons(View rootView) {
		boolean running = DataLogger.get().isRunning();
		

		rootView.findViewById(R.id.data_logging_start).setEnabled(!running);
		rootView.findViewById(R.id.data_logging_stop).setEnabled(running);

		int series = DataLogger.get().getCurrentSeries();
		String text = "";
		if (series > 0) {
			int resource = running ? R.string.data_logging_series_logging : R.string.data_logging_series_logged;
			text = String.format(getResources().getString(resource), series);
		}
		((TextView)rootView.findViewById(R.id.data_logging_current_series)).setText(text);

	}

	private void updateValueCount(View rootView) {
		TextView textView = (TextView) rootView.findViewById(R.id.data_logging_value_count);

		if (ProfileManager.get().getActiveProfileId() != null) {
			StringBuffer sb = new StringBuffer();
			HashMap<String, Integer> dataCount = DataLogger.get().getCurrentSeriesSampleCount();
			if (dataCount.size() == 0) {
				rootView.findViewById(R.id.profile_data_button_bar).setVisibility(View.GONE);
				sb.append(getResources().getString(R.string.data_count_none));
			} else {
				rootView.findViewById(R.id.profile_data_button_bar).setVisibility(View.VISIBLE);

				for (Entry<String, Integer> entry : dataCount.entrySet()) {
					int count = entry.getValue();
					String text = count == 1 ? getResources().getString(R.string.data_count_one) : String.format(getResources().getString(R.string.data_count_many), count);
					sb.append(SensorWrapperManager.get().getSensor(entry.getKey()).getName()).append(": ").append(text).append("\n");
				}
			}

			textView.setText(sb.toString());
		} else {
			textView.setText("");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView(getView());
		
		SettingsManager.get().registerDirectListener("profiles", this);
		ProfileManager.get().registerDirectListener(this);
		DataLogger.get().registerDataListener(this);
		DataLogger.get().registerStatusListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		SettingsManager.get().unregisterDirectListener("profiles", this);
		ProfileManager.get().unregisterDirectListener(this);
		DataLogger.get().unregisterDataListener(this);
		DataLogger.get().unregisterStatusListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.data_logging, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		boolean activeItems = !ProfileManager.get().activeProfileIsDefault();
		for (int i = 0; i < 2; i++) {
			menu.getItem(0).getSubMenu().getItem(i).setEnabled(activeItems);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_data_logging_edit: {
			Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
			intent.putExtra(Arguments.ARG_PROFILE, ProfileManager.get().getActiveProfileId());
			startActivity(intent);
			return true;
		}

		case R.id.action_data_logging_add_sensor: {
			FragmentManager fm = getChildFragmentManager();
			AddSensorDialogFragment dialog = new AddSensorDialogFragment();
			dialog.show(fm, "add_sensor");
			return true;
		}

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void modelNotificationReceived(String msg) {
		if ("profiles".equals(msg) || ProfileManager.get().profileIdIsActive(msg)) {
			updateView(getView());
		}
	}

	@Override
	public void dataLoggerStatusModified() {
		updateSeriesButtons(getView());
	}

	@Override
	public void dataLoggerDataModified(String msg) {
		updateValueCount(getView());
	}

	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		Vector<Fragment> fragments = new Vector<Fragment>();
		Model profile = ProfileManager.get().getActiveProfile();
		if (profile != null) {
			Vector<Model> profileSensors = profile.getModel("sensors", true).getModels("weight");

			for (Model profileSensor : profileSensors) {
				ProfileSensorFragment fragment = new ProfileSensorFragment();
				Bundle args = new Bundle();
				args.putString(Arguments.ARG_PROFILE, profile.getString("id"));
				args.putString(Arguments.ARG_SENSOR, profileSensor.getString("id"));
				fragment.setArguments(args);
				fragments.add(fragment);
			}

			boolean showCopyFromDefault = ProfileManager.get().activeProfileIsDefault() && profileSensors.size() > 0;
			getView().findViewById(R.id.create_profile_from_default).setVisibility(showCopyFromDefault ? View.VISIBLE : View.GONE);
		}

		return fragments;
	}

	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return child instanceof ProfileSensorFragment;
	}

}