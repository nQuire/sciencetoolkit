package org.greengin.sciencetoolkit;

import java.util.HashMap;
import java.util.Map.Entry;

import org.greengin.sciencetoolkit.logic.datalogging.deprecated.DeprecatedDataLogger;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class VersionManager {

	public static void check(Context applicationContext) {
		Model settings = SettingsManager.get().get("version");
		int currentVersion = settings.getInt("datamodel", 0);
		Log.d("stk version", " " + currentVersion);

		if (currentVersion < 1) {
			// update ids

			HashMap<String, String> nameIds = new HashMap<String, String>();
			for (Entry<String, SensorWrapper> entry : SensorWrapperManager.getInstance().getSensors().entrySet()) {
				nameIds.put(entry.getValue().getName(), entry.getKey());
				Log.d("stk version", entry.getValue().getName() + " > " + entry.getKey());

			}

			Model selected = SettingsManager.get().get("sensor_list");

			for (String name : nameIds.keySet()) {
				for (String prefix : new String[] { "sensor:", "liveview:", "liveplot:" }) {
					SettingsManager.get().remove(prefix + name);
				}
				selected.clear(name);
			}

			for (String profileId : ProfileManager.get().getProfileIds()) {
				Model profile = ProfileManager.get().get(profileId);
				Model sensors = profile.getModel("sensors");
				for (Model oldSensor : sensors.getModels()) {
					String name = oldSensor.getString("id");
					if (nameIds.containsKey(name)) {
						String id = nameIds.get(name);
						Log.d("stk version", profileId + ": " + oldSensor.getString("id") + " > " + id);
						Model newSensor = oldSensor.cloneModel(sensors);
						newSensor.setString("id", id);
						sensors.setModel(id, newSensor);
						sensors.clear(name);
					}
				}
			}

			int result = DeprecatedDataLogger.i().exportAllData();
			Log.d("stk version", "export: " + result);

			if (result == 1) {
				CharSequence text = "This app has changed the way data is stored. Your old data has been saved to the file:\n\ndownloads -> science_toolkit_old_data.csv";
				Toast toast = Toast.makeText(applicationContext, text, Toast.LENGTH_LONG);
				toast.show();
			}
		}

		SettingsManager.get().forceSave();
	}
}
