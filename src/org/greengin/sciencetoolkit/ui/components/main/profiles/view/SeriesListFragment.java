package org.greengin.sciencetoolkit.ui.components.main.profiles.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.Arguments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SeriesListFragment extends Fragment {

	static final String[] SIZE_UNITS = new String[] { "B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB" };

	SeriesAdapter adapter;
	SimpleDateFormat sdf;
	String profileId;
	OnClickListener exportListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);
		this.exportListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() instanceof File) {
					export((File) v.getTag());
				}
			}
		};
	}
	
	private File tempFile(File internal) {
		String name = internal.getName().split("\\.")[0];
		
		try {
			File outputDir = new File(getActivity().getExternalFilesDir(null), "sciencetoolkit");
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			}
			
			File outputFile = File.createTempFile(profileId + "-" + name + "-", ".csv", outputDir);
			BufferedReader reader = new BufferedReader(new FileReader(internal));
			PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
			
			String line;
			
			while((line = reader.readLine()) != null) {
				writer.print(line);
				writer.print("\n");
			}
			reader.close();
			writer.close();
			return outputFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void export(File series) {
		
		Model profile = ProfileManager.get().get(profileId);
		
		String shareMenuTitle = getResources().getString(R.string.export_menu_title);
		String subject = String.format(getResources().getString(R.string.export_payload_subject), profile.getString("title"));
		String body = getResources().getString(R.string.export_payload_body);
		
		File output = tempFile(series);
		
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.setType("*/*");
		sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {});
		sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		sendIntent.putExtra(Intent.EXTRA_TEXT, body);
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(output));
		startActivity(Intent.createChooser(sendIntent, shareMenuTitle));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_series_list, container, false);

		this.adapter = new SeriesAdapter(inflater, DataLogger.get().getSeries(this.profileId));

		ListView list = (ListView) rootView.findViewById(R.id.series_list);
		list.setAdapter(this.adapter);

		return rootView;
	}

	private class SeriesAdapter extends BaseAdapter {
		File[] files;
		LayoutInflater inflater;

		public SeriesAdapter(LayoutInflater inflater, File[] files) {
			this.files = files;
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
			File f = files[position];
			
			boolean newView = convertView == null;
			View view = newView ? inflater.inflate(R.layout.fragment_series_list_item, null) : convertView;
			
			ImageButton export = (ImageButton)view.findViewById(R.id.series_data_export);
			
			if (newView) {
				export.setOnClickListener(exportListener);
			}
			
			
			export.setTag(f);
			
			TextView title = (TextView)view.findViewById(R.id.series_list_item_title);
			title.setText(f.getName());
			
			long size = f.length();
			int units = 0;
			
			while(size > 1024) {
				size /= 1024;
				++units;
			}
			
			TextView data = (TextView)view.findViewById(R.id.series_list_item_data);
			data.setText(String.format("%s (%d %s)", sdf.format(new Date(f.lastModified())), size, SIZE_UNITS[units]));
			
			return view;
		}
	}

}