package org.greengin.sciencetoolkit.ui.fragments.sensors;

import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.settings.Settings;
import org.greengin.sciencetoolkit.settings.SettingsManager;
import org.greengin.sciencetoolkit.ui.activities.SensorListSettingsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SensorListFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_sensor_list, container, false);

		updateView(rootView);

		return rootView;
	}

	private void updateView(View rootView) {
		if (rootView != null) {
			FragmentManager fragmentManager = getChildFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			List<Fragment> fragments = fragmentManager.getFragments();
			if (fragments != null) {
				for (Fragment fragment : fragmentManager.getFragments()) {
					fragmentTransaction.remove(fragment);
				}
			}
			fragmentTransaction.commit();

			Settings showSensors = SettingsManager.getInstance().get("sensor_list");
			for (String sensorId : SensorWrapperManager.getInstance().getSensorsIds()) {
				if (showSensors.getBool(sensorId, true)) {
					SensorFragment fragment = new SensorFragment();
					Bundle args = new Bundle();
					args.putString(SensorFragment.ARG_SENSOR, sensorId);
					fragment.setArguments(args);
					fragmentManager.beginTransaction().add(R.id.sensor_list, fragment).commit();
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView(getView());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sensor_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_sensor_list_show:
			Intent intent = new Intent(getActivity(), SensorListSettingsActivity.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}