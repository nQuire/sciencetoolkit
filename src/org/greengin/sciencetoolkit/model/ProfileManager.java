package org.greengin.sciencetoolkit.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.notifications.ModelNotificationListener;
import org.greengin.sciencetoolkit.model.notifications.NotificationListenerAggregator;

import android.content.Context;
import android.util.Log;

public class ProfileManager extends AbstractModelManager implements ModelNotificationListener {

	public static final String DEFAULT_PROFILE_ID = "1";

	private static ProfileManager instance;

	public static void init(Context applicationContext) {
		instance = new ProfileManager(applicationContext);
	}

	public static ProfileManager getInstance() {
		return instance;
	}

	NotificationListenerAggregator listeners;

	Model settings;
	Model appSettings;

	Comparator<String> profileIdComparator;

	private ProfileManager(Context applicationContext) {
		super(applicationContext, "profiles.xml", 500);
		settings = SettingsManager.getInstance().get("profiles");
		appSettings = SettingsManager.getInstance().get("app");
		listeners = new NotificationListenerAggregator(applicationContext, "profiles:notifications");
		SettingsManager.getInstance().registerDirectListener("profiles", this);
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

		for (String sensorId : SensorWrapperManager.getInstance().getSensorsIds()) {
			SettingsManager.getInstance().registerDirectListener("sensor:" + sensorId, sensorListener);
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
		return profile;
	}

	public void createProfile(String title, boolean makeActive) {
		Model newProfile = createEmptyProfile(getNewId());
		newProfile.setString("title", title, true);
		modelModified(newProfile);
		listeners.fireEvent("list");

		if (makeActive && !DataLogger.getInstance().isRunning()) {
			switchActiveProfile(newProfile.getString("id"));
		}
	}

	public void deleteProfile(String profileId) {
		this.remove(profileId);
		listeners.fireEvent("list");
		DataLogger.getInstance().deleteData(profileId);
	}

	public void switchActiveProfile(String profileId) {
		if (profileId != null && !profileId.equals(settings.getString("current_profile"))) {
			if (DataLogger.getInstance().isRunning()) {
				DataLogger.getInstance().stop();
			}
			
			if (profileIdIsDefault(profileId)) {
				updateDefaultProfileWithCurrent();
			}
			
			settings.setString("current_profile", profileId);

			updateGlobalSensors();
		}
	}

	public int profileCount() {
		return this.items.size();
	}

	public void addSensor(Model profile, String sensorId) {
		addSensor(profile, sensorId, false);
	}

	public void addSensorToActiveProfile(String sensorId) {
		Model profile = getActiveProfile();
		if (profile != null) {
			boolean logging = DataLogger.getInstance().isRunning();

			if (logging) {
				DataLogger.getInstance().stop();
			}
			addSensor(profile, sensorId);
			if (logging) {
				DataLogger.getInstance().start();
			}
		}
	}

	public void removeSensorFromActiveProfile(String sensorId) {
		Model profile = getActiveProfile();
		if (profile != null) {
			boolean logging = DataLogger.getInstance().isRunning();

			if (logging) {
				DataLogger.getInstance().stop();
			}
			removeSensor(profile, sensorId);
			if (logging) {
				DataLogger.getInstance().start();
			}
		}
	}

	public void addSensor(Model profile, String sensorId, boolean suppressSave) {
		Model profileSensors = profile.getModel("sensors", true, true);
		int weight = profileSensors.getModels().size();

		if (!profileSensors.containsKey(sensorId)) {
			Model profileSensor = profileSensors.getModel(sensorId, true, true);
			profileSensor.setString("id", sensorId, true);
			profileSensor.setInt("weight", weight, true);
			Model sensorSettings = SettingsManager.getInstance().get("sensor:" + sensorId);
			Model profileSensorSettings = profileSensor.getModel("sensor_settings", true, true);
			profileSensorSettings.copyPrimitives(sensorSettings, true);

			if (!suppressSave) {
				this.modelModified(profile);
			}
		}
	}

	public void addSensors(Model profile, Vector<String> sensorIds) {
		addSensors(profile, sensorIds, false);
	}

	public void addSensors(Model profile, Vector<String> sensorIds, boolean suppressSave) {
		for (String sensorId : sensorIds) {
			addSensor(profile, sensorId, true);
		}

		if (sensorIds.size() > 0 && !suppressSave) {
			this.modelModified(profile);
		}
	}

	public void removeSensor(Model profile, String sensorId) {
		Model profileSensors = profile.getModel("sensors", true, true);
		profileSensors.clear(sensorId);
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

	public boolean sensorInActiveProfile(String sensorId) {
		return sensorInProfile(sensorId, getActiveProfile());
	}

	public boolean sensorInProfile(String sensorId, Model profile) {
		return sensorId != null && profile != null && profile.getModel("sensors", true).getModel(sensorId) != null;
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
	
	private void updateDefaultProfileWithCurrent() {
		Model profile = getActiveProfile();
		Model defaultProfile = get(ProfileManager.DEFAULT_PROFILE_ID);
		
		if (profile != null && defaultProfile != null && profile != defaultProfile) {
			Model profileSensors = profile.getModel("sensors", true, true);
			defaultProfile.clear("sensors", true);
			defaultProfile.setModel("sensors", profileSensors.cloneModel(defaultProfile));
		}
	}

	private void updateGlobalSensors() {
		Model profile = getActiveProfile();

		if (profile != null) {
			boolean modified = false;
			for (Model profileSensor : profile.getModel("sensors", true).getModels()) {
				String sensorId = profileSensor.getString("id");

				Model profileSensorSettings = profileSensor.getModel("sensor_settings");
				Model globalSensorSettings = SettingsManager.getInstance().get("sensor:" + sensorId);
				if (globalSensorSettings.copyPrimitives(profileSensorSettings, true)) {
					modified = true;
				}
			}

			if (modified) {
				SettingsManager.getInstance().modelModified(null);
			}
		}

	}

	private void sensorModified(String sensorId) {
		Model profile = this.getActiveProfile();
		if (profile != null) {
			Model profileSensor = profile.getModel("sensors", true).getModel(sensorId, false);
			if (profileSensor != null) {
				Model globalSettings = SettingsManager.getInstance().get("sensor:" + sensorId);
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

	@Override
	public int getCurrentVersion() {
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
