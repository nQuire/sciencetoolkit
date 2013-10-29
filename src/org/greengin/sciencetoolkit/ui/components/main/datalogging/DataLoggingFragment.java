package org.greengin.sciencetoolkit.ui.components.main.datalogging;


import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DataLoggingFragment extends Fragment {

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
		inflater.inflate(R.menu.data_logging, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		}

		return super.onOptionsItemSelected(item);
	}

}