package org.greengin.sciencetoolkit.ui.components.main.datalogging.edit;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AddSensorDialogFragment extends DialogFragment {

	public AddSensorDialogFragment() {
		super();
	}

	RadioGroup group;
	Button ok;
	Model profile;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_add_sensor, container);
		getDialog().setTitle(getResources().getString(R.string.add_sensor));

		profile = ProfileManager.getInstance().getActiveProfile();

		if (profile != null) {
			LinearLayout ll = (LinearLayout) view.findViewById(R.id.add_sensor_list);
			ll.removeAllViews();

			group = new RadioGroup(view.getContext());

			Model sensorsInProfile = profile.getModel("sensors");
			for (String sensorId : SensorWrapperManager.getInstance().getSensorsIds()) {
				if (sensorsInProfile == null || sensorsInProfile.getModel(sensorId) == null) {
					RadioButton button = new RadioButton(view.getContext());
					button.setTag(sensorId);
					button.setText(sensorId);
					group.addView(button);
				}
			}

			ok = (Button) view.findViewById(R.id.ok);
			ok.setEnabled(false);

			Button cancel = (Button) view.findViewById(R.id.cancel);
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					dismiss();
				}

			});

			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					int id = group.getCheckedRadioButtonId();
					View radioButton = group.findViewById(id);
					
					if (radioButton != null && profile != null) {
						String sensorId = radioButton.getTag().toString();
						ProfileManager.getInstance().addSensor(profile, sensorId);
					}
					dismiss();
				}

			});

			group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup rg, int checked) {
					((Button) getView().findViewById(R.id.ok)).setEnabled(true);
				}
			});
			ll.addView(group);
		}

		return view;
	}

}
