package org.greengin.sciencetoolkit.ui.base.dlgs;

import java.io.File;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ResetUploadStatusDlg {

	public static void open(Context context, Model profile, File series) {
		new ResetUploadStatusDlgBuilder(context, profile, series).launch();
	}

	private static class ResetUploadStatusDlgBuilder extends
			AlertDialog.Builder implements DialogInterface.OnClickListener {

		AlertDialog dlg;
		String profileId;
		File series;

		public ResetUploadStatusDlgBuilder(Context context, Model profile,
				File series) {
			super(context);
			this.profileId = profile.getString("id");
			this.series = series;

			setTitle(context.getString(R.string.reset_upload_dlg_title));
			setMessage(context.getString(R.string.reset_upload_dlg_msg));

			setPositiveButton(
					context.getResources().getString(R.string.button_label_ok),
					this);
			setNeutralButton(
					context.getResources().getString(
							R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				DataLogger.get().markAsSent(profileId, series, 0);
			}
			dlg.dismiss();
		}
	}
}