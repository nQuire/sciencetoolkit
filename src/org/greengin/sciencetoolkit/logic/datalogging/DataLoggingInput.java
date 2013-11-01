package org.greengin.sciencetoolkit.logic.datalogging;

import org.greengin.sciencetoolkit.logic.streams.DataInput;

import android.util.Log;

public class DataLoggingInput implements DataInput {

	String profileId;
	String sessionId;
	String sensorId;
	DataLogger logger;

	public DataLoggingInput(String profileId, String sessionId, String sensorId, DataLogger logger) {
		this.profileId = profileId;
		this.sessionId = sessionId;
		this.sensorId = sensorId;
		this.logger = logger;
	}

	@Override
	public void value(float[] values, int valueCount) {
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < valueCount; i++) {
			if (i > 0) {
				bf.append("|");
			}

			bf.append(values[i]);
		}
		logger.dataAdded(sensorId);
		Log.d("stk data", this.profileId + " " + this.sessionId + " " + this.sensorId + " " + bf.toString());
	}

}
