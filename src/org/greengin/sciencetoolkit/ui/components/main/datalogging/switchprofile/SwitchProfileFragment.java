package org.greengin.sciencetoolkit.ui.components.main.datalogging.switchprofile;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.ui.components.main.datalogging.edit.DataLoggingEditActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;

public class SwitchProfileFragment extends Fragment implements ModelNotificationListener {
	public static final String ARG_PROFILE = "profile";

	private String profileId;
	private Model profile;

	private RadioButton radio;
	private ImageButton discardButton;

	ModelNotificationListener periodListener;
	SwitchProfileActivity requestListener;

	public SwitchProfileFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		requestListener = (SwitchProfileActivity) activity;

		this.profileId = getArguments().getString(ARG_PROFILE);
		this.profile = ProfileManager.getInstance().get(this.profileId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_profile_switch, container, false);

		radio = (RadioButton) rootView.findViewById(R.id.profile_switch);
		radio.setChecked(this.profileId.equals(ProfileManager.getInstance().getActiveProfileId()));

		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					requestListener.requestSelectedForChange(profileId);
				}
			}
		});

		ImageButton editButton = (ImageButton) rootView.findViewById(R.id.profile_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), DataLoggingEditActivity.class);
				intent.putExtra("profile", profileId);
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

		this.updateView(rootView);

		return rootView;
	}

	private void updateView(View view) {
		updateTitleView(view);
		updateStatusView(view);
		updateDiscardView(view);
	}

	private void updateTitleView(View view) {
		if (view != null) {
			radio.setText(this.profile.getString("title"));
		}
	}

	private void updateStatusView(View view) {
		if (view != null) {
			int visibility = this.profileId.equals(ProfileManager.getInstance().getActiveProfileId()) ? View.VISIBLE : View.GONE;
			view.findViewById(R.id.profile_switch_status).setVisibility(visibility);
		}
	}

	private void updateDiscardView(View view) {
		if (view != null) {
			boolean canDiscard = ProfileManager.getInstance().profileCount() > 1 && !profileId.equals(ProfileManager.getInstance().getActiveProfileId());
			discardButton.setEnabled(canDiscard);
			discardButton.setVisibility(canDiscard ? View.VISIBLE : View.INVISIBLE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		updateView(getView());
		ProfileManager.getInstance().registerDirectListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		ProfileManager.getInstance().unregisterDirectListener(this);
	}

	@Override
	public void modelNotificationReveiced(String msg) {
		if ("switch".equals(msg)) {
			updateStatusView(getView());
			updateDiscardView(getView());
		} else if ("list".equals(msg)) {
			updateDiscardView(getView());
		} else if (this.profileId.equals(msg)) {
			updateTitleView(getView());
		}
	}

	public void setSelectedForChangeProfile(String profileId) {
		radio.setChecked(this.profileId.equals(profileId));
	}
}
