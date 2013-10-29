package org.greengin.sciencetoolkit.ui.fragments.sensors;

import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SensorListFragment extends Fragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_sensor_list, container, false);

		FragmentManager fragmentManager = getChildFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		List<Fragment> fragments = fragmentManager.getFragments();
		if (fragments != null) {
			for (Fragment fragment : fragmentManager.getFragments()) {
				fragmentTransaction.remove(fragment);
			}
		}

		for (String sensorId : SensorWrapperManager.getInstance().getSensors().keySet()) {
			SensorFragment fragment = new SensorFragment();

			Bundle args = new Bundle();
			args.putString(SensorFragment.ARG_SENSOR, sensorId);
			fragment.setArguments(args);
			fragmentTransaction.add(R.id.sensor_list, fragment);
		}

		fragmentTransaction.commit();

		return rootView;
	}

}