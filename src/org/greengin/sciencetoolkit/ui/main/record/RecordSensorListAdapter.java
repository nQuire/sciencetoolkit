package org.greengin.sciencetoolkit.ui.main.record;

import java.text.DecimalFormat;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordSensorListAdapter extends BaseAdapter {

	RecordSensorListener listener;
	LayoutInflater inflater;

	boolean isRemote = false;
	Vector<Model> profileSensors;
	
	OnClickListener selectListener;
	OnClickListener editListener;

	DecimalFormat formatter = new DecimalFormat("@@##");

	public RecordSensorListAdapter(RecordSensorListener listener, LayoutInflater inflater) {
		this.listener = listener;
		this.inflater = inflater;
		this.profileSensors = null;
		
		selectListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				RecordSensorListAdapter.this.listener.recordSensorSelected((String) v.getTag());
			}
		};
		editListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				RecordSensorListAdapter.this.listener.recordSensorEdit((String) v.getTag());
			}
		};
		
		updateSensorList(false);
	}

	public void updateSensorList() {
		updateSensorList(true);
	}

	public void updateSensorList(boolean notify) {
		Model profile = ProfileManager.get().getActiveProfile();
		this.isRemote = profile.getBool("is_remote");
		this.profileSensors = profile.getModel("sensors", true).getModels("weight");

		if (notify) {
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return profileSensors.size();
	}

	@Override
	public Model getItem(int position) {
		return profileSensors.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Model profileSensor = profileSensors.get(position);
		String profileSensorId = profileSensor.getString("id");

		SensorWrapper sensor = SensorWrapperManager.get().getSensor(profileSensor.getString("sensorid", null));

		int sensorType = sensor != null ? sensor.getType() : profileSensor.getInt("sensor_type", -1);
		String sensorName = sensor != null ? sensor.getName() : parent.getResources().getString(R.string.no_sensor_selected);

		double rate = profileSensor.getDouble("sample_rate", ModelDefaults.DATA_LOGGING_RATE);
		int units = profileSensor.getInt("sample_rate_ux", 0);
		int rateStringId = units == 0 ? R.string.sampling_rate_sec : (units == 1 ? R.string.sampling_rate_min : R.string.sampling_rate_hour);
		String rateLabel = String.format(parent.getResources().getString(rateStringId), formatter.format(rate));

		boolean newView = convertView == null;
		View view = newView ? inflater.inflate(R.layout.view_record_item, parent, false) : convertView;

		view.setTag(profileSensorId);
		view.setOnClickListener(selectListener);
		
		ImageView icon = (ImageView) view.findViewById(R.id.sensor_icon);

		icon.setImageResource(SensorUIData.getSensorResource(sensorType));

		TextView sensorNameView = (TextView) view.findViewById(R.id.sensor_name);
		sensorNameView.setText(sensorName);

		TextView rateView = (TextView) view.findViewById(R.id.sample_rate);
		rateView.setText(rateLabel);
		
		ImageButton button = (ImageButton) view.findViewById(R.id.sensor_config);
		button.setTag(profileSensorId);
		button.setOnClickListener(editListener);
		
		return view;
	}

}