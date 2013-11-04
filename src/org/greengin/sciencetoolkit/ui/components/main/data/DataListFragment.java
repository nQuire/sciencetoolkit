package org.greengin.sciencetoolkit.ui.components.main.data;

import java.util.Hashtable;
import java.util.List;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerListener;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DataListFragment extends Fragment implements DataLoggerListener {


	Hashtable<String, DataFragment> dataFragments;
	
	public DataListFragment() {
		dataFragments = new Hashtable<String, DataFragment>();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_data_list, container, false);
		
		Button deleteAll = (Button)rootView.findViewById(R.id.delete_all_data);
		deleteAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_all_data_dlg_title).setMessage(R.string.delete_all_data_dlg_msg).setPositiveButton(R.string.delete_data_dlg_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataLogger.getInstance().deleteAllData();
					}
				}).setNegativeButton(R.string.cancel, null).show();
			}
		});
		
		updateView(rootView);

		return rootView;
	}

	private void updateView(View rootView) {
		if (rootView != null) {
			dataFragments.clear();
			
			List<Fragment> fragments = getChildFragmentManager().getFragments();
			if (fragments != null) {
				for (Fragment fragment : fragments) {
					getChildFragmentManager().beginTransaction().remove(fragment).commit();
				}
			}

			for (String profileId : ProfileManager.getInstance().getProfileIds()) {
				DataFragment fragment = new DataFragment();
				Bundle args = new Bundle();
				args.putString(DataFragment.ARG_PROFILE, profileId);
				fragment.setArguments(args);
				getChildFragmentManager().beginTransaction().add(R.id.data_list, fragment).commit();
				
				dataFragments.put(profileId, fragment);
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateView(getView());
		DataLogger.getInstance().registerListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		DataLogger.getInstance().unregisterListener(this);
	}

	@Override
	public void dataLoggerDataModified(String msg) {
		if ("all".equals(msg)) {
			for(DataFragment fragment : dataFragments.values()) {
				fragment.dataModified();
			}
		} else if (dataFragments.containsKey(msg)) {
			dataFragments.get(msg).dataModified();
		}		
	}

}