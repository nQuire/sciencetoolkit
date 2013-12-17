package org.greengin.sciencetoolkit.logic.datalogging;

import org.greengin.sciencetoolkit.logic.streams.DataInput;

public class DataLoggingInput implements DataInput {

	String profileId;
	String profileSensorId;
	String sensorId;
	DataLoggerSerializer serializer;
	
	public DataLoggingInput(String profileId, String profileSensorId, String sensorId, DataLoggerSerializer serializer) {
		this.profileId = profileId;
		this.profileSensorId = profileSensorId;
		this.sensorId = sensorId;
		this.serializer = serializer;
	}

	@Override
	public void value(float[] values, int valueCount) {
		this.serializer.save(sensorId, profileSensorId, values, valueCount);
	}

}
