package org.greengin.sciencetoolkit.ui.base.dlgs.ack;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.model.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

public class AckDlg {

	public static void confirm(Context context, Model confirmContainer, String confirmKey, AckListener listener, boolean onlyRememberYes, int msg, int title) {
		if (confirmContainer.getBool(confirmKey, false)) {
			if (listener != null) {
				listener.ackDecision(true);
			}
		} else {
			new AckDlgBuilder(context, confirmContainer, confirmKey, listener, onlyRememberYes, msg, title).launch();
		}
	}

	private static class AckDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener {

		Model confirmContainer;
		String confirmKey;
		AckListener listener;
		boolean onlyRememberYes;

		CheckBox checkbox;

		AlertDialog dlg;

		public AckDlgBuilder(Context context, Model confirmContainer, String confirmKey, AckListener listener, boolean onlyRememberYes, int msg, int title) {
			super(context);

			this.confirmContainer = confirmContainer;
			this.confirmKey = confirmKey;
			this.listener = listener;
			this.onlyRememberYes = onlyRememberYes;

			View layout = LayoutInflater.from(context).inflate(R.layout.dlg_ack, null);
			checkbox = (CheckBox) layout.findViewById(R.id.ack);
			checkbox.setText(onlyRememberYes ? R.string.ack_remember_yes : R.string.ack_remember_decision);
			checkbox.setChecked(true);

			setTitle(context.getString(title));
			setMessage(context.getString(msg));
			setView(layout);

			setPositiveButton(context.getResources().getString(R.string.button_label_ok), this);
			setNeutralButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			boolean ack = which == DialogInterface.BUTTON_POSITIVE;
			if (checkbox.isChecked() && (ack || !onlyRememberYes)) {
				confirmContainer.setBool(confirmKey, ack);
			}

			if (listener != null) {
				listener.ackDecision(ack);
			}

			dlg.dismiss();
		}
	}

}
