package org.greengin.sciencetoolkit.model;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.util.Log;

public abstract class AbstractModelManager implements ModelChangeListener {
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
		items = ModelDeserializer.xml2modelMap(this, applicationContext, filename);
	}

	private void save() {
		lock.lock();
		ModelSerializer.model2xml(items, applicationContext, filename);
		lock.unlock();
	}

	protected Model get(String key, boolean create) {
		if (!items.containsKey(key)) {
			if (create) {
				Model model = new Model(this);
				items.put(key, model);
				modelModified(model);
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
		Log.d("stk settings", "modified");
		lock.lock();
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Log.d("stk settings", "about to save");
					timer = null;
					save();

				}
			}, 10000);
		}
		lock.unlock();
	}

}
