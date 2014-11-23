package org.greengin.sciencetoolkit.logic.datalogging;

import java.util.Vector;

import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.logic.streams.DataInput;

public class DataLoggingInput implements DataInput {

	String profileId;
	String profileSensorId;
	String sensorId;
	DataLoggerSerializer serializer;
	Vector<TimeValue> valueRecord;
	
	public DataLoggingInput(String profileId, String profileSensorId, String sensorId, DataLoggerSerializer serializer, Vector<TimeValue> valueRecord) {
		this.profileId = profileId;
		this.profileSensorId = profileSensorId;
		this.sensorId = sensorId;
		this.serializer = serializer;
		this.valueRecord = valueRecord;
	}

	@Override
	public void value(float[] values, int valueCount) {
		this.valueRecord.add(new TimeValue(System.currentTimeMillis(), values));
		this.serializer.save(sensorId, profileSensorId, values, valueCount);
	}
}
