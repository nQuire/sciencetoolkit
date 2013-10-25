package org.greengin.sciencetoolkit.data;

import java.util.Vector;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AsynchDataRunnable implements Runnable {

	SQLiteDatabase db;
	Vector<SensorTimeValue> pendingValues;
	Thread thread;

	public AsynchDataRunnable(SQLiteDatabase db) {
		this.db = db;
		this.thread = null;
		pendingValues = new Vector<SensorTimeValue>();
	}

	public void addData(String sensorId, float[] values, int valueCount) {
		Log.d("stkdb", "async db adddata");
		this.pendingValues.add(new SensorTimeValue(sensorId, values, valueCount));
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void run() {
		Log.d("stkdb", "async db run");
		
		Vector<SensorTimeValue> toWrite = pendingValues;
		pendingValues = new Vector<SensorTimeValue>();

		try {
			db.beginTransaction();
			for (int i = 0; i < toWrite.size(); i++) {
				SensorTimeValue v = toWrite.get(i);
				ContentValues cv = new ContentValues();
				cv.put("sensor", v.sensorId);
				cv.put("timestamp", v.time);
				cv.put("data", v.data);

				db.insert(ScienceToolkitSQLiteOpenHelper.DATA_TABLE_NAME, null, cv);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		
		Log.d("stkdb", "async db end");
		this.thread = null;
	}

	class SensorTimeValue {
		long time;
		String sensorId;
		String data;

		public SensorTimeValue(String sensorId, float[] values, int valueCount) {
			this.time = System.currentTimeMillis();
			this.sensorId = sensorId;

			StringBuffer bf = new StringBuffer();
			for (int i = 0; i < valueCount; i++) {
				if (i > 0) {
					bf.append("|");
				}

				bf.append(values[i]);
			}
			this.data = bf.toString();
		}
	}

}
