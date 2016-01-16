package org.greengin.sciencetoolkit.common.logic.appstatus;

import java.util.Vector;


import android.content.Context;

public class ApplicationStatusManager {

	private static ApplicationStatusManager instance;
	private Vector<ApplicationStatusListener> listeners;
	
	boolean awake;

	public static void init(Context applicationContext) {
		instance = new ApplicationStatusManager();
	}

	public static ApplicationStatusManager get() {
		return instance;
	}

	public ApplicationStatusManager() {
		this.listeners = new Vector<ApplicationStatusListener>();
	}

	
	public boolean isAwake() {
		return awake;
	}
	
	public void registerStatusListener(ApplicationStatusListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void unregisterStatusListener(ApplicationStatusListener listener) {
		listeners.remove(listener);
	}

	public void setAwake(boolean awake) {
		if (awake != this.awake) {
		this.awake = awake;
		for (ApplicationStatusListener listener : listeners) {
			listener.applicationStatusEvent(this.awake);
		}
		}
		
	}

}
