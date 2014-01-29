package org.greengin.sciencetoolkit.ui.main.explore;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;
import org.greengin.sciencetoolkit.ui.base.modelconfig.settings.LivePlotSettingsFragment;
import org.greengin.sciencetoolkit.ui.base.plot.LiveXYSensorPlotFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ExploreFragment extends EventFragment implements OnItemClickListener  {
	
	ExploreSensorListAdapter adapter;
	LiveXYSensorPlotFragment fragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		eventManager.setListener(new EventListener());
		eventManager.listenToSettings("sensor_list");
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.view_explore_sensors, container, false);
		adapter = new ExploreSensorListAdapter(inflater);
		GridView grid = (GridView) rootView.findViewById(R.id.sensor_list);
		grid.setAdapter(adapter);
		
		fragment = new LiveXYSensorPlotFragment();
		getChildFragmentManager().beginTransaction().add(R.id.explore_view, fragment).addToBackStack(null).commit();
	
		
		grid.setOnItemClickListener(this);
		
		return rootView;
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
	
	
	private class EventListener extends EventManagerListener {		
		@Override
		public void eventSetting(String settingsId, boolean whilePaused) {
			if ("sensor_list".equals(settingsId)) {
				adapter.updateSensorList();
			}
		}		
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String sensorId = (String)view.getTag(); 
		if (ProfileManager.get().activeProfileIsDefault()) {
			ProfileManager.get().addSensorToActiveProfile(sensorId);
		}
		fragment.open(sensorId);
	}

}