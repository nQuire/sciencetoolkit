package org.greengin.sciencetoolkit.ui.base.events;

import java.util.List;

public class EventManagerListener {
	public void events(List<String> settingsEvents, List<String> profileEvents, List<String> dataEvents, List<String> dataStatusEvents, boolean whilePaused) {
		if (settingsEvents.size() > 0) {
			this.eventsSettings(settingsEvents, whilePaused);
		}
		if (profileEvents.size() > 0) {
			this.eventsProfiles(profileEvents, whilePaused);
		}
		if (dataEvents.size() > 0) {
			this.eventsNewData(dataEvents, whilePaused);
		}
		if (dataStatusEvents.size() > 0) {
			this.eventsDataStatus(dataStatusEvents, whilePaused);
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

	public void eventsNewData(List<String> dataEvents, boolean whilePaused) {
		for (String e : dataEvents) {
			this.eventNewData(e, whilePaused);
		}
	}
	
	public void eventsDataStatus(List<String> dataStatusEvents, boolean whilePaused) {
		for (String e : dataStatusEvents) {
			this.eventDataStatus(e, whilePaused);
		}
	}

	public void eventSetting(String event, boolean whilePaused) {
	}

	public void eventProfile(String event, boolean whilePaused) {
	}

	public void eventNewData(String event, boolean whilePaused) {
	}
	
	public void eventDataStatus(String event, boolean whilePaused) {
	}
}
