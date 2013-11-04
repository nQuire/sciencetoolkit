package org.greengin.sciencetoolkit.ui.components.main.data;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class DataFragment extends Fragment {
	public static final String ARG_PROFILE = "profile";

	private String profileId;
	private Model profile;

	private int dataCount;

	BroadcastReceiver valueReceiver;
	ModelNotificationListener periodListener;

	public DataFragment() {
		dataCount = -1;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.profileId = getArguments().getString(ARG_PROFILE);
		this.profile = ProfileManager.getInstance().get(this.profileId);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_data, container, false);

		TextView nameTextView = (TextView) rootView.findViewById(R.id.profile_name);
		nameTextView.setText(this.profile.getString("title"));

		this.updateView(rootView);

		ImageButton discardButton = (ImageButton) rootView.findViewById(R.id.profile_data_discard);
		discardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (profileId != null) {
					String deleteMsg = String.format(getResources().getString(R.string.delete_profile_data_dlg_msg), profile.getString("title"));
					CharSequence styledDeleteMsg = Html.fromHtml(deleteMsg);
					new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_profile_data_dlg_title).setMessage(styledDeleteMsg).setPositiveButton(R.string.delete_data_dlg_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DataLogger.getInstance().deleteData(profileId);
						}
					}).setNegativeButton(R.string.cancel, null).show();
				}
			}
		});
		
		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void updateView(View view) {
		updateValueView(view);
	}

	private boolean updateDataCount() {
		int count = DataLogger.getInstance().getSampleCount(this.profileId);
		if (count != this.dataCount) {
			this.dataCount = count;
			return true;
		} else {
			return false;
		}
	}

	public void dataModified() {
		updateValueView(getView());
	}

	private void updateValueView(View view) {
		if (updateDataCount()) {
			String text;

			switch (dataCount) {
			case 0:
				text = getResources().getString(R.string.data_count_none);
				break;
			case 1:
				text = getResources().getString(R.string.data_count_one);
				break;
			default:
				text = String.format(getResources().getString(R.string.data_count_many), dataCount);
				break;
			}

			((TextView) view.findViewById(R.id.profile_data)).setText(text);
		}
	}

}