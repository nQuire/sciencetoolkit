package org.greengin.sciencetoolkit.ui.dataviewer;

import java.io.File;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.widgets.colorview.ColorView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class SeriesListAdapter extends BaseAdapter {

	SeriesListListener listener;
	LayoutInflater inflater;

	Model profile;
	boolean isRemote = false;
	File[] seriesList;
	Hashtable<String, Integer> seriesIndex;

	OnClickListener editTitleListener;
	OnClickListener uploadListener;
	OnClickListener discardListener;
	OnClickListener toggleListener;

	public SeriesListAdapter(Context context, String profileId, SeriesListListener listener, LayoutInflater inflater) {
		this.listener = listener;
		this.inflater = inflater;
		this.profile = ProfileManager.get().get(profileId);
		this.seriesIndex = new Hashtable<String, Integer>();

		this.editTitleListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesEdit(profile, (File) v.getTag());
			}
		};
		
		this.uploadListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesUpload(profile, (File) v.getTag());
			}
		};

		this.discardListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesDelete(profile, (File) v.getTag());
			}
		};
		
		this.toggleListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesListAdapter.this.listener.seriesToggled(profile, (File) v.getTag());
			}
		};

		updateSeriesList(false);
	}

	public void updateSeriesList() {
		updateSeriesList(true);
	}

	private int getAvailableColorIndex() {
		for (int i = 0;; i++) {
			boolean available = true;
			for (Entry<String, Integer> entry : seriesIndex.entrySet()) {
				if (entry.getValue() == i) {
					available = false;
					break;
				}
			}
			if (available) {
				return i;
			}
		}
	}

	public void updateSeriesList(boolean notify) {
		this.isRemote = profile.getBool("is_remote");
		this.seriesList = DataLogger.get().getSeries(profile.getString("id"));
		Model seriesContainerModel = profile.getModel("series", true);

		for (File f : seriesList) {
			boolean enabled = seriesContainerModel.getModel(f.getName(), true).getBool("dataviewershow", true);
			if (enabled) {
				Integer value = seriesIndex.get(f.getName());
				if (value == null || value.intValue() < 0) {
					seriesIndex.put(f.getName(), getAvailableColorIndex());
				}
			} else {
				seriesIndex.put(f.getName(), -1);
			}
		}

		if (notify) {
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return seriesList != null ? seriesList.length : 0;
	}

	@Override
	public File getItem(int position) {
		return seriesList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		File series = seriesList[position];
		Model seriesModel = profile.getModel("series", true).getModel(series.getName(), true, true);

		int seriesUploadStatus = seriesModel.getInt("uploaded", 0);
		String seriesName = seriesModel.getString("title", series.getName());

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_series_list_item, parent, false) : convertView;

		TextView seriesNameView = (TextView) view.findViewById(R.id.series_name);
		seriesNameView.setText(seriesName);
		seriesNameView.setTag(series);
		if (newView) {
			seriesNameView.setOnClickListener(editTitleListener);
		}

		ImageButton uploadButton = (ImageButton) view.findViewById(R.id.series_upload);

		uploadButton.setTag(series);
		if (newView) {
			uploadButton.setOnClickListener(uploadListener);
		}

		if (isRemote) {
			uploadButton.setEnabled(seriesUploadStatus == 0);
			uploadButton.setVisibility(View.VISIBLE);
		} else {
			uploadButton.setVisibility(View.GONE);
		}

		ImageButton discardButton = (ImageButton) view.findViewById(R.id.series_discard);
		discardButton.setTag(series);
		if (newView) {
			discardButton.setOnClickListener(discardListener);
		}
		
		ColorView toggleButton = (ColorView) view.findViewById(R.id.series_selected);
		toggleButton.setColorIndex(seriesIndex.get(series.getName()));
		toggleButton.setTag(series);
		if (newView) {
			toggleButton.setOnClickListener(toggleListener);
		}
		
		return view;
	}

}