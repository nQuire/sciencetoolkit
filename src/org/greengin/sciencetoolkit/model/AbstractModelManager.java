package org.greengin.sciencetoolkit.model;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.model.serialize.ModelDeserializer;
import org.greengin.sciencetoolkit.model.serialize.ModelSerializer;
import org.greengin.sciencetoolkit.model.serialize.ModelVersionManager;

import android.content.Context;

public abstract class AbstractModelManager implements ModelChangeListener, ModelVersionManager {
	ReentrantLock lock;
	Timer timer;
	int saveDelay;
	String filename;

	Context applicationContext;
	Hashtable<String, Model> items;

	protected AbstractModelManager(Context applicationContext, String filename, int saveDelay) {
		this.applicationContext = applicationContext;
		this.lock = new ReentrantLock();
		this.filename = filename;
		this.saveDelay = saveDelay;

		load();
	}

	private void load() {
		items = ModelDeserializer.xml2modelMap(this, this, applicationContext, filename);
	}

	protected void saveNow() {
		this.save();
	}

	private void save() {
		lock.lock();
		ModelSerializer.model2xml(this, items, applicationContext, filename);
		lock.unlock();
	}

	protected Model get(String key, boolean create) {
		if (!items.containsKey(key)) {
			if (create) {
				Model model = new Model(this);
				items.put(key, model);
				model.setString("id", key);
				return model;
			} else {
				return null;
			}
		} else {
			return items.get(key);
		}
	}

	public void remove(String key) {
		if (items.remove(key) != null) {
			modelModified(null);
		}
	}

	@Override
	public void modelModified(Model model) {
		lock.lock();
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					timer = null;
					save();

				}
			}, saveDelay);
		}
		lock.unlock();
	}

}
