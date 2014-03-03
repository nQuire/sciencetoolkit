package org.greengin.sciencetoolkit.ui.base.dlgs.editprofilesensor;


import java.io.File;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.dataviewer.SeriesListFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class SeriesDeleteDlg {

	public static void open(Context context, Model profile, File series, SeriesActionListener listener) {
		new SeriesDeleteDlgBuilder(context, profile, series, listener).launch();
	}

	private static class SeriesDeleteDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {

		AlertDialog dlg;
		Model profile;
		File series;
		SeriesActionListener listener;

		public SeriesDeleteDlgBuilder(Context context, Model profile, File series, SeriesActionListener listener) {
			super(context);

			this.listener = listener;
			this.profile = profile;
			this.series = series;

			String seriesName = SeriesListFragment.seriesName(profile, series);
			setTitle(context.getString(R.string.series_delete_dlg_title));
			
			setMessage(String.format(context.getString(R.string.series_delete_dlg_msg), seriesName));

			setPositiveButton(context.getResources().getString(R.string.button_label_delete), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				listener.seriesDeleted(profile, series);
			}
			dlg.dismiss();
		}

	}

}
