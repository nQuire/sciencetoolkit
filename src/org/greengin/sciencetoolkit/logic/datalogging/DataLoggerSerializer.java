package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;

public class DataLoggerSerializer {
	
	BufferedWriter writer;
	HashMap<String, Integer> countMap;
	int count;
	DataLogger manager;
	
	public DataLoggerSerializer(DataLogger manager) {
		this.writer = null;
		this.countMap = new HashMap<String, Integer>();
		this.manager = manager;
	}
	
	public boolean open(File file, Model profile) {
		try {
			countMap.clear();
			count = 0;
			
			this.writer = new BufferedWriter(new FileWriter(file));
			this.writer.write(String.format("# profile: %s\n", profile.getString("id")));
			
			for (Model profileSensor : profile.getModel("sensors", true).getModels()) {
				String sensorId = profileSensor.getString("sensorid");
				SensorWrapper sensor = SensorWrapperManager.get().getSensor(sensorId);
				this.writer.write(String.format("# sensor: %s %s %s\n", profileSensor.getString("id"), sensorId, sensor.getName()));
			}
			
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean close() {
		try {
			this.writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public void save(String sensorId, String sensorProfileId, float[] values, int valueCount) {
		long t = System.currentTimeMillis();
		StringBuffer buffer = new StringBuffer();
		buffer.append(sensorProfileId).append(',').append(t);
		for (int i = 0; i < valueCount; i++) {
			buffer.append(',').append(values[i]);
		}
		buffer.append('\n');
		try {
			writer.write(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int n = countMap.containsKey(sensorId) ? countMap.get(sensorId) + 1: 1;
		countMap.put(sensorId, n);
		count ++;
		manager.fireDataModified();
	}
	
	public HashMap<String, Integer> getCountMap() {
		return countMap;
	}
	
	public int getCount() {
		return count;
	}
	
	
	
}
