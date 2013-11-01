package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class ScienceToolkitSQLiteOpenHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "sciencetoolkit";

	public static final String SENSORS_TABLE_NAME = "sensors";
	public static final String SENSORS_TABLE_CREATE = "CREATE TABLE " + SENSORS_TABLE_NAME + " (name TEXT);";
	public static final String[] SENSORS_TABLE_QUERY_COLUMNS = new String[] { "ROWID" };
	public static final String SENSORS_TABLE_QUERY_WHERE = "name=?";

	// v1: 	CREATE TABLE data (sensor INTEGER, timestamp INTEGER, data TEXT);
	// v2: 	CREATE TABLE data (profile INTEGER, sensor INTEGER, timestamp INTEGER, data TEXT);

	public static final String DATA_TABLE_NAME = "data";
	public static final String DATA_TABLE_CREATE = "CREATE TABLE " + DATA_TABLE_NAME + " (profile INTEGER, sensor INTEGER, timestamp INTEGER, data TEXT);";
	public static final String[] DATA_TABLE_QUERY_COUNT_COLUMNS = new String[] { "Count(ROWID)" };
	public static final String[] DATA_TABLE_QUERY_ALL_COLUMNS = new String[] { "profile", "sensor", "timestamp", "data" };
	public static final String DATA_TABLE_QUERY_WHERE_PROFILE = "profile=?";
	public static final String DATA_TABLE_QUERY_WHERE_PROFILE_SENSOR = "profile=? AND sensor=?";
	public static final String DATA_TABLE_DELETE_ALL = "DELETE FROM data";

	Context context;
	HashMap<String, String> sensorIdsCache;
	AsynchSQLiteWriterRunnable runnable;
	
	public ScienceToolkitSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.sensorIdsCache = new HashMap<String, String>();
		this.context = context;
		this.runnable = new AsynchSQLiteWriterRunnable(getWritableDatabase());
	}

	/*public void fireDataEvent() {
		Intent i = new Intent(DataManager.DATA_MODIFIED);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}*/

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SENSORS_TABLE_CREATE);
		db.execSQL(DATA_TABLE_CREATE);
		Log.d("stkdb", "tables created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		if (oldV < 2) {
			db.execSQL("ALTER TABLE " + DATA_TABLE_NAME + " ADD COLUMN profile INTEGER");
			db.execSQL("UPDATE " + DATA_TABLE_NAME + " SET profile='1'");
		}
	}

	protected String getInternalSensorId(String sensorId) {
		Log.d("stkdb", "request id: " + sensorId);
		if (!this.sensorIdsCache.containsKey(sensorId)) {
			String internalId;
			Cursor cursor = getReadableDatabase().query(SENSORS_TABLE_NAME, SENSORS_TABLE_QUERY_COLUMNS, SENSORS_TABLE_QUERY_WHERE, new String[] { sensorId }, null, null, null, "1");
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				internalId = cursor.getString(0);
				Log.d("stkdb", "found in db: " + internalId);
			} else {
				ContentValues values = new ContentValues();
				values.put("name", sensorId);
				internalId = "" + getWritableDatabase().insert(SENSORS_TABLE_NAME, null, values);
				Log.d("stkdb", "added in db: " + internalId);
			}

			this.sensorIdsCache.put(sensorId, internalId);
			return internalId;
		} else {
			Log.d("stkdb", "found in cache: " + this.sensorIdsCache.get(sensorId));
			return this.sensorIdsCache.get(sensorId);
		}
	}

	public int dataCount() {
		return dataCount(null);
	}

	public int dataCount(String profileId) {
		Cursor cursor;
		if (profileId != null) {
			cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_COUNT_COLUMNS, DATA_TABLE_QUERY_WHERE_PROFILE, new String[] { profileId }, null, null, null);
		} else {
			cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_COUNT_COLUMNS, null, null, null, null, null);
		}

		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			Log.d("stkdb", "data: " + cursor.getInt(0) + " for " + profileId);
			return cursor.getInt(0);
		} else {
			Log.d("stkdb", "data count not found for " + profileId);
			return 0;
		}
	}

	public void save(String profileId, String sensorId, String data) {
		String internalSensorId = getInternalSensorId(sensorId);
		this.runnable.addData(profileId, internalSensorId, data);
		//this.fireDataEvent();
	}

	public void emptyData() {
		getWritableDatabase().execSQL(DATA_TABLE_DELETE_ALL);
		//fireDataEvent();
	}

	public void exportData() {
		String state = Environment.getExternalStorageState();
		Log.d("stkdb", "export state: " + state);
		Log.d("stkdb", Environment.getExternalStorageDirectory().getAbsolutePath());
		
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File export = null;
			for (int i = 0;; i++) {
				Log.d("stkdb", "export try: " + "science_toolkit_" + i + ".csv");
				export = new File(path, "science_toolkit_" + i + ".csv");
				if (!export.exists()) {
					break;
				}
			}

			Log.d("stkdb", "export file: " + export.getAbsolutePath());

			try {
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(export));

				Cursor cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_ALL_COLUMNS, null, null, null, null, null);
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					while(!cursor.isAfterLast()) {
						bw.write(cursor.getString(0));
						bw.write(" , ");
						bw.write(cursor.getString(1));
						bw.write(" , ");
						bw.write(cursor.getString(2));
						
						String[] parts = cursor.getString(3).split("\\|");
						for (int i = 0; i < 3; i++) {
							bw.write(" , ");
							bw.write(i < parts.length ? parts[i] : "");
						}
						bw.write("\n");
						
						cursor.moveToNext();
					}
				}
				
				bw.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
