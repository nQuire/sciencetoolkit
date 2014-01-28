package org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.dlgs.edittext.EditTextActionListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;

public class SensorSelectDlg {

	public static void open(Context context, int titleId, int messageId, int okLabelId, String defaultValue, boolean required, EditTextActionListener listener) {
		new SensorSelectDlgBuilder(context, titleId, messageId, okLabelId, defaultValue, required, listener).launch();
	}

	private static class SensorSelectDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener, TextWatcher {
		
		View layout;
		GridView view;
		SelectSensorListAdapter adapter;

		AlertDialog dlg;
		EditTextActionListener listener;
		boolean required;

		public SensorSelectDlgBuilder(Context context, int titleId, int messageId, int okLabelId, String defaultValue, boolean required, EditTextActionListener listener) {
			super(context);
			
			layout = LayoutInflater.from(context).inflate(R.layout.dlg_select_sensors, null);

			
			view = (GridView) layout.findViewById(R.id.sensor_list);
			adapter = new SelectSensorListAdapter(LayoutInflater.from(context));
			view.setAdapter(adapter);

			setTitle(context.getResources().getString(titleId));
			
			setView(layout);

			setMessage(context.getResources().getString(messageId));
			setPositiveButton(context.getResources().getString(okLabelId), this);
			setNegativeButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
			updatePositiveButton();
		}

		private void updatePositiveButton() {
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dlg.dismiss();
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
