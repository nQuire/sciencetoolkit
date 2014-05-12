package org.greengin.sciencetoolkit.spotit.ui.base.events;

import java.util.List;

public class SpotItEventManagerListener {
	public void events(List<String> settingsEvents, List<String> projectEvents, List<String> dataEvents, boolean whilePaused) {
		if (settingsEvents.size() > 0) {
			this.eventsSettings(settingsEvents, whilePaused);
		}
		if (projectEvents.size() > 0) {
			this.eventsProjects(projectEvents, whilePaused);
		}
		if (dataEvents.size() > 0) {
			this.eventsNewData(dataEvents, whilePaused);
		}
	}

	public void eventsSettings(List<String> settingsEvents, boolean whilePaused) {
		for (String e : settingsEvents) {
			this.eventSetting(e, whilePaused);
		}
	}

	public void eventsProjects(List<String> projectEvents, boolean whilePaused) {
		for (String e : projectEvents) {
			this.eventProject(e, whilePaused);
		}
	}

	public void eventsNewData(List<String> dataEvents, boolean whilePaused) {
		for (String e : dataEvents) {
			this.eventNewData(e, whilePaused);
		}
	}
	
	public void eventSetting(String event, boolean whilePaused) {
	}

	public void eventProject(String event, boolean whilePaused) {
	}

	public void eventNewData(String event, boolean whilePaused) {
	}
	
	public void eventDataStatus(String event, boolean whilePaused) {
	}
}
