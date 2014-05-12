package org.greengin.sciencetoolkit.ui.dataviewer;

import java.io.File;
import java.util.ArrayList;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.common.ui.base.RemoteCapableActivity;
import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.remote.UploadRemoteAction;
import org.greengin.sciencetoolkit.ui.base.SenseItArguments;
import org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor.SeriesActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor.SeriesDeleteDlg;
import org.greengin.sciencetoolkit.ui.base.dlgs.edittext.EditTextActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.edittext.EditTextDlg;
import org.greengin.sciencetoolkit.ui.base.events.SenseItEventFragment;
import org.greengin.sciencetoolkit.ui.base.events.SenseItEventManagerListener;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SeriesListFragment extends SenseItEventFragment implements SeriesListListener, SeriesActionListener {

	SeriesListAdapter adapter;
	String profileId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.profileId = getArguments().getString(SenseItArguments.ARG_PROFILE);

		eventManager.setListener(new EventListener());
		eventManager.listenToLoggerStatus();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.view_series_list, container, false);

		if (this.profileId != null) {
			adapter = new SeriesListAdapter(this.getActivity(), profileId, this, inflater);
			ListView list = (ListView) rootView.findViewById(R.id.series_list);
			list.setAdapter(adapter);
		}

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.data_viewer, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		}

		return super.onOptionsItemSelected(item);
	}

	private class EventListener extends SenseItEventManagerListener {
		@Override
		public void eventDataStatus(String event, boolean whilePaused) {
			adapter.updateSeriesList();
		}
	}

	@Override
	public void seriesDelete(Model profile, File series) {
		SeriesDeleteDlg.open(getActivity(), profile, series, this);
	}

	@Override
	public void seriesDeleted(Model profile, File series) {
		if (series.getName().equals(profile.getModel("dataviewer", true).getString("series"))) {
			profile.getModel("dataviewer", true).clear("series");
		}

		DataLogger.get().deleteData(profile.getString("id"), series);
	}

	@Override
	public void seriesUpload(Model profile, File series) {
		((RemoteCapableActivity) getActivity()).remoteRequest(new UploadRemoteAction(profile, series));
	}

	@Override
	public void seriesEdit(Model profile, File series) {
		String seriesName = DataLogger.get().seriesName(profile, series);
		EditTextDlg.open(this.getActivity(), R.string.series_edit_name_title, R.string.series_edit_name_msg, R.string.button_label_set, seriesName, true, new EditSeriesTitleManager(profile, series));
	}

	@Override
	public void seriesSelected(Model profile, File series) {
		((DataViewerActivity) getActivity()).getSupportActionBar().setSelectedNavigationItem(1);
	}

	private class EditSeriesTitleManager implements EditTextActionListener {
		Model profile;
		File series;

		public EditSeriesTitleManager(Model profile, File series) {
			this.profile = profile;
			this.series = series;
		}

		@Override
		public void editTextComplete(String value) {
			if (value != null) {
				Model seriesModel = profile.getModel("series", true).getModel(series.getName(), true, true);
				seriesModel.setString("title", value);
				adapter.updateSeriesList();
			}
		}

	}

	
	@Override
	public void seriesShare(Model profile, File series) {
		File export = DataLogger.get().getPublicFile(profile, series);
		ArrayList<Uri> uris = new ArrayList<Uri>();
		if (export != null) {
			uris.add(Uri.fromFile(export));
		}

		if (uris.size() > 0) {
			String shareMenuTitle = getResources().getString(R.string.series_share_title);
			String subject = String.format(getResources().getString(R.string.series_share_subject), DataLogger.get().seriesName(profile, series));
			String body = getResources().getString(R.string.series_share_body);

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
			sendIntent.setType("plain/text");
			sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {});
			sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			ArrayList<String> bodyContent = new ArrayList<String>();
			bodyContent.add(body);
			sendIntent.putExtra(Intent.EXTRA_TEXT, bodyContent);

			sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			startActivity(Intent.createChooser(sendIntent, shareMenuTitle));
		}
	}

}