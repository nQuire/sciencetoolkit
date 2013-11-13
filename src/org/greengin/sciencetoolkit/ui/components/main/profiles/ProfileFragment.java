package org.greengin.sciencetoolkit.ui.components.main.profiles;

import java.io.File;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerDataListener;
import org.greengin.sciencetoolkit.logic.datalogging.DataLoggerStatusListener;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.components.main.data.view.DataViewActivity;
import org.greengin.sciencetoolkit.ui.components.main.profiles.edit.ProfileEditActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProfileFragment extends Fragment implements DataLoggerStatusListener, DataLoggerDataListener, ModelNotificationListener {
	private String profileId;
	private Model profile;

	private RadioButton radio;
	private ImageButton discardButton;

	private int dataCount;

	public ProfileFragment() {
		dataCount = -1;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.profile = ProfileManager.getInstance().get(this.profileId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

		radio = (RadioButton) rootView.findViewById(R.id.profile_switch);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Intent intent = new Intent(ProfileListFragment.REQUEST_SELECTED_PROFILE);
					intent.putExtra(Arguments.ARG_PROFILE, profileId);
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
				}
			}
		});

		updateRadioChecked(ProfileManager.getInstance().getActiveProfileId());

		ImageButton dataViewButton = (ImageButton) rootView.findViewById(R.id.profile_data_view);
		dataViewButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (profileId != null) {
					Intent intent = new Intent(getActivity(), DataViewActivity.class);
					intent.putExtra(Arguments.ARG_PROFILE, profileId);
					startActivity(intent);
				}
			}
		});

		ImageButton dataExportButton = (ImageButton) rootView.findViewById(R.id.profile_data_export);
		dataExportButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (profileId != null) {
					File exportFile = DataLogger.getInstance().exportData(profileId);
					if (exportFile != null) {
						String exportMsg = String.format(getResources().getString(R.string.export_data_dlg_msg), profile.getString("title"), exportFile.getAbsolutePath());
						CharSequence styledExportMsg = Html.fromHtml(exportMsg);
						new AlertDialog.Builder(v.getContext()).setIcon(R.drawable.ic_action_save).setTitle(R.string.export_data_dlg_title).setMessage(styledExportMsg).setPositiveButton(R.string.export_data_dlg_yes, new ShareClickListener(profile.getString("title"), exportFile)).setNegativeButton(R.string.export_data_dlg_no, null).show();
					}
				}
			}
		});

		ImageButton dataDiscardButton = (ImageButton) rootView.findViewById(R.id.profile_data_discard);
		dataDiscardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (profileId != null) {
					String deleteMsg = String.format(getResources().getString(R.string.delete_profile_data_dlg_msg), profile.getString("title"));
					CharSequence styledDeleteMsg = Html.fromHtml(deleteMsg);
					new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_profile_data_dlg_title).setMessage(styledDeleteMsg).setPositiveButton(R.string.delete_dlg_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DataLogger.getInstance().deleteData(profileId);
						}
					}).setNegativeButton(R.string.cancel, null).show();
				}
			}
		});

		ImageButton editButton = (ImageButton) rootView.findViewById(R.id.profile_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
				intent.putExtra(Arguments.ARG_PROFILE, profileId);
				startActivity(intent);
			}
		});

		discardButton = (ImageButton) rootView.findViewById(R.id.profile_discard);
		discardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (profileId != null) {
					String deleteMsg = String.format(getResources().getString(R.string.delete_profile_dlg_msg), profile.getString("title"));
					CharSequence styledDeleteMsg = Html.fromHtml(deleteMsg);
					new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_profile_dlg_title).setMessage(styledDeleteMsg).setPositiveButton(R.string.delete_dlg_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ProfileManager.getInstance().deleteProfile(profileId);
						}
					}).setNegativeButton(R.string.cancel, null).show();
				}
			}
		});

		return rootView;
	}

	public void onResume() {
		super.onResume();

		updateView();

		SettingsManager.getInstance().registerDirectListener("profiles", this);
		ProfileManager.getInstance().registerDirectListener(this);
		DataLogger.getInstance().registerStatusListener(this);
		DataLogger.getInstance().registerDataListener(this);
	}

	public void onPause() {
		super.onPause();

		SettingsManager.getInstance().unregisterDirectListener("profiles", this);
		ProfileManager.getInstance().unregisterDirectListener(this);
		DataLogger.getInstance().unregisterStatusListener(this);
		DataLogger.getInstance().unregisterDataListener(this);
	}

	private void updateView() {
		View view = getView();
		updateValueView(view);
		updateRadioView(view);
		updateTitleView(view);
		updateStatusView(view);
		updateDiscardView(view);
	}

	private void updateRadioView(View view) {
		radio.setEnabled(!DataLogger.getInstance().isRunning());
	}

	private void updateTitleView(View view) {
		radio.setText(this.profile.getString("title"));
	}

	private void updateStatusView(View view) {
		if (view != null) {
			int text = this.profileId.equals(ProfileManager.getInstance().getActiveProfileId()) ? (DataLogger.getInstance().isRunning() ? R.string.switch_profile_running : R.string.switch_profile_active) : R.string.switch_profile_inactive;
			((TextView) view.findViewById(R.id.profile_status)).setText(text);
		}
	}

	private void updateDiscardView(View view) {
		if (view != null) {
			boolean canDiscard = ProfileManager.getInstance().profileCount() > 1 && !profileId.equals(ProfileManager.getInstance().getActiveProfileId());
			discardButton.setEnabled(canDiscard);
			discardButton.setVisibility(canDiscard ? View.VISIBLE : View.GONE);
		}
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

	private class ShareClickListener implements DialogInterface.OnClickListener {
		File share;
		String profileTitle;

		public ShareClickListener(String profileTitle, File share) {
			this.profileTitle = profileTitle;
			this.share = share;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String shareMenuTitle = getResources().getString(R.string.export_menu_title);
			String subject = String.format(getResources().getString(R.string.export_payload_subject), profileTitle);
			String body = getResources().getString(R.string.export_payload_body);

			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.setType("*/*");
			sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {});
			sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			sendIntent.putExtra(Intent.EXTRA_TEXT, body);
			sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(share));
			startActivity(Intent.createChooser(sendIntent, shareMenuTitle));
		}
	}

	@Override
	public void dataLoggerStatusModified() {
		updateRadioView(getView());
		updateStatusView(getView());
	}

	@Override
	public void dataLoggerDataModified(String msg) {
		if ("all".equals(msg) || this.profileId.equals(msg)) {
			updateValueView(getView());
		}
	}

	public void updateRadioChecked(String activeProfileId) {
		radio.setChecked(profileId.equals(activeProfileId));
	}

	@Override
	public void modelNotificationReceived(String msg) {
		if (this.profileId.equals(msg)) {
			updateTitleView(getView());
		} else if ("profiles".equals(msg)) {
			View view = getView();
			updateStatusView(view);
			updateDiscardView(view);
		}
	}

}
