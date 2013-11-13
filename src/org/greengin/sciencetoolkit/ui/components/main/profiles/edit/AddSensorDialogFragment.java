package org.greengin.sciencetoolkit.ui.components.main.profiles.edit;

import java.util.Vector;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class AddSensorDialogFragment extends DialogFragment implements OnCheckedChangeListener {

	public AddSensorDialogFragment() {
		super();
	}

	Vector<String> chosen;
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

			chosen = new Vector<String>();

			Model sensorsInProfile = profile.getModel("sensors");
			for (String sensorId : SensorWrapperManager.getInstance().getSensorsIds()) {
				if (sensorsInProfile == null || sensorsInProfile.getModel(sensorId) == null) {

					CheckBox checkbox = new CheckBox(view.getContext());
					checkbox.setChecked(false);
					checkbox.setTag(sensorId);
					checkbox.setText(sensorId);
					checkbox.setOnCheckedChangeListener(this);

					ll.addView(checkbox);
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
					ProfileManager.getInstance().addSensors(profile, chosen);
					dismiss();
				}
			});
		}

		return view;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		String sensorId = (String) buttonView.getTag();
		if (isChecked) {
			if (!chosen.contains(sensorId)) {
				chosen.add(sensorId);
			}
		} else {
			chosen.remove(sensorId);
		}
		ok.setEnabled(chosen.size() > 0);
	}

}
