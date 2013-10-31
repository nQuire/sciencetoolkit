package org.greengin.sciencetoolkit.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;


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

	public Vector<Model> getModels() {
		Vector<Model> models = new Vector<Model>();
		for (Entry<String, Object> entry : entries.entrySet()) {
			if (entry.getValue() instanceof Model) {
				models.add((Model) entry.getValue());
			}
		}
		return models;
	}

	public Vector<Model> getModels(String orderKey) {
		Vector<Model> models = getModels();
		if (orderKey != null) {
			Collections.sort(models, new ModelComparator(orderKey));
		}
		return models;
	}

	private static class ModelComparator implements Comparator<Model> {
		String key;

		public ModelComparator(String key) {
			this.key = key;
		}

		@Override
		public int compare(Model lhs, Model rhs) {
			Object va = lhs.get(key, null);
			Object vb = rhs.get(key, null);
			if (va == null && vb == null) {
				return 0;
			} else if (va == null) {
				return -1;
			} else if (vb == null) {
				return 1;
			} else if (va instanceof String && vb instanceof String) {
				return ((String) va).compareTo((String) vb);
			} else if (va instanceof Long && vb instanceof Long) {
				return ((Long) va).compareTo((Long) vb);
			} else if (va instanceof Double && vb instanceof Double) {
				return ((Double) va).compareTo((Double) vb);
			} else if (va instanceof Integer && vb instanceof Integer) {
				return ((Integer) va).compareTo((Integer) vb);
			} else if (va instanceof Boolean && vb instanceof Boolean) {
				return ((Boolean) va).compareTo((Boolean) vb);
			} else {
				return 0;
			}
		}
	}

}
