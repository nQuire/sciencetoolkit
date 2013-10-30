package org.greengin.sciencetoolkit.model;

import java.util.Hashtable;
import java.util.Map.Entry;

public class Model {
	Hashtable<String, Object> entries;
	ModelChangeListener listener;

	public Model(ModelChangeListener listener) {
		this.entries = new Hashtable<String, Object>();
		this.listener = listener;
	}

	private boolean set(String key, Object obj, boolean suppressSave) {
		if (obj != null && !obj.equals(entries.get(key))) {
			entries.put(key, obj);
			if (!suppressSave) {
				this.listener.modelModified(this);
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean clear(String key) {
		return clear(key, false);
	}

	public boolean clear(String key, boolean suppressSave) {
		boolean removed = entries.remove(key) != null;
		if (removed && !suppressSave) {
			listener.modelModified(this);
		}
		return removed;
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

	public boolean setModel(String key, Model model) {
		return set(key, model, false);
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

	public boolean setModel(String key, Model model, boolean suppressSave) {
		return set(key, model, suppressSave);
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

	public void copyPrimitives(Model source, boolean suppressSave) {
		if (source != null) {
			for (Entry<String, Object> entry : entries.entrySet()) {
				if (entry.getValue() instanceof String || entry.getValue() instanceof Boolean || entry.getValue() instanceof Number) {
					this.set(entry.getKey(), entry.getValue(), true);
				}
			}
			if (!suppressSave) {
				this.listener.modelModified(this);
			}
		}
	}

	public Model getModel(String key) {
		return getModel(key, false, false);
	}

	public Model getModel(String key, boolean createIfNotExists) {
		return getModel(key, createIfNotExists, false);
	}

	public Model getModel(String key, boolean createIfNotExists, boolean suppressSave) {
		if (entries.containsKey(key)) {
			return (Model) entries.get(key);
		} else if (createIfNotExists) {
			Model model = new Model(this.listener);
			entries.put(key, model);
			if (!suppressSave) {
				listener.modelModified(this);
			}
			return model;
		} else {
			return null;
		}
	}

}
