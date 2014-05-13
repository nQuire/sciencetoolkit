package org.greengin.sciencetoolkit.spotit.ui.main.images;

import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventFragment;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventManagerListener;
import org.greengin.sciencetoolkit.spotit.ui.remote.SpotItProjectBrowserActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ImagesFragment extends SpotItEventFragment {

	ImagesGridAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		eventManager.setListener(new EventListener());

		eventManager.listenToSettings("profiles");
		eventManager.listenToLoggedData();
		eventManager.listenToProfiles();

		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.view_projects, container,
				false);

		this.adapter = new ImagesGridAdapter(inflater);

		ListView list = (ListView) rootView.findViewById(R.id.project_list);
		list.setAdapter(adapter);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuResource = R.menu.projects;
		inflater.inflate(menuResource, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_application_connect:
			Intent intent = new Intent(getActivity(),
					SpotItProjectBrowserActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private class EventListener extends SpotItEventManagerListener {

	}
}