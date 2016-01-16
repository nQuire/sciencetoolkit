package org.greengin.sciencetoolkit.logic.datalogging.deprecated;

import org.greengin.sciencetoolkit.logic.streams.DataInput;

public class DeprecatedDataLoggingInput implements DataInput {

	String profileId;
	String sessionId;
	String sensorId;
	DeprecatedScienceToolkitSQLiteOpenHelper helper;

	public DeprecatedDataLoggingInput(String profileId, String sessionId, String sensorId, DeprecatedScienceToolkitSQLiteOpenHelper helper) {
		this.profileId = profileId;
		this.sessionId = sessionId;
		this.sensorId = sensorId;
		this.helper = helper;
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
		helper.save(profileId, sensorId, bf.toString());
	}
}
