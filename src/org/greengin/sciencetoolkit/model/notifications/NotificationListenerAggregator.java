package org.greengin.sciencetoolkit.model.notifications;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class NotificationListenerAggregator extends BroadcastReceiver {
	ReentrantLock lock;
	String filter;
	Vector<ModelNotificationListener> directListeners;
	Vector<ModelNotificationListener> uilisteners;
	boolean uilisteneractivated;

	Context applicationContext;


	public NotificationListenerAggregator(Context applicationContext, String filter) {
		this.applicationContext = applicationContext;
		this.filter = filter;
		uilisteners = new Vector<ModelNotificationListener>();
		directListeners = new Vector<ModelNotificationListener>();
		lock = new ReentrantLock();
		uilisteneractivated = false;
	}

	public void fireEvent(String msg) {
		for (ModelNotificationListener listener : directListeners) {
			listener.modelNotificationReceived(msg);
		}
		
		if (uilisteneractivated) {
			Intent i = new Intent(filter);
			i.putExtra("msg", msg);
			LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i);
		}
	}

	public void addDirectListener(ModelNotificationListener listener) {
		if (!directListeners.contains(listener)) {
			directListeners.add(listener);
		}
	}

	public void removeDirectListener(ModelNotificationListener listener) {
		directListeners.remove(listener);
	}

	public void addUIListener(ModelNotificationListener listener) {
		lock.lock();
		if (!uilisteners.contains(listener)) {
			uilisteners.add(listener);
			if (!uilisteneractivated) {
				uilisteneractivated = true;
				LocalBroadcastManager.getInstance(applicationContext).registerReceiver(this, new IntentFilter(this.filter));
			}
		}
		lock.unlock();
	}

	public void removeUIListener(ModelNotificationListener listener) {
		lock.lock();
		uilisteners.remove(listener);
		if (uilisteners.size() == 0) {
			uilisteneractivated = false;
			LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(this);
		}
		lock.unlock();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String msg = intent.getExtras().getString("msg");
		for (ModelNotificationListener listener : uilisteners) {
			listener.modelNotificationReceived(msg);
		}
	}

}
