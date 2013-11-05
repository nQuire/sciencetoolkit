package org.greengin.sciencetoolkit.ui.components.main.datalogging.switchprofile;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;

public class SwitchProfileFragment extends Fragment {
	public static final String ARG_PROFILE = "profile";

	private String profileId;
	private Model profile;
	
	private RadioButton radio;

	ModelNotificationListener periodListener;

	public SwitchProfileFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.profileId = getArguments().getString(ARG_PROFILE);
		this.profile = ProfileManager.getInstance().get(this.profileId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_profile_switch, container, false);
		
		radio = (RadioButton) rootView.findViewById(R.id.profile_switch);
		radio.setText(this.profile.getString("title"));

		this.updateView(rootView);

		ImageButton editButton = (ImageButton) rootView.findViewById(R.id.profile_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*if (profileId != null) {
					Intent intent = new Intent(getActivity(), DataViewActivity.class);
					intent.putExtra("profile", profileId);
					startActivity(intent);
				}*/
			}
		});

		ImageButton discardButton = (ImageButton) rootView.findViewById(R.id.profile_discard);
		discardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*if (profileId != null) {
					String deleteMsg = String.format(getResources().getString(R.string.delete_profile_data_dlg_msg), profile.getString("title"));
					CharSequence styledDeleteMsg = Html.fromHtml(deleteMsg);
					new AlertDialog.Builder(v.getContext()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.delete_profile_data_dlg_title).setMessage(styledDeleteMsg).setPositiveButton(R.string.delete_data_dlg_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DataLogger.getInstance().deleteData(profileId);
						}
					}).setNegativeButton(R.string.cancel, null).show();
				}*/
			}
		});

		return rootView;
	}

	private void updateView(View view) {
		if (this.profileId.equals(ProfileManager.getInstance().getActiveProfileId())) {
			
		} else {
			
		}
	}
}
