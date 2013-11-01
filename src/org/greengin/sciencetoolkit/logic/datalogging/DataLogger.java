package org.greengin.sciencetoolkit.logic.datalogging;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.model.SettingsManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class DataLogger extends BroadcastReceiver {
	private static final String DATA_LOGGING_CURRENT_SESSION = "data_logging_current_session";
	
	private static DataLogger instance;
	
	public static void init(Context applicationContext) {
		instance = new DataLogger(applicationContext);
	}
	
	public static DataLogger getInstance() {
		return instance;
	}
	
	ReentrantLock runningLock;
	ReentrantLock listenersLock;
	
	Context applicationContext;
	
	String profileId;
	Model profile;
	boolean running;
	Vector<DataPipe> pipes;
	Hashtable<String, Integer> dataCount;
	Vector<CurrentSessionDataListener> listeners;
	ScienceToolkitSQLiteOpenHelper helper;
	
	public DataLogger(Context applicationContext) {
		this.applicationContext = applicationContext;
		
		runningLock = new ReentrantLock();
		listenersLock = new ReentrantLock();
		pipes = new Vector<DataPipe>();
		dataCount = new Hashtable<String, Integer>();
		listeners = new Vector<CurrentSessionDataListener>();
		helper = new ScienceToolkitSQLiteOpenHelper(applicationContext);
	}
	
	public void registerListener(CurrentSessionDataListener listener) {
		listenersLock.lock();
		if (!listeners.contains(listener)) {
			listeners.add(listener);
			if (listeners.size() == 1) {
				LocalBroadcastManager.getInstance(applicationContext).registerReceiver(this, new IntentFilter(DataLogger.DATA_LOGGING_CURRENT_SESSION));
			}
		}
		
		listenersLock.unlock();		
	}
	
	public void unregisterListener(CurrentSessionDataListener listener) {
		listenersLock.lock();
		listeners.remove(listener);
		if (listeners.size() == 0) {
			LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(this);
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	private void setProfile(Model profile) {
		this.profile = profile;
		this.profileId = profile.getString("id");
		this.running = false;
	}
	
	public void start() {
		start(ProfileManager.getInstance().getActiveProfile());
	}
	
	private void start(Model profile) {
		runningLock.lock();
		if (!running) {
			setProfile(profile);
			running = true;
			dataCount.clear();
			pipes.clear();
			for (Model profileSensor : profile.getModel("sensors", true).getModels()) {
				String sensorId = profileSensor.getString("id");
				SensorWrapper sensor = SensorWrapperManager.getInstance().getSensor(sensorId);
				int period = profileSensor.getInt("period", ModelDefaults.DATA_LOGGING_PERIOD);
				Model profileSensorSettings = profileSensor.getModel("sensor_settings");
				Model globalSensorSettings = SettingsManager.getInstance().get("sensor:" + sensorId);
				globalSensorSettings.copyPrimitives(profileSensorSettings, false);
				
				if (sensor != null) {
					DataPipe pipe = new DataPipe(sensor);
					pipe.addFilter(new FixedRateDataFilter(period));
					pipe.setEnd(new DataLoggingInput(profileId, "*", sensorId, this));
					pipes.add(pipe);
					dataCount.put(sensorId, 0);
				}
			}
			
			for (DataPipe pipe : pipes) {
				pipe.attach();
			}
			
		}
		runningLock.unlock();
	}
	
	public void stop() {
		runningLock.lock();
		
		if (running) {
			for (DataPipe pipe : pipes) {
				pipe.detach();
			}
			pipes.clear();			
			running = false;
		}
		
		runningLock.unlock();
	}
	
	public Hashtable<String, Integer> getValueCount() {
		return this.dataCount;
	}

	public void dataAdded(String sensorId) {
		dataCount.put(sensorId, dataCount.get(sensorId) + 1);
		if (listeners.size() > 0) {
			Intent intent = new Intent(DataLogger.DATA_LOGGING_CURRENT_SESSION);
			LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		for (CurrentSessionDataListener listener : listeners) {
			listener.currentSessionDataAdded();
		}
	}
	
}
