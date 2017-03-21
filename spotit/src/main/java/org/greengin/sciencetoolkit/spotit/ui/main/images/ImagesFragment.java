package org.greengin.sciencetoolkit.spotit.ui.main.images;

import java.util.List;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.logic.remote.UploadRemoteAction;
import org.greengin.sciencetoolkit.spotit.ui.base.SpotItBaseFragment;
import org.greengin.sciencetoolkit.spotit.ui.base.events.SpotItEventManagerListener;
import org.greengin.sciencetoolkit.spotit.ui.main.MainActivity;
import org.greengin.sciencetoolkit.spotit.ui.remote.SpotItProjectBrowserActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class ImagesFragment extends SpotItBaseFragment implements ImageListener, ImageActionListener {

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

		View rootView = inflater.inflate(R.layout.view_images, container,
				false);

		this.adapter = new ImagesGridAdapter(inflater, this);

		GridView list = (GridView) rootView.findViewById(R.id.image_list);
		list.setAdapter(adapter);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuResource = R.menu.projects_logged_out;
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
		@Override
		public void eventsNewData(List<String> dataEvents, boolean whilePaused) {
			adapter.updateData();
		}
	}

	@Override
	public void imageUpload(Model observation) {
		((MainActivity) getActivity()).remoteRequest(new UploadRemoteAction(getActivity(), observation));		
	}

	@Override
	public void imageDelete(Model observation) {
		ImageDeleteDlg.open(getActivity(), observation, this);
	}



	@Override
	public void imageDeleted(Model observation) {
		return;
	}
	
}
