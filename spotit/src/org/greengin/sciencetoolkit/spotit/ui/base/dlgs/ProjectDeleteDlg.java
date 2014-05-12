package org.greengin.sciencetoolkit.spotit.ui.base.dlgs;


import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.spotit.R;
import org.greengin.sciencetoolkit.spotit.ui.main.projects.ProjectItemEventListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ProjectDeleteDlg {

	public static void open(Context context, Model project, ProjectItemEventListener listener) {
		new ProfileDeleteDlgBuilder(context, project, listener).launch();
	}

	private static class ProfileDeleteDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {

		AlertDialog dlg;
		ProjectItemEventListener listener;
		Model project;
		
		public ProfileDeleteDlgBuilder(Context context, Model project, ProjectItemEventListener listener) {
			super(context);

			this.listener = listener;
			this.project = project;

			setTitle(context.getString(R.string.delete_project_dlg_title));
			setMessage(String.format(context.getString(R.string.delete_project_dlg_msg), project.getString("title")));

			setPositiveButton(context.getResources().getString(R.string.button_label_delete), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				listener.projectDelete(project);
			}
			dlg.dismiss();
		}
	}

}
