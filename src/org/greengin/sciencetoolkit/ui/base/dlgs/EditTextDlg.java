package org.greengin.sciencetoolkit.ui.base.dlgs;

import org.greengin.sciencetoolkit.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class EditTextDlg {

	public static void open(Context context, int titleId, int messageId, int okLabelId, String defaultValue, boolean required, EditTextActionListener listener) {
		new EditTextDlgBuilder(context, titleId, messageId, okLabelId, defaultValue, required, listener).launch();
	}
	
	private static class EditTextDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener, TextWatcher {
		
		EditText editText;
		AlertDialog dlg;
		EditTextActionListener listener;
		boolean required;
		
		public EditTextDlgBuilder(Context context, int titleId, int messageId, int okLabelId, String defaultValue, boolean required, EditTextActionListener listener) {
			super(context);
			
			this.listener = listener;
			this.required = required;
			
			editText = new EditText(context);
			if (defaultValue != null) {
				editText.setText(defaultValue);
			}
			
			editText.addTextChangedListener(this);
			
			setTitle(context.getResources().getString(titleId));

			setMessage(context.getResources().getString(messageId));
			setView(editText);
			setPositiveButton(context.getResources().getString(okLabelId), this);
			setNegativeButton(context.getResources().getString(R.string.button_label_cancel), this);
		}
		
		public void launch() {
			dlg = this.show();
			editText.requestFocus();
			updatePositiveButton();
		}
		
		private void updatePositiveButton() {
			dlg.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!required || editText.getText().length() > 0);
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			String value = which == DialogInterface.BUTTON_POSITIVE ? editText.getText().toString() : null;
			dlg.dismiss();
			listener.editTextComplete(value);
		}

		
		@Override
		public void afterTextChanged(Editable s) {
			updatePositiveButton();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		
	}

}
