package org.greengin.sciencetoolkit.ui.components.main.data;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerDataListener;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.ParentListFragment;
import org.greengin.sciencetoolkit.ui.components.main.data.files.FileManagementActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class DataListFragment extends ParentListFragment implements DataLoggerDataListener, ModelNotificationListener {

	Hashtable<String, DataFragment> dataFragments;

	public DataListFragment() {
		super(R.id.data_list);
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

		Button deleteAll = (Button) rootView.findViewById(R.id.delete_all_data);
		deleteAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_all_data_dlg_title).setMessage(R.string.delete_all_data_dlg_msg).setPositiveButton(R.string.delete_dlg_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DataLogger.getInstance().deleteAllData();
					}
				}).setNegativeButton(R.string.cancel, null).show();
			}
		});

		updateChildrenList();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateChildrenList();
		DataLogger.getInstance().registerDataListener(this);
		ProfileManager.getInstance().registerDirectListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		DataLogger.getInstance().unregisterDataListener(this);
		ProfileManager.getInstance().unregisterDirectListener(this);
	}

	@Override
	public void dataLoggerDataModified(String msg) {
		if ("all".equals(msg)) {
			for (DataFragment fragment : dataFragments.values()) {
				fragment.dataModified();
			}
		} else if (dataFragments.containsKey(msg)) {
			dataFragments.get(msg).dataModified();
		}
	}

	@Override
	public void modelNotificationReceived(String msg) {
		if ("list".equals(msg)) {
			updateChildrenList();
		}
	}

	@Override
	protected List<Fragment> getUpdatedFragmentChildren() {
		dataFragments.clear();

		Vector<Fragment> fragments = new Vector<Fragment>();

		for (String profileId : ProfileManager.getInstance().getProfileIds()) {
			DataFragment fragment = new DataFragment();
			Bundle args = new Bundle();
			args.putString(Arguments.ARG_PROFILE, profileId);
			fragment.setArguments(args);
			fragments.add(fragment);
			dataFragments.put(profileId, fragment);
		}

		return fragments;
	}
	
	@Override
	protected boolean removeChildFragmentOnUpdate(Fragment child) {
		return child instanceof DataFragment;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.data, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_data_file_management: {
			Intent intent = new Intent(getActivity(), FileManagementActivity.class);
	    	startActivity(intent);
		}
		}

		return super.onOptionsItemSelected(item);
	}


}