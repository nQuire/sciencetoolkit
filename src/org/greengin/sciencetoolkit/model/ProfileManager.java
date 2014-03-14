package org.greengin.sciencetoolkit.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.model.notifications.NotificationListenerAggregator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ProfileManager extends AbstractModelManager implements ModelNotificationListener {

	public static final String DEFAULT_PROFILE_ID = "1";

	private static ProfileManager instance;

	public static void init(Context applicationContext) {
		instance = new ProfileManager(applicationContext);
	}

	public static ProfileManager get() {
		return instance;
	}

	NotificationListenerAggregator listeners;

	Model settings;
	Model appSettings;

	Comparator<String> profileIdComparator;

	private ProfileManager(Context applicationContext) {
		super(applicationContext, "profiles.xml", 500);
		settings = SettingsManager.get().get("profiles");
		appSettings = SettingsManager.get().get("app");
		listeners = new NotificationListenerAggregator(applicationContext, "profiles:notifications");
		SettingsManager.get().registerDirectListener("profiles", this);
		initDefaultProfile();
		checkDataConsistency();

		ModelNotificationListener sensorListener = new ModelNotificationListener() {
			@Override
			public void modelNotificationReceived(String msg) {
				if (msg.startsWith("sensor:")) {
					sensorModified(msg.substring(7));
				}
			}
		};

		for (String sensorId : SensorWrapperManager.get().getSensorsIds()) {
			SettingsManager.get().registerDirectListener("sensor:" + sensorId, sensorListener);
		}

		profileIdComparator = new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				try {
					return Integer.parseInt(rhs) - Integer.parseInt(lhs);
				} catch (Exception e) {
					return 0;
				}
			}
		};
	}

	private void initDefaultProfile() {
		if (!items.containsKey(ProfileManager.DEFAULT_PROFILE_ID)) {
			Model defaultProfile = createEmptyProfile(ProfileManager.DEFAULT_PROFILE_ID);
			defaultProfile.setString("title", applicationContext.getString(R.string.default_profile_title), true);
			modelModified(defaultProfile);
			listeners.fireEvent("list");

			settings.setString("current_profile", defaultProfile.getString("id"));
		}

		if (!items.containsKey(getActiveProfileId())) {
			settings.setString("current_profile", ProfileManager.DEFAULT_PROFILE_ID);
		}
	}

	public boolean profileIdIsActive(String id) {
		return id != null && id.equals(getActiveProfileId());
	}

	public boolean profileIsActive(Model profile) {
		return profile != null && profileIdIsActive(profile.getString("id"));
	}

	public boolean profileIdIsDefault(String id) {
		return ProfileManager.DEFAULT_PROFILE_ID.equals(id);
	}

	public boolean profileIsDefault(Model profile) {
		return profile != null && profileIdIsDefault(profile.getString("id"));
	}

	public boolean activeProfileIsDefault() {
		return profileIdIsDefault(getActiveProfileId());
	}

	private void checkDataConsistency() {
		for (Entry<String, Model> entry : items.entrySet()) {
			String id = entry.getKey();
			Model model = entry.getValue();

			if (id.length() == 0) {
				Log.d("stk profiles", "empty id");
			} else {
				if (!id.equals(model.getString("id"))) {
					Log.d("stk profiles", "conflicting ids: " + id + " " + model.getString("id"));
				}
			}
		}
	}

	private Model createEmptyProfile(String id) {
		Model profile = new Model(this);
		items.put(id, profile);

		profile.setString("id", id, true);
		profile.getModel("sensors", true, true);
		profile.setBool("initial_edit", true);
		return profile;
	}

	public void createProfile(String title, boolean makeActive) {
		Model newProfile = createEmptyProfile(getNewId());
		newProfile.setString("title", title, true);

		super.modelModified(null);
		listeners.fireEvent("list");

		if (makeActive && DataLogger.get().isIdle()) {
			switchActiveProfile(newProfile.getString("id"));
		}
	}

	public void deleteProfile(String profileId) {
		this.remove(profileId);
		listeners.fireEvent("list");
		DataLogger.get().deleteData(profileId);
	}

	public void switchActiveProfile(String profileId) {
		if (profileId != null && !profileId.equals(settings.getString("current_profile"))) {
			if (DataLogger.get().isRunning()) {
				DataLogger.get().stopSeries();
			}

			settings.setString("current_profile", profileId);
			updateGlobalSensors();
		}
	}

	public boolean sensorSelectedInExplore(String sensorId) {
		Model profile = getActiveProfile();
		if (profile != null && profile.getBool("initial_edit", false) && !profile.getBool("is_remote") && DataLogger.get().isIdle()) {
			profile.getModel("sensors").clearAll(true);
			addSensor(profile, sensorId, false);
			return true;
		} else {
			return false;
		}
	}

	public void removeSensorFromActiveProfile(String sensorId) {
		Model profile = getActiveProfile();
		if (profile != null) {
			removeSensor(profile, sensorId);
		}

		profile.setBool("initial_edit", profile.getModel("sensors", true).getModels().size() == 0);
	}

	private Model addSensor(Model profile, String sensorId, boolean suppressSave) {
		Model profileSensors = profile.getModel("sensors", true, true);
		String id = null;
		for (int i = 0;; i++) {
			id = String.valueOf(i);
			if (profileSensors.getModel(id) == null) {
				break;
			}
		}

		return addSensor(profile, id, sensorId, suppressSave);
	}

	private Model addSensor(Model profile, String profileSensorId, String sensorId, boolean suppressSave) {
		Model profileSensors = profile.getModel("sensors", true, true);
		int weight = profileSensors.getModels().size();

		if (!sensorInProfile(sensorId, profile)) {
			Model profileSensor = profileSensors.getModel(profileSensorId, true, true);
			profileSensor.setString("id", profileSensorId, true);
			profileSensor.setString("sensorid", sensorId, true);
			profileSensor.setInt("weight", weight, true);
			Model sensorSettings = SettingsManager.get().get("sensor:" + sensorId);
			Model profileSensorSettings = profileSensor.getModel("sensor_settings", true, true);
			profileSensorSettings.copyPrimitives(sensorSettings, true);

			if (!suppressSave) {
				this.modelModified(profile);
			}

			return profileSensor;
		} else {
			return null;
		}
	}

	public void addSensors(Model profile, Vector<String> sensorIds) {
		addSensors(profile, sensorIds, false);
		profile.setBool("initial_edit", false);
	}

	private void addSensors(Model profile, Vector<String> sensorIds, boolean suppressSave) {
		for (String sensorId : sensorIds) {
			addSensor(profile, sensorId, true);
		}

		if (sensorIds.size() > 0 && !suppressSave) {
			this.modelModified(profile);
		}
	}

	public void removeSensor(Model profile, String sensorId) {
		Model profileSensors = profile.getModel("sensors", true);

		for (Model profileSensor : profileSensors.getModels()) {
			if (sensorId.equals(profileSensor.getString("sensorid"))) {
				profileSensors.clear(profileSensor.getString("id"));
			}
		}
	}

	public Model get(String key) {
		return get(key, false);
	}

	public Model getActiveProfile() {
		return get(getActiveProfileId());
	}

	public String getActiveProfileId() {
		return settings.getString("current_profile");
	}

	public Vector<String> getProfileIds() {
		Vector<String> profiles = new Vector<String>();
		for (String id : items.keySet()) {
			profiles.add(id);
		}

		Collections.sort(profiles, profileIdComparator);
		return profiles;
	}

	private String getNewId() {
		for (int i = 1;; i++) {
			String test = Integer.toString(i);
			if (!items.containsKey(test)) {
				return test;
			}
		}
	}

	public int getProfileCount() {
		return this.items.size();
	}

	public boolean sensorInActiveProfile(String sensorId) {
		return sensorInProfile(sensorId, getActiveProfile());
	}

	public boolean sensorInProfile(String sensorId, Model profile) {
		return getSensorProfileId(sensorId, profile) != null;
	}

	public String getSensorProfileIdInActiveProfile(String sensorId) {
		return getSensorProfileId(sensorId, getActiveProfile());
	}

	public String getSensorProfileId(String sensorId, Model profile) {
		if (sensorId != null && profile != null) {
			for (Model profileSensor : profile.getModel("sensors", true).getModels()) {
				if (sensorId.equals(profileSensor.getString("sensorid"))) {
					return profileSensor.getString("id");
				}
			}
		}
		return null;
	}

	public Vector<String> getSensorsInActiveProfile() {
		return getSensorsInProfile(getActiveProfile());
	}

	public Vector<String> getSensorsInProfile(Model profile) {
		Vector<String> sensorIds = new Vector<String>();

		if (profile != null) {
			for (Model profileSensor : profile.getModel("sensors", true).getModels()) {
				String sensorId = profileSensor.getString("sensorid", null);
				if (sensorId != null) {
					sensorIds.add(profileSensor.getString("sensorid"));
				}
			}
		}

		return sensorIds;
	}

	@Override
	public void modelModified(Model model) {
		super.modelModified(model);

		if (model != null) {
			Model profile = model.getRootParent();
			String profileId = profile.getString("id", null);
			if (profileId.equals(getActiveProfileId())) {
				updateGlobalSensors();
			}

			listeners.fireEvent(profileId);
		}
	}

	private void updateGlobalSensors() {
		Model profile = getActiveProfile();

		if (profile != null) {
			boolean modified = false;
			for (Model profileSensor : profile.getModel("sensors", true).getModels()) {
				String sensorId = profileSensor.getString("sensorid");

				Model profileSensorSettings = profileSensor.getModel("sensor_settings");
				Model globalSensorSettings = SettingsManager.get().get("sensor:" + sensorId);
				if (globalSensorSettings.copyPrimitives(profileSensorSettings, true)) {
					modified = true;
				}
			}

			if (modified) {
				SettingsManager.get().modelModified(null);
			}
		}

	}

	private void sensorModified(String sensorId) {
		Model profile = this.getActiveProfile();
		if (profile != null) {
			Model profileSensor = profile.getModel("sensors", true).getModel(sensorId, false);
			if (profileSensor != null) {
				Model globalSettings = SettingsManager.get().get("sensor:" + sensorId);
				if (profileSensor.getModel("sensor_settings", true).copyPrimitives(globalSettings, true)) {
					modelModified(null);
				}
			}
		}
	}

	@Override
	public void modelNotificationReceived(String msg) {
		listeners.fireEvent("switch");
	}

	public void registerUIListener(ModelNotificationListener listener) {
		listeners.addUIListener(listener);
	}

	public void unregisterUIListener(ModelNotificationListener listener) {
		listeners.removeUIListener(listener);
	}

	public void registerDirectListener(ModelNotificationListener listener) {
		listeners.addDirectListener(listener);
	}

	public void unregisterDirectListener(ModelNotificationListener listener) {
		listeners.removeDirectListener(listener);
	}

	public void updateRemoteProfiles(JSONObject remoteData) {
		try {
			Iterator<?> projectIt = remoteData.keys();
			while (projectIt.hasNext()) {

				String jsonProjectId = (String) projectIt.next();
				JSONArray jsonProjectArray = remoteData.getJSONArray(jsonProjectId);

				for (int i = 0; i < jsonProjectArray.length(); i++) {
					JSONObject jsonProfileContainerObj = jsonProjectArray.getJSONObject(i);
					String jsonProfileTitle = jsonProfileContainerObj.getString("title");
					JSONObject jsonProfileObj = jsonProfileContainerObj.getJSONObject("obj");
					String jsonProfileId = jsonProfileObj.getString("id");

					String profileId = String.format("r.%s.%s", jsonProjectId, jsonProfileId);

					Model profile = get(profileId);
					if (profile == null) {
						profile = createEmptyProfile(profileId);
					}

					profile.setString("title", jsonProfileTitle, true);
					profile.setBool("initial_edit", false);
					profile.setBool("is_remote", true, true);
					profile.setBool("requires_location", jsonProfileObj.getBoolean("geolocated"), true);
					
					Model remoteInfo = profile.getModel("remote_info", true, true);
					remoteInfo.setString("project", jsonProjectId);
					remoteInfo.setString("profile", jsonProfileId);
					
					profile.clear("sensors", true);
					
					JSONArray jsonInputsArray = jsonProfileObj.getJSONArray("sensorInputs");

					for (int j = 0; j < jsonInputsArray.length(); j++) {
						JSONObject jsonInputObj = jsonInputsArray.getJSONObject(j);
						String jsonInputId = jsonInputObj.getString("id");

						double rate = jsonInputObj.getDouble("rate");
						String sensorType = jsonInputObj.getString("sensor");

						String sensorId = sensorType + ":0";
						if (SensorWrapperManager.get().getSensors().containsKey(sensorId)) {
							Model profileSensor = addSensor(profile, jsonInputId, sensorId, true);
							profileSensor.setString("sensor_type", sensorType);
							profileSensor.setDouble("sample_rate", rate, true);
						}
					}
				}
			}

			this.forceSave();
			listeners.fireEvent("list");
			
		} catch (JSONException e) {
			Log.d("stk remote update", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public int getCurrentVersion() {
		// 1: sensor sample period -> sample rate
		return 1;
	}

	@Override
	public void updateRootModel(String key, Model model, int version) {
		if (version < 1) {
			// sensor sample period -> sample rate

			for (Model sensorModel : model.getModel("sensors", true).getModels()) {
				if (sensorModel.containsKey("period")) {
					if (!sensorModel.containsKey("sample_rate")) {
						int period = sensorModel.getInt("period");
						double rate = Math.min(period > 0 ? 1000 / period : 0, ModelDefaults.DATA_LOGGING_RATE_MAX);
						sensorModel.setDouble("sample_rate", rate);
						sensorModel.setInt("sample_rate_ux", 0);
					}
					sensorModel.clear("period");
				}
			}
		}
	}
}
