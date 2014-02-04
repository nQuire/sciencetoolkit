package org.greengin.sciencetoolkit.ui.dataviewer;

import java.io.File;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.base.Arguments;
import org.greengin.sciencetoolkit.ui.base.dlgs.edittext.EditTextActionListener;
import org.greengin.sciencetoolkit.ui.base.dlgs.edittext.EditTextDlg;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SeriesListFragment extends EventFragment implements SeriesListListener {

	SeriesListAdapter adapter;
	String profileId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);

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

	private class EventListener extends EventManagerListener {
		@Override
		public void eventDataStatus(String event, boolean whilePaused) {
			adapter.updateSeriesList();
		}
	}

	@Override
	public void seriesDelete(Model profile, File series) {

	}

	@Override
	public void seriesUpload(Model profile, File series) {

	}

	@Override
	public void seriesEdit(Model profile, File series) {
		Model seriesModel = profile.getModel("series", true).getModel(series.getName(), true, true);
		String seriesName = seriesModel.getString("title", series.getName());
		EditTextDlg.open(this.getActivity(), R.string.series_edit_name_title, R.string.series_edit_name_msg, R.string.button_label_set, seriesName, true, new EditSeriesTitleManager(profile, series));
	}

	@Override
	public void seriesToggled(Model profile, File series) {
		Model seriesModel = profile.getModel("series", true).getModel(series.getName(), true, true);
		int index = seriesModel.getInt("dataviewershow", -1);
		seriesModel.setInt("dataviewershow", index < 0 ? adapter.getAvailableColorIndex() : -1);
		adapter.updateSeriesList();
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

}