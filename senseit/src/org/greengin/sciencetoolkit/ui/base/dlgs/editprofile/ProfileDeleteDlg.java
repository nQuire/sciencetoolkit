package org.greengin.sciencetoolkit.ui.base.dlgs.editprofile;


import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ProfileDeleteDlg {

	public static void open(Context context, Model profile, ProfileActionListener listener) {
		new ProfileDeleteDlgBuilder(context, profile, listener).launch();
	}

	private static class ProfileDeleteDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {

		AlertDialog dlg;
		ProfileActionListener listener;
		Model profile;
		
		public ProfileDeleteDlgBuilder(Context context, Model profile, ProfileActionListener listener) {
			super(context);

			this.listener = listener;
			this.profile = profile;

			setTitle(context.getString(R.string.delete_profile_sensor_dlg_title));
			setMessage(String.format(context.getString(R.string.delete_profile_sensor_dlg_msg), profile.getString("title")));

			setPositiveButton(context.getResources().getString(R.string.button_label_delete), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				listener.profileDelete(profile);
			}
			dlg.dismiss();
		}

	}

}
