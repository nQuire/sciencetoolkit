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
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.remote.RemoteCapableActivity;
import org.greengin.sciencetoolkit.ui.remote.UploadRemoteAction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SeriesListFragment extends Fragment implements DataLoggerStatusListener {

	static final String[] SIZE_UNITS = new String[] { "B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB" };

	SeriesAdapter adapter;
	SimpleDateFormat sdf;
	String profileId;
	boolean isRemote;
	OnClickListener uploadListener;
	OnClickListener exportListener;
	OnClickListener discardListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.isRemote = ProfileManager.get().get(this.profileId).getBool("is_remote");

		this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);
		this.exportListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() instanceof File) {
					exportSeries((File) v.getTag());
				}
			}
		};

		this.discardListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() instanceof File) {
					deleteSeries((File) v.getTag());
				}
			}
		};

		this.uploadListener = this.isRemote ? new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() instanceof File) {
					uploadSeries((File) v.getTag());
				}
			}
		} : null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		DataLogger.get().registerStatusListener(this);
		this.updateDataList();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		DataLogger.get().unregisterStatusListener(this);
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

			while ((line = reader.readLine()) != null) {
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

	private void uploadSeries(File series) {
		if (isRemote && !DataLogger.get().isSent(profileId, series)) {
			UploadRemoteAction action = new UploadRemoteAction(profileId, series);
			((RemoteCapableActivity) getActivity()).remoteRequest(action);
		}
	}
	
	
	private void deleteSeries(File series) {

		String deleteMsg = String.format(getResources().getString(R.string.series_delete_dlg_msg), series.getName());
		CharSequence styledDeleteMsg = Html.fromHtml(deleteMsg);
		new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.series_delete_dlg_title).setMessage(styledDeleteMsg).setPositiveButton(R.string.delete_dlg_yes, new DeleteAcceptListener(series)).setNegativeButton(R.string.cancel, null).show();
	}

	private void exportSeries(File series) {

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
		
		public void setFiles(File[] files) {
			this.files = files;
			this.notifyDataSetChanged();
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

			ImageButton export = (ImageButton) view.findViewById(R.id.series_data_export);
			ImageButton delete = (ImageButton) view.findViewById(R.id.series_data_delete);
			ImageButton upload = (ImageButton) view.findViewById(R.id.series_data_upload);

			if (newView) {
				export.setOnClickListener(exportListener);
				delete.setOnClickListener(discardListener);
				upload.setOnClickListener(uploadListener);
			}

			export.setTag(f);
			delete.setTag(f);
			
			if (isRemote) {
				upload.setVisibility(View.VISIBLE);
				upload.setTag(f);
				boolean sent = DataLogger.get().isSent(profileId, f);
				upload.setEnabled(true/*!sent*/);
			} else {
				upload.setVisibility(View.GONE);
			}

			TextView title = (TextView) view.findViewById(R.id.series_list_item_title);
			title.setText(f.getName());

			long size = f.length();
			int units = 0;

			while (size > 1024) {
				size /= 1024;
				++units;
			}

			TextView data = (TextView) view.findViewById(R.id.series_list_item_data);
			data.setText(String.format("%s (%d %s)", sdf.format(new Date(f.lastModified())), size, SIZE_UNITS[units]));

			return view;
		}
	}

	private class DeleteAcceptListener implements DialogInterface.OnClickListener {
		File series;

		public DeleteAcceptListener(File series) {
			this.series = series;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			DataLogger.get().deleteData(profileId, series);
		}
	}

	@Override
	public void dataLoggerStatusModified() {
		updateDataList();
	}
	
	private void updateDataList() {
		this.adapter.setFiles(DataLogger.get().getSeries(this.profileId));		
	}

}