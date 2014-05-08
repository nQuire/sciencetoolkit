package org.greengin.sciencetoolkit.ui.base.dlgs.sensorselect;

import java.util.Vector;

import org.greengin.sciencetoolkit.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class SensorSelectDlg {

	
	public static void open(Context context, int titleId, int messageId, String defaultValue, boolean required, SelectSensorActionListener listener) {
		new SensorSelectDlgBuilder(context, titleId, messageId, defaultValue, required, listener).launch();
	}

	private static class SensorSelectDlgBuilder extends AlertDialog.Builder implements DialogInterface.OnClickListener, TextWatcher, OnItemClickListener {
		
		View layout;
		GridView view;
		SelectSensorListAdapter adapter;
		Vector<String> selected;
		SelectSensorActionListener listener;

		AlertDialog dlg;

		public SensorSelectDlgBuilder(Context context, int titleId, int messageId, String defaultValue, boolean required, 	SelectSensorActionListener listener) {
			super(context);
			
			this.listener = listener;			
			this.selected = new Vector<String>();
			
			layout = LayoutInflater.from(context).inflate(R.layout.dlg_select_sensors, null);
			
			view = (GridView) layout.findViewById(R.id.sensor_list);
			adapter = new SelectSensorListAdapter(LayoutInflater.from(context), selected, listener);
			view.setAdapter(adapter);
			view.setOnItemClickListener(this);

			setTitle(context.getResources().getString(titleId));
			
			setView(layout);

			setMessage(context.getResources().getString(messageId));
			setPositiveButton(context.getResources().getString(R.string.add_profile_sensor_oklabel0), this);
			setNegativeButton(context.getResources().getString(R.string.button_label_cancel), this);
		}

		public void launch() {
			dlg = this.show();
			updatePositiveButton();
		}

		private void updatePositiveButton() {
			String label;
			boolean enabled;
			
			switch (selected.size()) {
			case 0:
				enabled = false;
				label = dlg.getContext().getResources().getString(R.string.add_profile_sensor_oklabel0);
				break;
			case 1:
				enabled = true;
				label = dlg.getContext().getResources().getString(R.string.add_profile_sensor_oklabel1);
				break;
			default:
				enabled = true;
				label = String.format(dlg.getContext().getResources().getString(R.string.add_profile_sensor_oklabelmany), selected.size());
				break;
			}
			dlg.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
			dlg.getButton(DialogInterface.BUTTON_POSITIVE).setText(label);
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Vector<String> value = which == DialogInterface.BUTTON_POSITIVE ? selected : null;
			dlg.dismiss();
			listener.sensorsSelected(value);
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

		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
			String sensorId = (String) view.getTag();
			if (!selected.remove(sensorId)) {
				selected.add(sensorId);
			}
			updatePositiveButton();
			adapter.updateSensorList();						
		}

	}

}
