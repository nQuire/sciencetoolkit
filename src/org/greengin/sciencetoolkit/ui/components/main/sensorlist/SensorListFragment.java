package org.greengin.sciencetoolkit.ui.components.main.sensorlist;

import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.ParentListFragment;
import org.greengin.sciencetoolkit.ui.components.main.sensorlist.choose.SensorListSettingsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class SensorListFragment extends ParentListFragment implements ModelNotificationListener {

	public SensorListFragment() {
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

		View rootView = inflater.inflate(R.layout.fragment_sensor_list, container, false);

		updateChildrenList();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateChildrenList();
		SettingsManager.get().registerDirectListener("sensor_list", this);
	}

	public void onPause() {
		super.onPause();
		SettingsManager.get().unregisterDirectListener("sensor_list", this);
	}
	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.sensor_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_sensor_list_show: {
			Intent intent = new Intent(getActivity(), SensorListSettingsActivity.class);
			startActivity(intent);
			break;
		}

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		Vector<Fragment> fragments = new Vector<Fragment>();

		Model showSensors = SettingsManager.get().get("sensor_list");

		for (String sensorId : SensorWrapperManager.getInstance().getSensorsIds()) {
			if (showSensors.getBool(sensorId, true)) {
				SensorFragment fragment = new SensorFragment();
				Bundle args = new Bundle();
				args.putString(Arguments.ARG_SENSOR, sensorId);
				fragment.setArguments(args);
				fragments.add(fragment);
			}
		}

		return fragments;
	}

	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return child instanceof SensorFragment;
	}

	@Override
	public void modelNotificationReceived(String msg) {
		updateChildrenList();
	}

}