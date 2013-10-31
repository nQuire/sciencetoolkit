package org.greengin.sciencetoolkit.ui.components.main.datalogging;


import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.NotificationListener;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class DataLoggingFragment extends Fragment implements NotificationListener {

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
				
				FragmentManager fragmentManager = getChildFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				List<Fragment> fragments = fragmentManager.getFragments();
				if (fragments != null) {
					for (Fragment fragment : fragments) {
						fragmentTransaction.remove(fragment);
					}
				}
				fragmentTransaction.commit();
				
				TextView noSensorsNotice = (TextView)rootView.findViewById(R.id.no_sensors_notice);
				Vector<Model> sensors = profile.getModel("sensors", true).getModels("weight");
				
				if (sensors.size() == 0) {
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
						fragmentManager.beginTransaction().add(R.id.sensor_list, fragment).commit();
					}
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView(getView());
		ProfileManager.getInstance().registerDirectListener(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		ProfileManager.getInstance().unregisterDirectListener(this);
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
	public void notificationReveiced(String msg) {
		updateView(getView());
	}

}