package org.greengin.sciencetoolkit.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

import org.greengin.sciencetoolkit.DataManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ScienceToolkitSQLiteOpenHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "sciencetoolkit";

	public static final String SENSORS_TABLE_NAME = "sensors";
	public static final String SENSORS_TABLE_CREATE = "CREATE TABLE " + SENSORS_TABLE_NAME + " (name TEXT);";
	public static final String[] SENSORS_TABLE_QUERY_COLUMNS = new String[] { "ROWID" };
	public static final String SENSORS_TABLE_QUERY_WHERE = "name=?";

	public static final String DATA_TABLE_NAME = "data";
	public static final String DATA_TABLE_CREATE = "CREATE TABLE " + DATA_TABLE_NAME + " (sensor INTEGER, timestamp INTEGER, data TEXT);";
	public static final String[] DATA_TABLE_QUERY_COUNT_COLUMNS = new String[] { "Count(ROWID)" };
	public static final String DATA_TABLE_QUERY_WHERE = "sensor=?";
	public static final String DATA_TABLE_DELETE_ALL = "DELETE FROM data";

	HashMap<String, String> sensorIdsCache;
	AsynchDataRunnable runnable;
	Context context;

	public ScienceToolkitSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.sensorIdsCache = new HashMap<String, String>();
		this.runnable = new AsynchDataRunnable(this.getWritableDatabase());
		this.context = context;

	}

	public void fireDataEvent() {
		Intent i = new Intent(DataManager.DATA_MODIFIED);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SENSORS_TABLE_CREATE);
		db.execSQL(DATA_TABLE_CREATE);
		Log.d("stkdb", "tables created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
	}

	protected String getSensorId(String sensor) {
		Log.d("stkdb", "request id: " + sensor);
		if (!this.sensorIdsCache.containsKey(sensor)) {
			String id;
			Cursor cursor = getReadableDatabase().query(SENSORS_TABLE_NAME, SENSORS_TABLE_QUERY_COLUMNS, SENSORS_TABLE_QUERY_WHERE, new String[] { sensor }, null, null, null, "1");
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				id = cursor.getString(0);
				Log.d("stkdb", "found in db: " + id);
			} else {
				ContentValues values = new ContentValues();
				values.put("name", sensor);
				id = "" + getWritableDatabase().insert(SENSORS_TABLE_NAME, null, values);
				Log.d("stkdb", "added in db: " + id);
			}

			this.sensorIdsCache.put(sensor, id);
			return id;
		} else {
			Log.d("stkdb", "found in cache: " + this.sensorIdsCache.get(sensor));
			return this.sensorIdsCache.get(sensor);
		}
	}

	public int dataCount() {
		return dataCount(null);
	}

	public int dataCount(String sensor) {
		Cursor cursor;
		if (sensor != null) {
			String sensorId = getSensorId(sensor);
			cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_COUNT_COLUMNS, DATA_TABLE_QUERY_WHERE, new String[] { sensorId }, null, null, null);
		} else {
			cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_COUNT_COLUMNS, null, null, null, null, null);
		}

		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			Log.d("stkdb", "data: " + cursor.getInt(0) + " for " + sensor);
			return cursor.getInt(0);
		} else {
			Log.d("stkdb", "data count not found for " + sensor);
			return 0;
		}
	}

	public void save(String sensor, float[] values, int valueCount) {
		String sensorId = getSensorId(sensor);
		this.runnable.addData(sensorId, values, valueCount);
		this.fireDataEvent();
	}

	public void emptyData() {
		getWritableDatabase().execSQL(DATA_TABLE_DELETE_ALL);
		fireDataEvent();
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

				Cursor cursor = getReadableDatabase().query(DATA_TABLE_NAME, null, null, null, null, null, null);
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					while(!cursor.isAfterLast()) {
						bw.write(cursor.getString(0));
						bw.write(" , ");
						bw.write(cursor.getString(1));
						
						String[] parts = cursor.getString(2).split("\\|");
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
