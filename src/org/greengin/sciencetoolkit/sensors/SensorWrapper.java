package org.greengin.sciencetoolkit.sensors;

import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public abstract class SensorWrapper implements SettingsListener {

	boolean enabled;
	int formatMinInt;
	SensorWrapperMonitor monitor;
	Context context;
	
	String[] valueUnits;

	Vector<SensorWrapperListener> listeners;

	public SensorWrapper(Context context) {
		this.enabled = false;
		this.listeners = new Vector<SensorWrapperListener>();
		this.monitor = new SensorWrapperMonitor(this, context);
		this.context = context;
		
		LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (getName().equals(intent.getExtras().getString("sensor"))) {
					setEnabled(true);
				}
			}
		}, new IntentFilter(SensorWrapperMonitor.MONITOR_REQUEST_SENSOR));
	}
	
	protected void setValueUnits(String[] units) {
		this.valueUnits = units;
	}

	protected void updateMaxRange() {
		this.formatMinInt = 1 + Math.max(0, (int) Math.ceil(Math.log(Math.abs(this.getMaxRange()))));
	}

	public SensorWrapperMonitor getMonitor() {
		return this.monitor;
	}

	public void addListener(SensorWrapperListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeListener(SensorWrapperListener listener) {
		this.listeners.remove(listener);
	}

	public void fireStateEvent() {
		for (SensorWrapperListener listener : this.listeners) {
			listener.sensorStateUpdated();
		}
	}

	public void fireValueEvent() {
		for (SensorWrapperListener listener : this.listeners) {
			listener.sensorValueUpdated();
		}
	}

	public void fireMonitorEvent() {
		for (SensorWrapperListener listener : this.listeners) {
			listener.sensorMonitorUpdated();
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		if (enabled != this.enabled) {
			this.enabled = enabled;
			this.fireStateEvent();
		}
	}
	abstract public int getType();

	abstract public String getName();

	abstract public int getValueCount();

	abstract public float[] getValue();

	abstract public float getResolution();

	abstract public int getMinDelay();

	abstract public float getMaxRange();
	
	public abstract String[] getValueLabels();
	
	public String[] getValueUnits() {
		return this.valueUnits;
	}

	public String getValueStr() {
		return this.formatValue(this.getValue(), this.getValueCount());
	}

	protected String formatValue(float[] value, int count) {
		if (value == null) {
			return "";
		} else {
			StringBuffer buff = new StringBuffer();
			for (int i = 0; i < count; i++) {
				if (i > 0) {
					buff.append("\n");
				}

				String vstr = Float.toString(value[i]);
				int p = vstr.indexOf('.');
				if (p < 0) {
					p = vstr.length();
				}
				for (int j = p; j < this.formatMinInt; j++) {
					buff.append(' ');
				}

				buff.append(vstr).append(' ').append(this.valueUnits[i]);
			}
			return buff.toString();
		}
	}
}
