package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.logic.location.CurrentLocation;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.streams.DataPipe;
import org.greengin.sciencetoolkit.logic.streams.filters.FixedRateDataFilter;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;
import org.greengin.sciencetoolkit.model.ModelOperations;
import org.greengin.sciencetoolkit.model.ProfileManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class DataLogger {
	private static final String DATA_LOGGING_NEW_DATA = "DATA_LOGGING_NEW_DATA";
	private static final String DATA_LOGGING_NEW_STATUS = "DATA_LOGGING_NEW_STATUS";

	private static DataLogger instance;

	public static void init(Context applicationContext) {
		instance = new DataLogger(applicationContext);
	}

	public static DataLogger get() {
		return instance;
	}

	ReentrantLock runningLock;
	ReentrantLock listenersLock;

	Context applicationContext;

	String profileId;
	Model profile;
	int series;
	File seriesFile;
	boolean running;
	boolean geolocated;
	Vector<DataPipe> pipes;
	Vector<DataLoggerDataListener> dataListeners;
	BroadcastReceiver dataReceiver;
	Vector<DataLoggerStatusListener> statusListeners;
	BroadcastReceiver statusReceiver;

	DataLoggerFileManager fileManager;
	DataLoggerSerializer serializer;

	public DataLogger(Context applicationContext) {
		this.applicationContext = applicationContext;

		runningLock = new ReentrantLock();
		listenersLock = new ReentrantLock();
		pipes = new Vector<DataPipe>();
		fileManager = new DataLoggerFileManager(applicationContext);
		series = 0;
		serializer = new DataLoggerSerializer(this);

		dataListeners = new Vector<DataLoggerDataListener>();
		statusListeners = new Vector<DataLoggerStatusListener>();

		dataReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String msg = intent.getExtras().getString("msg");
				for (DataLoggerDataListener listener : dataListeners) {
					listener.dataLoggerDataModified(msg);
				}
			}
		};
		statusReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				for (DataLoggerStatusListener listener : statusListeners) {
					listener.dataLoggerStatusModified();
				}
			}
		};
	}

	public void registerDataListener(DataLoggerDataListener listener) {
		listenersLock.lock();
		if (!dataListeners.contains(listener)) {
			dataListeners.add(listener);
			if (dataListeners.size() == 1) {
				LocalBroadcastManager.getInstance(applicationContext).registerReceiver(dataReceiver, new IntentFilter(DataLogger.DATA_LOGGING_NEW_DATA));
			}
		}

		listenersLock.unlock();
	}

	public void unregisterDataListener(DataLoggerDataListener listener) {
		listenersLock.lock();

		if (dataListeners.remove(listener) && dataListeners.size() == 0) {
			LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(dataReceiver);
		}
	}

	public void registerStatusListener(DataLoggerStatusListener listener) {
		listenersLock.lock();
		if (!statusListeners.contains(listener)) {
			statusListeners.add(listener);
			if (statusListeners.size() == 1) {
				LocalBroadcastManager.getInstance(applicationContext).registerReceiver(statusReceiver, new IntentFilter(DataLogger.DATA_LOGGING_NEW_STATUS));
			}
		}

		listenersLock.unlock();
	}

	public void unregisterStatusListener(DataLoggerStatusListener listener) {
		listenersLock.lock();
		if (statusListeners.remove(listener) && statusListeners.size() == 0) {
			LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(statusReceiver);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void startNewSeries() {
		runningLock.lock();
		if (!running) {

			profile = ProfileManager.get().getActiveProfile();
			profileId = profile.getString("id");
			geolocated = profile.getBool("requires_location");
			if (geolocated) {
				CurrentLocation.get().startlocation();
			}
			
			pipes.clear();

			Vector<Model> sensors = profile.getModel("sensors", true).getModels();
			if (sensors.size() > 0) {
				running = true;

				series = fileManager.startNewSeries(profileId);
				seriesFile = fileManager.getCurrentSeriesFile(profileId);
				serializer.open(seriesFile, profile);

				for (Model profileSensor : sensors) {
					String profileSensorId = profileSensor.getString("id");
					String sensorId = profileSensor.getString("sensorid");
					SensorWrapper sensor = SensorWrapperManager.get().getSensor(sensorId);
					int period = ModelOperations.rate2period(profileSensor, "sample_rate", ModelDefaults.DATA_LOGGING_RATE, null, ModelDefaults.DATA_LOGGING_RATE_MAX);

					if (sensor != null) {
						DataPipe pipe = new DataPipe(sensor);
						pipe.addFilter(new FixedRateDataFilter(period));
						pipe.setEnd(new DataLoggingInput(profileId, profileSensorId, sensorId, serializer));
						pipes.add(pipe);
					}
				}

				for (DataPipe pipe : pipes) {
					pipe.attach();
				}

				fireStatusModified();
			}
		}
		runningLock.unlock();
	}

	public void stopSeries() {
		runningLock.lock();

		if (running) {
			for (DataPipe pipe : pipes) {
				pipe.detach();
			}
			pipes.clear();

			serializer.close();
			
			if (geolocated) {			
				CurrentLocation.get().stoplocation();
				String loc = CurrentLocation.get().locationString();
				DataLogger.get().getSeriesMetadata(profileId, seriesFile).setString("location", loc);
			}
			
			running = false;
			fireStatusModified();

		}

		runningLock.unlock();
	}

	public int getCurrentSeries() {
		return series;
	}

	public int getSeriesCount(String profileId) {
		return this.fileManager.seriesCount(profileId);
	}

	public File[] getSeries(String profileId) {
		return this.fileManager.series(profileId);
	}

	public HashMap<String, Integer> getCurrentSeriesSampleCount() {
		return this.serializer.getCount();
	}

	public void deleteAllData() {
		// this.helper.emptyData(null);
	}

	public void deleteData(String profileId) {
		this.fileManager.deleteSeries(profileId);
		Model profile = ProfileManager.get().get(profileId);
		if (profile != null) {
			profile.clear("series", true);
			ProfileManager.get().forceSave();
		}
		this.fireStatusModified();
	}

	public void deleteData(String profileId, File series) {
		this.fileManager.deleteSeries(profileId, series);
		Model profile = ProfileManager.get().get(profileId);
		if (profile != null) {
			profile.getModel("series", true).clear(series.getName(), true);
			ProfileManager.get().forceSave();
		}
		this.fireStatusModified();
	}

	public void markAsSent(String profileId, File series, boolean sent) {
		Model profile = ProfileManager.get().get(profileId);

		if (profile != null) {
			Model seriesModel = profile.getModel("series", true).getModel(series.getName(), true, true);
			if (sent) {
				seriesModel.setBool("uploaded", true, true);
			} else {
				seriesModel.clear(series.getName(), true);
			}

			ProfileManager.get().forceSave();
			this.fireStatusModified();
		}
	}

	public boolean isSent(String profileId, File series) {
		Model profile = ProfileManager.get().get(profileId);
		return profile != null && profile.getModel("series", true).getModel(series.getName(), true).getBool("uploaded", false);
	}

	public Model getSeriesMetadata(String profileId, File series) {
		Model profile = ProfileManager.get().get(profileId);
		return profile.getModel("series", true).getModel("metadata", true);
	}

	private void fireStatusModified() {
		if (statusListeners.size() > 0) {
			Intent intent = new Intent(DataLogger.DATA_LOGGING_NEW_STATUS);
			LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
		}
	}

	public void fireDataModified() {
		if (dataListeners.size() > 0) {
			Intent intent = new Intent(DataLogger.DATA_LOGGING_NEW_DATA);
			intent.putExtra("msg", profileId);
			LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent);
		}
	}

	public boolean getRange(long[] values, String profileId) {
		values[0] = values[1] = 0;
		return false;
	}

}