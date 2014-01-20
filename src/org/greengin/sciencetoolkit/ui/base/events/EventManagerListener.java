package org.greengin.sciencetoolkit.ui.base.events;

import java.util.List;

public class EventManagerListener {
	public void events(List<String> settingsEvents, List<String> profileEvents, List<String> dataEvents, boolean whilePaused) {
		if (settingsEvents.size() > 0) {
			this.eventsSettings(settingsEvents, whilePaused);
		}
		if (settingsEvents.size() > 0) {
			this.eventsProfiles(profileEvents, whilePaused);
		}
		if (settingsEvents.size() > 0) {
			this.eventsData(dataEvents, whilePaused);
		}
	}

	public void eventsSettings(List<String> settingsEvents, boolean whilePaused) {
		for (String e : settingsEvents) {
			this.eventSetting(e, whilePaused);
		}
	}

	public void eventsProfiles(List<String> profileEvents, boolean whilePaused) {
		for (String e : profileEvents) {
			this.eventProfile(e, whilePaused);
		}
	}

	public void eventsData(List<String> dataEvents, boolean whilePaused) {
		for (String e : dataEvents) {
			this.eventData(e, whilePaused);
		}
	}

	public void eventSetting(String event, boolean whilePaused) {
	}

	public void eventProfile(String event, boolean whilePaused) {
	}

	public void eventData(String event, boolean whilePaused) {
	}
}
