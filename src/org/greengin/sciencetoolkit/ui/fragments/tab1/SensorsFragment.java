package org.greengin.sciencetoolkit.ui.fragments.tab1;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SensorsFragment extends Fragment {

	public SensorsFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("stk sf", "on create");

		View rootView = inflater.inflate(R.layout.fragment_sensors, container, false);

		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		for (String sensorId : SensorWrapperManager.getInstance().getSensors().keySet()) {
			SensorShortFragment fragment = new SensorShortFragment();

			Bundle args = new Bundle();
			args.putString(SensorShortFragment.ARG_SENSOR, sensorId);
			fragment.setArguments(args);
			fragmentTransaction.add(R.id.sensor_list, fragment);
		}

		fragmentTransaction.commit();

		return rootView;
	}

	public void onDestroyView() {
		super.onDestroyView();
	}

	public void onDetach() {
		super.onDetach();
		Log.d("stk sf", "on detach");
	}

}