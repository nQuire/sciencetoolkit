package org.greengin.sciencetoolkit.ui.components.main.profiles.view.visualizations;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.SensorUIData;
import org.greengin.sciencetoolkit.ui.components.main.profiles.view.AbstractDataVisualizationFragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ListVisualizationFragment extends AbstractDataVisualizationFragment {

	CursorAdapter adapter;
	SimpleDateFormat sdf;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", getResources().getConfiguration().locale);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.fragment_data_view_list, container, false);

		this.adapter = new CustomAdapter(this.getActivity(), getCursor());
		
		ListView view = (ListView) rootView.findViewById(R.id.data_list);
		view.setAdapter(this.adapter);

		return rootView;
	}
	
		
	
	@Override 
	public void onDestroy() {
		super.onDestroy();
	}
	
	private class CustomAdapter extends CursorAdapter {
		public CustomAdapter(Context context, Cursor cursor) {
			super(context, cursor, 0);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			LinearLayout layout = (LinearLayout) view;
			
			String sensorId = DataLogger.i().sensorName(cursor.getString(1));
			SensorWrapper sensor = SensorWrapperManager.getInstance().getSensor(sensorId);
			
			String timestamp = sdf.format(new Date(cursor.getLong(2)));
			String[] labels = SensorUIData.getValueLabels(sensor.getType());
			String[] values = cursor.getString(3).split("\\|");
			StringBuffer data = new StringBuffer();
			for (int i = 0; i < values.length; i++) {
				if (i > 0) {
					data.append("\n");
				}
				data.append(labels[i]).append(": ").append(values[i]);
			}
			
			((TextView)layout.getChildAt(0)).setText(sensorId);
			((TextView)layout.getChildAt(1)).setText(timestamp);
			((TextView)layout.getChildAt(2)).setText(data.toString());
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LinearLayout ll = new LinearLayout(context);
			ll.setOrientation(LinearLayout.VERTICAL);
			
			TextView sensor = new TextView(context);
			ll.addView(sensor, 0);
			
			TextView timestamp = new TextView(context);
			ll.addView(timestamp, 1);
			
			TextView value = new TextView(context);
			ll.addView(value, 2);			
			
			return ll;
		}
	}

	@Override
	protected void updateDataRange() {
		adapter.changeCursor(getCursor());
	}
}