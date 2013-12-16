package org.greengin.sciencetoolkit.ui.components.main.profiles.view;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.ui.Arguments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SeriesListFragment extends Fragment {
	
	static final String[] SIZE_UNITS = new String[]{"B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB"};
	
	SeriesAdapter adapter;
	SimpleDateFormat sdf;
	String profileId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_series_list, container, false);

		this.adapter = new SeriesAdapter(this.getActivity(), inflater, DataLogger.get().getSeries(this.profileId));

		ListView list = (ListView) rootView.findViewById(R.id.series_list);
		list.setAdapter(this.adapter);

		return rootView;
	}

	private class SeriesAdapter extends BaseAdapter {
		File[] files;
		FragmentActivity activity;
		LayoutInflater inflater;

		public SeriesAdapter(FragmentActivity activity, LayoutInflater inflater, File[] files) {
			this.files = files;
			this.activity = activity;
			this.inflater = inflater;
		}

		@Override
		public int getCount() {
			return files != null ? files.length : 0;
		}

		@Override
		public File getItem(int position) {
			return files[position];
		}

		@Override
		public long getItemId(int position) {
			File f = files[position];
			return Long.parseLong(f.getName().split("\\.")[0]);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView != null ? convertView : inflater.inflate(R.layout.fragment_series_list_item, null);
			
			File f = files[position];
			
			TextView title = (TextView)view.findViewById(R.id.series_list_item_title);
			title.setText(f.getName());
			
			long size = f.length();
			int units = 0;
			
			while(size > 1024) {
				size /= 1024;
				++units;
			}
			
			String dataText = String.format("%s (%d %s)", sdf.format(new Date(f.lastModified())), size, SIZE_UNITS[units]);
			TextView data = (TextView)view.findViewById(R.id.series_list_item_data);
			data.setText(dataText);
			
			
			return view;
		}
	}

}