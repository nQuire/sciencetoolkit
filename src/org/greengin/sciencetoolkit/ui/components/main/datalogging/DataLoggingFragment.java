package org.greengin.sciencetoolkit.ui.components.main.datalogging;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerListener;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.components.main.datalogging.edit.DataLoggingEditActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class DataLoggingFragment extends Fragment implements ModelNotificationListener, DataLoggerListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_data_logging, container, false);

		updateView(rootView);
		
		rootView.findViewById(R.id.data_logging_start).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DataLogger.getInstance().start();
				updateButtons(view.getRootView());
			}
		});
		
		rootView.findViewById(R.id.data_logging_stop).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DataLogger.getInstance().stop();
				updateButtons(view.getRootView());
			}
		});
		
		return rootView;
	}
	
	private void updateView(View rootView) {
		if (rootView != null) {
			TextView nameView = (TextView) rootView.findViewById(R.id.current_profile_name);
			Model profile = ProfileManager.getInstance().getActiveProfile();
			if (profile == null) {
				nameView.setText("No profile selected");
			} else {
				nameView.setText(profile.getString("title"));

				List<Fragment> fragments = getChildFragmentManager().getFragments();
				if (fragments != null) {
					for (Fragment fragment : fragments) {
						getChildFragmentManager().beginTransaction().remove(fragment).commit();
					}
				}

				TextView noSensorsNotice = (TextView) rootView.findViewById(R.id.no_sensors_notice);
				Vector<Model> sensors = profile.getModel("sensors", true).getModels("weight");

				View buttonsContainer = rootView.findViewById(R.id.data_logging_buttons);

				if (sensors.size() == 0) {
					buttonsContainer.setVisibility(View.GONE);

					String pre = getResources().getString(R.string.no_sensor_notice_pre);
					String post = getResources().getString(R.string.no_sensor_notice_post);

					SpannableString text = new SpannableString(pre + " " + post);
					Drawable d = getResources().getDrawable(R.drawable.ic_overflow);
					d.setBounds(0, 0, 32, 32);
					ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);

					text.setSpan(span, pre.length(), pre.length() + 1, 0);
					noSensorsNotice.setText(text, BufferType.SPANNABLE);
					noSensorsNotice.setVisibility(View.VISIBLE);
				} else {
					noSensorsNotice.setVisibility(View.GONE);

					Vector<Model> profileSensors = profile.getModel("sensors", true).getModels("weight");
					for (Model profileSensor : profileSensors) {
						ProfileSensorFragment fragment = new ProfileSensorFragment();
						Bundle args = new Bundle();
						args.putString("profile", profile.getString("id"));
						args.putString("sensor", profileSensor.getString("id"));
						fragment.setArguments(args);
						getChildFragmentManager().beginTransaction().add(R.id.sensor_list, fragment).commit();
					}

					buttonsContainer.setVisibility(View.VISIBLE);
					updateButtons(rootView);
					updateValueCount(rootView);
				}
			}
		}
	}

	private void updateButtons(View rootView) {
		boolean running = DataLogger.getInstance().isRunning();
		
		rootView.findViewById(R.id.data_logging_start).setEnabled(!running);
		rootView.findViewById(R.id.data_logging_stop).setEnabled(running);
	}
	

	private void updateValueCount(View rootView) {
		TextView textView = (TextView) rootView.findViewById(R.id.data_logging_value_count);
		StringBuffer sb = new StringBuffer();
		Iterator<Entry<String, Integer>> it = DataLogger.getInstance().getDetailedSampleCount(ProfileManager.getInstance().getActiveProfileId()).entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			sb.append(entry.getKey()).append(": ").append(entry.getValue());
			if (it.hasNext()) {
				sb.append("\n");
			}
		}
		textView.setText(sb.toString());
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView(getView());
		ProfileManager.getInstance().registerDirectListener(this);
		DataLogger.getInstance().registerListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		ProfileManager.getInstance().unregisterDirectListener(this);
		DataLogger.getInstance().unregisterListener(this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.data_logging, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_data_logging_edit:
			Intent intent = new Intent(getActivity(), DataLoggingEditActivity.class);
			intent.putExtra("mode", "edit");
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void modelNotificationReveiced(String msg) {
		updateView(getView());
	}

	@Override
	public void dataLoggerDataModified(String msg) {
		updateValueCount(getView());		
	}

}