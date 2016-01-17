package org.greengin.sciencetoolkit.ui.base.dlgs.editprofile;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class ProfileDlg {

	public static void open(Context context, Model profile, boolean canDelete, ProfileActionListener listener) {
		new ProfileSensorDlgBuilder(context, profile, canDelete, listener).launch();
	}

	private static class ProfileSensorDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener, TextWatcher {

		Model profile;
		EditText editText;
		AlertDialog dlg;
		ProfileActionListener listener;
		boolean canDelete;
		boolean isRemote;
		Context context;

		public ProfileSensorDlgBuilder(Context context, Model profile, boolean canDelete, ProfileActionListener listener) {
			super(context);

			this.listener = listener;
			this.profile = profile;
			this.canDelete = canDelete;
			this.context = context;
			this.isRemote = profile.getBool("is_remote");
			
			String title = profile.getString("title");

			editText = new EditText(context);
			editText.setText(title);
			editText.addTextChangedListener(this);
			
			
			setTitle(title);

			if (isRemote) {
				editText.setEnabled(false);
			}

			setView(editText);

			setPositiveButton(context.getResources().getString(R.string.sample_rate_ok), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
			setNegativeButton(context.getResources().getString(R.string.sample_rate_delete), this);
		}

		public void launch() {
			dlg = this.show();
			updateButtons();
		}

		private void updateButtons() {
			dlg.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!isRemote && editText.getText().length() > 0);
			dlg.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(canDelete);
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
						listener.profileTitleEditComplete(profile, editText.getText().toString());
			} else if (which == DialogInterface.BUTTON_NEUTRAL) {
						listener.profileTitleEditComplete(profile, null);
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				ProfileDeleteDlg.open(context, profile, listener);
			}
			dlg.dismiss();
		}

		@Override
		public void afterTextChanged(Editable s) {
			updateButtons();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

	}

}
