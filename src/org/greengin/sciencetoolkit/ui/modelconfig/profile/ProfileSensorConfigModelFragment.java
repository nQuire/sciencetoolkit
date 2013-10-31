package org.greengin.sciencetoolkit.ui.modelconfig.profile;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.modelconfig.sensors.SensorConfigViewCreator;

import android.view.View;

public class ProfileSensorConfigModelFragment extends AbstractProfileConfigFragment {

	SensorWrapper sensor;

	@Override
	protected Model fetchProfileConfigModel() {
		String profileSensorId = arguments[2];
		sensor = SensorWrapperManager.getInstance().getSensor(profileSensorId);
		return profile.getModel("sensors").getModel(profileSensorId).getModel("sensor_settings", true);
	}

	@Override
	protected void createConfigOptions(View view) {
		SensorConfigViewCreator.createView(this, view, sensor);
	}

}
