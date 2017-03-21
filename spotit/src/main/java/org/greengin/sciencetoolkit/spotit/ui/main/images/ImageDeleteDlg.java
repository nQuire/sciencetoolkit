package org.greengin.sciencetoolkit.spotit.ui.main.images;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;

import java.io.File;

public class ImageDeleteDlg {

	public static void open(Context context, Model observation, ImageActionListener listener) {
		new ImageDeleteDlgBuilder(context, observation, listener).launch();
	}

	private static class ImageDeleteDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {

		AlertDialog dlg;
		Model observation;
		ImageActionListener listener;

		public ImageDeleteDlgBuilder(Context context, Model observation, ImageActionListener listener) {
			super(context);

			this.listener = listener;
			this.observation = observation;

			setTitle(context.getString(R.string.image_delete_dlg_title));
			
			setMessage(String.format(context.getString(R.string.image_delete_dlg_msg), observation.getString("name")));

			setPositiveButton(context.getResources().getString(R.string.button_label_delete), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				listener.imageDeleted(observation);
			}
			dlg.dismiss();
		}

	}

}
