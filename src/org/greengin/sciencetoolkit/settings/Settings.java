package org.greengin.sciencetoolkit.settings;

import java.util.Hashtable;

public class Settings {
	Hashtable<String, Object> entries;
	SettingsManager manager;

	public Settings(SettingsManager manager) {
		this.entries = new Hashtable<String, Object>();
		this.manager = manager;
	}

	private boolean set(String key, Object obj, boolean suppressSave) {
		if (obj != null && !obj.equals(entries.get(key))) {
			if (!suppressSave) {
				manager.modified();
			}
			entries.put(key, obj);
			return true;
		} else {
			return false;
		}
	}

	private Object get(String key, Object defaultValue) {
		return entries.containsKey(key) ? entries.get(key) : defaultValue;
	}

	public boolean setInt(String key, int value) {
		return setInt(key, value, false);
	}
	
	public boolean setLong(String key, long value) {
		return setLong(key, value, false);
	}

	public boolean setDouble(String key, double value) {
		return setDouble(key, value, false);
	}

	public boolean setString(String key, String value) {
		return setString(key, value, false);
	}

	public boolean setBool(String key, boolean value) {
		return setBool(key, value, false);
	}


	boolean setInt(String key, int value, boolean suppressSave) {
		return set(key, value, suppressSave);
	}
	
	boolean setLong(String key, long value, boolean suppressSave) {
		return set(key, value, suppressSave);
	}

	boolean setDouble(String key, double value, boolean suppressSave) {
		return set(key, value, suppressSave);
	}

	boolean setString(String key, String value, boolean suppressSave) {
		return set(key, value, suppressSave);
	}

	boolean setBool(String key, boolean value, boolean suppressSave) {
		return set(key, value, suppressSave);
	}

	public Integer getInt(String key) {
		return getInt(key, 0);
	}
	
	public Long getLong(String key) {
		return getLong(key, 0);
	}

	public Integer getInt(String key, int defaultValue) {
		return (Integer) get(key, defaultValue);
	}

	public Long getLong(String key, long defaultValue) {
		return (Long) get(key, defaultValue);
	}
	
	public Number getNumber(String key, Number defaultValue) {
		return (Number) get(key, defaultValue);
	}

	public Double getDouble(String key) {
		return getDouble(key, 0.);
	}

	public Double getDouble(String key, double defaultValue) {
		return (Double) get(key, defaultValue);
	}

	public Boolean getBool(String key) {
		return getBool(key, false);
	}

	public Boolean getBool(String key, boolean defaultValue) {
		return (Boolean) get(key, defaultValue);
	}

	public String getString(String key) {
		return getString(key, "");
	}

	public String getString(String key, String defaultValue) {
		return (String) get(key, defaultValue);
	}

}
