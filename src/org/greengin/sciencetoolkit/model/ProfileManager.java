package org.greengin.sciencetoolkit.model;


import android.content.Context;

public class ProfileManager extends AbstractModelManager {

	private static ProfileManager instance;
	
	public static void init(Context applicationContext) {
		instance = new ProfileManager(applicationContext);
	}

	public static ProfileManager getInstance() {
		return instance;
	}

	
	Model settings;
	
	private ProfileManager(Context applicationContext) {
		super(applicationContext, "profiles.xml", 1000);
		settings = SettingsManager.getInstance().get("profiles");
		initDefaultProfile();
	}
	
	private void initDefaultProfile() {
		if (settings.getBool("create_default", true) && items.size() == 0) {
			Model defaultProfile = createEmptyProfile();
			defaultProfile.setString("title", "Default");
			
			settings.setBool("create_default", false);
			settings.setString("current_profile", defaultProfile.getString("id"));
		}
	}
	
	private Model createEmptyProfile() {
		String id = getNewId();
		Model profile = new Model(this);
		items.put(id, profile);
		
		profile.setString("type", "profile");
		profile.setString("id", id);
		profile.getModel("sensors", true, false);
		
		return profile;
	}

	public Model get(String key) {
		return get(key, false);
	}
	
	public Model getActiveProfile() {
		String id = settings.getString("current_profile");
		Model profile = get(id);
		return profile;
	}
	
	
	private String getNewId() {
		for (int i = 1;; i++) {
			String test = Integer.toString(i);
			if (!items.containsKey(test)) {
				return test;
			}
		}
	}
}
