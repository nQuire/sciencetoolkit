package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
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

		int n = countMap.containsKey(sensorId) ? countMap.get(sensorId) + 1 : 1;
		countMap.put(sensorId, n);
		count++;
		manager.fireDataModified(sensorProfileId);
	}

	public HashMap<String, Integer> getCountMap() {
		return countMap;
	}

	public int getCount() {
		return count;
	}

	public HashMap<String, String> getSensorsInSeries(File series) {
		HashMap<String, String> sensors = new HashMap<String, String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(series));
			while (true) {
				String line = reader.readLine();
				if (line == null || !line.startsWith("#")) {
					break;
				} else if (line.startsWith("# sensor:")) {
					String[] parts = line.split(" ", 5);
					if (parts.length == 5) {
						sensors.put(parts[3], parts[2]);
					}
				}
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sensors;
	}

	public long duration(File series) {
		long a = timestamp(firstDataLine(series));
		long b = timestamp(lastDataLine(series));
		return a >= 0 && b >= 0 ? b - a : -1;
	}

	private String firstDataLine(File series) {
		String result = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(series));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				} else if (line.length() > 0 && !line.startsWith("#")) {
					result = line;
					break;
				}
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private long timestamp(String line) {
		if (line != null) {
			String[] parts = line.split(",");
			if (parts.length > 2) {
				try {
					return Long.parseLong(parts[1]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}

		return -1l;
	}

	/**
	 * From:
	 * http://stackoverflow.com/questions/686231/quickly-read-the-last-line
	 * -of-a-text-file			Log.d("stk", "!!!");

	 * 
	 * @param series
	 * @return
	 */
	private String lastDataLine(File series) {
		RandomAccessFile fileHandler = null;
		try {
			fileHandler = new RandomAccessFile(series, "r");
			long fileLength = fileHandler.length() - 1;
			StringBuilder sb = new StringBuilder();

			for (long filePointer = fileLength; filePointer != -1; filePointer--) {
				fileHandler.seek(filePointer);
				int readByte = fileHandler.readByte();

				if (readByte == 0xA) {
					if (filePointer == fileLength) {
						continue;
					} else {
						break;
					}
				} else if (readByte == 0xD) {
					if (filePointer == fileLength - 1) {
						continue;
					} else {
						break;
					}
				}

				sb.append((char) readByte);
			}
			
			return sb.reverse().toString();
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (fileHandler != null) {
				try {
					fileHandler.close();
				} catch (IOException e) {
					/* ignore */
				}
			}
		}
	}

}
