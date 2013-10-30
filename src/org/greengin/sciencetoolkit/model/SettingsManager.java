package org.greengin.sciencetoolkit.model;


import android.content.Context;

public class SettingsManager extends AbstractModelManager {

	private static SettingsManager instance;

	public static void init(Context applicationContext) {
		instance = new SettingsManager(applicationContext);
	}

	public static SettingsManager getInstance() {
		return instance;
	}

	private SettingsManager(Context applicationContext) {
		super(applicationContext, "settings.xml", 600);
	}

	public Model get(String key) {
		return get(key, true);
	}
}
