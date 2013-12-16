package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class DataLoggerSerializer {
	
	BufferedWriter writer;
	HashMap<String, Integer> count;
	DataLogger manager;
	
	public DataLoggerSerializer(DataLogger manager) {
		this.writer = null;
		this.count = new HashMap<String, Integer>();
		this.manager = manager;
	}
	
	public boolean open(File file) {
		try {
			count.clear();
			this.writer = new BufferedWriter(new FileWriter(file));
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
	
	public void save(String sensorId, float[] values, int valueCount) {
		long t = System.currentTimeMillis();
		StringBuffer buffer = new StringBuffer();
		buffer.append(sensorId).append(',').append(t);
		for (int i = 0; i < valueCount; i++) {
			buffer.append(',').append(values[i]);
		}
		buffer.append('\n');
		try {
			writer.write(buffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int n = count.containsKey(sensorId) ? count.get(sensorId) + 1: 1;
		count.put(sensorId, n);
		manager.fireDataModified();
	}
	
	public HashMap<String, Integer> getCount() {
		return count;
	}
	
	
	
}
