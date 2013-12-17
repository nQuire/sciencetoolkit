package org.greengin.sciencetoolkit.ui.components.main.datalogging;

import java.text.DecimalFormat;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.SensorUIData;
import org.greengin.sciencetoolkit.ui.components.main.datalogging.config.ProfileSensorSettingsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileSensorFragment extends Fragment {

	protected int sensorType;
	protected SensorWrapper sensor;
	protected String profileSensorId;
	protected Model profileSensor;
	protected String profileId;
	protected Model profile;
	
	protected ImageButton editButton;


		
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.profileId = getArguments().getString(Arguments.ARG_PROFILE);
		this.profileSensorId = getArguments().getString(Arguments.ARG_SENSOR);
		
		this.profile = ProfileManager.get().get(this.profileId);
		this.profileSensor = this.profile == null ? null : this.profile.getModel("sensors", true).getModel(this.profileSensorId);
		
		if (this.profileSensor == null) {
			this.sensor = null;
			this.sensorType = -1;
		} else {
			this.sensor = SensorWrapperManager.get().getSensor(this.profileSensor.getString("sensorid", null));
			if (this.sensor != null) {
				this.sensorType = this.sensor.getType();
			} else {
				this.sensorType = this.profileSensor.getInt("sensor_type", -1);
			}
		}
	}

	protected void prepareView(View rootView) {
		TextView nameTextView = (TextView) rootView.findViewById(R.id.sensor_name);
		nameTextView.setText(this.sensor != null ? this.sensor.getName() : "no sensor");

		TextView rateTextView = (TextView) rootView.findViewById(R.id.sensor_sample_rate);
		double rate = this.profileSensor.getDouble("sample_rate", ModelDefaults.DATA_LOGGING_RATE);
		DecimalFormat formatter = new DecimalFormat("@@##");
		String rateStr = formatter.format(rate);
		
		int units = this.profileSensor.getInt("sample_rate_ux", 0);
		int rateStringId = units == 0 ? R.string.samples_second_v : (units == 1 ? R.string.samples_min_v : R.string.samples_hour_v);  
		
		rateTextView.setText(String.format(getResources().getString(rateStringId), rateStr));

		ImageView image = (ImageView) rootView.findViewById(R.id.sensor_image);
		image.setImageResource(SensorUIData.getSensorResource(this.sensorType));

		editButton = (ImageButton) rootView.findViewById(R.id.sensor_config_edit);
		editButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ProfileSensorSettingsActivity.class);
				intent.putExtra(Arguments.ARG_PROFILE, profileId);				
				intent.putExtra(Arguments.ARG_SENSOR, profileSensorId);				
		    	startActivity(intent);
			}
		});
	}
	
	protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
		View rootView = inflater.inflate(R.layout.fragment_profile_sensor, container, false);
		return rootView;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
 
		View rootView = loadViewLayout(inflater, container);
		prepareView(rootView);
		return rootView;
	}

}











