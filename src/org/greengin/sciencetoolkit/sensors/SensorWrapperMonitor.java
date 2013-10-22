package org.greengin.sciencetoolkit.sensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SensorWrapperMonitor implements SettingsListener, SensorWrapperListener {
	public static final String MONITOR_REQUEST_SENSOR = "MONITOR_REQUEST_SENSOR";
	public static final String MONITOR_SCHEDULE_END = "MONITOR_SCHEDULE_END";

	public static final int MANUAL_RECORDING = 0;
	public static final int MANUAL_STOPPED = 1;
	public static final int SCHEDULED_STOPPED = 2;
	public static final int SCHEDULED_WAITING = 3;
	public static final int SCHEDULED_RECORDING = 4;

	SensorWrapper sensor;
	Context context;

	boolean enabled;
	boolean manual;
	boolean running;
	long scheduleStart;
	long scheduleEnd;
	long period;
	int minPeriod;

	Timer timer;
	MonitorTimerTask task;

	float[] currentValues;

	public SensorWrapperMonitor(SensorWrapper sensor, Context context) {
		this.sensor = sensor;
		this.context = context;
		this.enabled = false;
		this.manual = true;
		this.minPeriod = 10;
		this.period = 10;
		this.scheduleStart = System.currentTimeMillis() + 60000;
		this.scheduleEnd = this.scheduleStart + 60000;
		this.timer = null;
		this.task = null;

		LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (SensorWrapperMonitor.this.sensor.getName().equals(intent.getExtras().getString("sensor"))) {
					disableMonitor();
				}
			}
		}, new IntentFilter(SensorWrapperMonitor.MONITOR_SCHEDULE_END));
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getStatusStr() {
		return this.sensor.isEnabled() ? (this.enabled ? "running" : "stopped") : "sensor is disabled";
	}

	public int getStatus() {
		if (this.manual) {
			return this.enabled ? MANUAL_RECORDING : MANUAL_STOPPED;
		} else {
			if (this.enabled) {
				long current = System.currentTimeMillis();
				if (current < this.scheduleStart) {
					return SCHEDULED_WAITING;
				} else if (current > this.scheduleEnd) {
					return SCHEDULED_STOPPED;
				} else {
					return SCHEDULED_RECORDING;
				}
			} else {
				return SCHEDULED_STOPPED;
			}
		}
	}

	public void disableMonitor() {
		if (this.enabled) {
			this.timer.cancel();
			this.timer.purge();
			this.timer = null;
			this.task = null;
			this.sensor.removeListener(this);
			this.enabled = false;
			sensor.fireMonitorEvent();
		}
	}

	public void enableMonitor() {
		this.enabled = true;
		this.sensor.addListener(this);

		if (this.timer != null) {
			this.timer.cancel();
			this.timer.purge();
		}
		this.timer = new Timer();
		this.task = new MonitorTimerTask();
		long period = Math.max(this.period, this.minPeriod);

		if (this.manual) {
			timer.scheduleAtFixedRate(this.task, 0, period);
		} else {
			long start = scheduleStart;
			long current = System.currentTimeMillis();
			if (start < current) {
				long pn = (long) Math.ceil(((double)(current - start)) / period);
				start += pn * period;
			}
			
			timer.scheduleAtFixedRate(task, new Date(start), period);
		}

		sensor.fireMonitorEvent();
	}

	public void toggleMonitor() {
		if (this.enabled) {
			disableMonitor();
		} else {
			enableMonitor();
		}
	}

	@Override
	public List<Bundle> getOptions() {
		Vector<Bundle> list = new Vector<Bundle>();

		Bundle mode = new Bundle();
		mode.putString("key", "mode");
		mode.putString("name", "Mode");
		mode.putString("description", "Select whether this monitor is started manually or at a given time.");
		mode.putString("type", "select");
		mode.putStringArrayList("options", new ArrayList<String>(Arrays.asList("Manually", "Schedule")));
		list.add(mode);

		Bundle fromDate = new Bundle();
		fromDate.putString("key", "from");
		fromDate.putString("name", "From");
		fromDate.putString("requires", "scheduled");
		fromDate.putString("description", "The date when data recording will start.");
		fromDate.putString("type", "datetime");
		list.add(fromDate);

		Bundle untilDate = new Bundle();
		untilDate.putString("key", "until");
		untilDate.putString("name", "Unitl");
		untilDate.putString("requires", "scheduled");
		untilDate.putString("description", "The date when data recording will stop.");
		untilDate.putString("type", "datetime");
		list.add(untilDate);

		Bundle period = new Bundle();
		period.putString("key", "period");
		period.putString("type", "number");
		period.putString("name", "Period");
		period.putString("description", "Select the time (in ms) between data samples.");
		list.add(period);

		return list;
	}

	@Override
	public Object getOptionValue(String key) {
		if ("mode".equals(key)) {
			return this.manual ? 0 : 1;
		} else if ("period".equals(key)) {
			return this.period;
		} else if ("from".equals(key)) {
			return this.scheduleStart;
		} else if ("until".equals(key)) {
			return this.scheduleEnd;
		} else if ("scheduled".equals(key)) {
			return !this.manual;
		}
		return null;
	}

	@Override
	public boolean setOptionValue(String key, Object value) {
		boolean change = false;
		if ("mode".equals(key)) {
			this.manual = ((Integer) value) == 0;
			change = true;
		} else if ("period".equals(key)) {
			this.period = (Integer) value;
			change = true;
		} else if ("from".equals(key)) {
			this.scheduleStart = (Long) value;
			change = true;
		} else if ("until".equals(key)) {
			this.scheduleEnd = (Long) value;
			change = true;
		}

		if (change) {
			this.sensor.fireMonitorEvent();
		}

		return change;
	}

	private class MonitorTimerTask extends TimerTask {

		boolean needValue;

		public MonitorTimerTask() {
			needValue = false;
		}

		public void newValue() {
			if (needValue) {
				needValue = false;
				save();
			}
		}

		@Override
		public void run() {
			if (!manual && scheduleEnd < System.currentTimeMillis()) {
				Intent i = new Intent(SensorWrapperMonitor.MONITOR_SCHEDULE_END);
				i.putExtra("sensor", sensor.getName());
				LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			} else if (!sensor.isEnabled()) {
				Intent i = new Intent(SensorWrapperMonitor.MONITOR_REQUEST_SENSOR);
				i.putExtra("sensor", sensor.getName());
				LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			} else if (currentValues == null) {
				needValue = true;
			} else {
				save();
			}
		}

		public void save() {
			Log.d("monitor", "" + (System.currentTimeMillis() % 10000) + " " + currentValues[0]);
		}

	}

	@Override
	public void sensorStateUpdated() {
		if (!sensor.isEnabled()) {
			this.currentValues = null;
		}
	}

	@Override
	public void sensorValueUpdated() {
		if (this.currentValues == null) {
			this.currentValues = new float[sensor.getValueCount()];
		}
		for (int i = 0; i < currentValues.length; i++) {
			currentValues[i] = sensor.getValue()[i];
		}
		if (this.task != null) {
			this.task.newValue();
		}
	}

	@Override
	public void sensorMonitorUpdated() {
	}

}
