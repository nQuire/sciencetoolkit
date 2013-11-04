package org.greengin.sciencetoolkit.logic.datalogging;

import java.util.Vector;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class AsynchSQLiteWriterRunnable implements Runnable {

	SQLiteDatabase db;
	Vector<ProfileSensorTimeValue> pendingValues;
	Thread thread;
	DataLoggerListener listener;

	public AsynchSQLiteWriterRunnable(SQLiteDatabase db, DataLoggerListener listener) {
		this.db = db;
		this.thread = null;
		this.pendingValues = new Vector<ProfileSensorTimeValue>();
		this.listener = listener;
	}

	public void addData(String profileId, String sensorId, String data) {
		this.pendingValues.add(new ProfileSensorTimeValue(profileId, sensorId, data));
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		Vector<ProfileSensorTimeValue> toWrite = pendingValues;
		pendingValues = new Vector<ProfileSensorTimeValue>();
		Vector<String> profiles = new Vector<String>();

		try {
			db.beginTransaction();
			for (ProfileSensorTimeValue v : toWrite) {
				ContentValues cv = new ContentValues();
				cv.put("profile", v.profileId);
				cv.put("sensor", v.sensorId);
				cv.put("timestamp", v.time);
				cv.put("data", v.data);
				
				if (!profiles.contains(v.profileId)) {
					profiles.add(v.profileId);
				}

				db.insert(ScienceToolkitSQLiteOpenHelper.DATA_TABLE_NAME, null, cv);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		
		for (String profile : profiles) {
			listener.dataLoggerDataModified(profile);
		}
		this.thread = null;
	}

	class ProfileSensorTimeValue {
		long time;
		String profileId;
		String sensorId;
		String data;

		public ProfileSensorTimeValue(String profileId, String internalSensorId, String data) {
			this.time = System.currentTimeMillis();
			this.profileId = profileId;
			this.sensorId = internalSensorId;
			this.data = data;
		}
	}

}
