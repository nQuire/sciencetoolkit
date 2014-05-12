package org.greengin.sciencetoolkit.spotit.ui.base.events;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.common.model.SettingsManager;
import org.greengin.sciencetoolkit.common.model.events.ModelNotificationListener;
import org.greengin.sciencetoolkit.common.ui.base.events.EventManager;
import org.greengin.sciencetoolkit.spotit.logic.data.DataLoggerDataListener;
import org.greengin.sciencetoolkit.spotit.logic.data.DataManager;
import org.greengin.sciencetoolkit.spotit.model.ProjectManager;

public class SpotItEventManager implements EventManager {
	SpotItEventManagerListener listener;
	boolean isPaused;
	boolean newEvents;

	Vector<String> settingListeners;
	boolean projectListener;
	boolean loggedDataListener;
	boolean loggerStatusListener;

	Vector<String> settings;
	Vector<String> profiles;
	Vector<String> data;

	ReentrantLock lock;

	SettingsListener settingsListener;
	ProfilesListener profilesListener;
	DataListener dataListener;

	public SpotItEventManager() {
		this.isPaused = true;
		this.newEvents = false;

		this.listener = null;
		this.settingListeners = new Vector<String>();
		this.projectListener = false;
		this.loggedDataListener = false;
		this.loggerStatusListener = false;

		this.settings = new Vector<String>();
		this.profiles = new Vector<String>();
		this.data = new Vector<String>();

		this.settingsListener = new SettingsListener();
		this.profilesListener = new ProfilesListener();
		this.dataListener = new DataListener();

		this.lock = new ReentrantLock();
	}

	public void setListener(SpotItEventManagerListener listener) {
		this.listener = listener;
	}

	public void listenToSettings(String settingsId) {
		if (!settingListeners.contains(settingsId)) {
			settingListeners.add(settingsId);
			SettingsManager.get().registerUIListener(settingsId, settingsListener);
		}
	}

	public void stopListeningToSettings(String settingsId) {
		SettingsManager.get().unregisterUIListener(settingsId, settingsListener);
		settingListeners.remove(settingsId);
	}

	public void listenToProfiles() {
		if (!projectListener) {
			ProjectManager.get().registerUIListener(profilesListener);
			projectListener = true;
		}
	}

	public void stopListeningToProfiles() {
		ProjectManager.get().unregisterUIListener(profilesListener);
		projectListener = false;
	}

	public void listenToLoggedData() {
		if (!loggedDataListener) {
			DataManager.get().registerDataListener(dataListener);
			loggedDataListener = true;
		}
	}

	public void stopListeningToLoggedData() {
		DataManager.get().unregisterDataListener(dataListener);
		loggedDataListener = false;
	}
	
	@Override
	public void destroy() {
		for (String sid : settingListeners) {
			SettingsManager.get().unregisterUIListener(sid, settingsListener);
		}
		settingListeners.clear();
		
		if (projectListener) {
			stopListeningToProfiles();
		}
		
		if (loggedDataListener) {
			stopListeningToLoggedData();
		}
		
	}

	private void addEvent(Vector<String> container, String event) {
		lock.lock();
		if (!container.contains(event)) {
			container.add(event);

			if (isPaused) {
				newEvents = true;
			} else {
				notifyEvents(false);
			}
		}
		lock.unlock();
	}

	@Override
	public void pause() {
		lock.lock();
		this.isPaused = true;
		lock.unlock();
	}

	@Override
	public void resume() {
		lock.lock();
		this.isPaused = false;
		if (this.newEvents) {
			this.newEvents = false;
			notifyEvents(true);
		}
		lock.unlock();
	}

	private void notifyEvents(boolean whilePaused) {
		if (listener != null) {
			listener.events(settings, profiles, data, whilePaused);
		}
		settings.clear();
		profiles.clear();
		data.clear();
	}

	private class SettingsListener implements ModelNotificationListener {
		@Override
		public void modelNotificationReceived(String msg) {
			addEvent(settings, msg);
		}
	}

	private class ProfilesListener implements ModelNotificationListener {
		@Override
		public void modelNotificationReceived(String msg) {
			addEvent(profiles, msg);
		}
	}

	private class DataListener implements DataLoggerDataListener {
		@Override
		public void dataLoggerDataEvent(String msg) {
			addEvent(data, msg);
		}
	}
	

}
