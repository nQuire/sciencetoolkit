package org.greengin.sciencetoolkit.fragments;

import org.greengin.sciencetoolkit.DataManager;
import org.greengin.sciencetoolkit.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DataFragment extends Fragment {

	BroadcastReceiver dataListener;

	public DataFragment() {
		dataListener = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("stkdb", "data fragment updated");
				dataModified();
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_data, container, false);

		updateView(rootView);

		/*
		 * FragmentManager fragmentManager = getFragmentManager();
		 * FragmentTransaction fragmentTransaction =
		 * fragmentManager.beginTransaction();
		 * 
		 * for(String sensor :
		 * SensorWrapperManager.getInstance().getSensors().keySet()) {
		 * SensorShortFragment fragment = new SensorShortFragment(); Bundle args
		 * = new Bundle(); args.putString(SensorShortFragment.ARG_SENSOR,
		 * sensor); fragment.setArguments(args);
		 * fragmentTransaction.add(R.id.sensor_list, fragment); }
		 * 
		 * fragmentTransaction.commit();
		 */

		return rootView;
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);

		LocalBroadcastManager.getInstance(activity.getApplicationContext()).registerReceiver(dataListener, new IntentFilter(DataManager.DATA_MODIFIED));
	}

	public void onDetach() {
		super.onDetach();

		LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(dataListener);
	}

	private void updateView(View view) {
		TextView nameTextView = (TextView) view.findViewById(R.id.data_count);
		nameTextView.setText("data " + DataManager.getInstance().dataCount());
	}

	public void dataModified() {
		updateView(this.getView());
	}

}