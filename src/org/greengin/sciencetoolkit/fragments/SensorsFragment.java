package org.greengin.sciencetoolkit.fragments;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.sensors.SensorWrapperManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SensorsFragment extends Fragment {
	public SensorsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sensors, container, false);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		for(String sensor : SensorWrapperManager.getInstance().getSensors().keySet()) {
			SensorShortFragment fragment = new SensorShortFragment();
			Bundle args = new Bundle();
			args.putString(SensorShortFragment.ARG_SENSOR, sensor);
			fragment.setArguments(args);
			fragmentTransaction.add(R.id.sensor_list, fragment);
		}

		fragmentTransaction.commit();		

		return rootView;
	}
}