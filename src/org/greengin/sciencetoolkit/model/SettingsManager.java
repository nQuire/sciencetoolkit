package org.greengin.sciencetoolkit.model;

import java.util.Hashtable;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class SettingsManager extends AbstractModelManager {

	private static SettingsManager instance;

	public static void init(Context applicationContext) {
		instance = new SettingsManager(applicationContext);
	}

	public static SettingsManager getInstance() {
		return instance;
	}

	private Hashtable<String, SettingsChangeListenerAggregator> listeners;

	private SettingsManager(Context applicationContext) {
		super(applicationContext, "settings.xml", 600);
		listeners = new Hashtable<String, SettingsChangeListenerAggregator>();
	}

	public Model get(String key) {
		return get(key, true);
	}

	@Override
	public void modelModified(Model model) {
		super.modelModified(model);
		SettingsChangeListenerAggregator aggregator = getAggregator(model.getString("id"), false);
		if (aggregator != null) {
			aggregator.fireChangeEvent();
		}
	}
	
	public void registerUIListener(String key, BroadcastReceiver listener) {
		getAggregator(key, true).addUIListener(listener);
	}

	public void unregisterUIListener(String key, BroadcastReceiver listener) {
		getAggregator(key, true).removeUIListener(listener);
	}

	public void registerDirectListener(String key, SettingsChangeListener listener) {
		getAggregator(key, true).addDirectListener(listener);
	}

	public void unregisterDirectListener(String key, SettingsChangeListener listener) {
		getAggregator(key, true).removeDirectListener(listener);
	}

	private SettingsChangeListenerAggregator getAggregator(String key, boolean create) {
		if (!listeners.containsKey(key) && create) {
			listeners.put(key, new SettingsChangeListenerAggregator("settings:" + key));
		}
		return listeners.get(key);
	}

	private class SettingsChangeListenerAggregator {
		String filter;
		Vector<SettingsChangeListener> directListeners;
		Vector<BroadcastReceiver> uilisteners;

		protected void fireChangeEvent() {
			for (SettingsChangeListener listener : directListeners) {
				listener.settingsModified();
			}
			if (uilisteners.size() > 0) {
				Intent i = new Intent(filter);
				LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i);
			}
		}
		
		public SettingsChangeListenerAggregator(String filter) {
			this.filter = filter;
			uilisteners = new Vector<BroadcastReceiver>();
			directListeners = new Vector<SettingsChangeListener>();
		}

		public void addDirectListener(SettingsChangeListener listener) {
			if (!directListeners.contains(listener)) {
				directListeners.add(listener);
			}
		}

		public void removeDirectListener(SettingsChangeListener listener) {
			directListeners.remove(listener);
		}

		public void addUIListener(BroadcastReceiver listener) {
			if (!uilisteners.contains(listener)) {
				uilisteners.add(listener);
				LocalBroadcastManager.getInstance(applicationContext).registerReceiver(listener, new IntentFilter(this.filter));
			}
		}

		public void removeUIListener(BroadcastReceiver listener) {
			uilisteners.remove(listener);
			LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(listener);
		}

	}
}
