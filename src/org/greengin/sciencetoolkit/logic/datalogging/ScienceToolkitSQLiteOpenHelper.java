package org.greengin.sciencetoolkit.logic.datalogging;

import java.util.HashMap;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScienceToolkitSQLiteOpenHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "sciencetoolkit";

	public static final String SENSORS_TABLE_NAME = "sensors";
	public static final String SENSORS_TABLE_CREATE = "CREATE TABLE " + SENSORS_TABLE_NAME + " (name TEXT);";
	public static final String[] SENSORS_TABLE_QUERY_COLUMNS_E2I = new String[] { "ROWID" };
	public static final String SENSORS_TABLE_QUERY_WHERE_E2I = "name=?";
	public static final String[] SENSORS_TABLE_QUERY_COLUMNS_I2E = new String[] { "name" };
	public static final String SENSORS_TABLE_QUERY_WHERE_I2E = "ROWID=?";

	// v1: CREATE TABLE data (sensor INTEGER, timestamp INTEGER, data TEXT);
	// v2: CREATE TABLE data (profile INTEGER, sensor INTEGER, timestamp
	// INTEGER, data TEXT);

	public static final String DATA_TABLE_NAME = "data";
	public static final String DATA_TABLE_CREATE = "CREATE TABLE " + DATA_TABLE_NAME + " (profile INTEGER, sensor INTEGER, timestamp INTEGER, data TEXT);";
	public static final String[] DATA_TABLE_QUERY_COUNT_COLUMNS = new String[] { "Count(ROWID)" };
	public static final String[] DATA_TABLE_QUERY_COUNT_COLUMNS_BY_SENSOR = new String[] { "sensor", "Count(sensor)" };
	public static final String DATA_TABLE_QUERY_COUNT_COLUMNS_BY_SENSOR_GROUP_BY = "sensor";
	public static final String[] DATA_TABLE_QUERY_ALL_COLUMNS = new String[] { "profile", "sensor", "timestamp", "data" };
	public static final String[] DATA_TABLE_QUERY_LIST_VIEW_COLUMNS = new String[] { "ROWID as _id", "sensor", "timestamp", "data" };
	public static final String[] DATA_TABLE_QUERY_RANGE = new String[] { "MIN(timestamp) as mintime", "MAX(timestamp) as maxtime" };
	public static final String DATA_TABLE_QUERY_WHERE_PROFILE = "profile=?";
	public static final String DATA_TABLE_QUERY_WHERE_PROFILE_SENSOR = "profile=? AND sensor=?";
	public static final String DATA_TABLE_DELETE_ALL = "DELETE FROM data";
	public static final String DATA_TABLE_DELETE_PROFILE = "DELETE FROM data WHERE profile=?";

	Context context;
	HashMap<String, String> sensorIdsCacheE2I;
	HashMap<String, String> sensorIdsCacheI2E;
	AsynchSQLiteWriterRunnable runnable;
	DataLoggerDataListener listener;

	public ScienceToolkitSQLiteOpenHelper(Context context, DataLoggerDataListener listener) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.sensorIdsCacheE2I = new HashMap<String, String>();
		this.sensorIdsCacheI2E = new HashMap<String, String>();
		this.context = context;
		this.runnable = new AsynchSQLiteWriterRunnable(getWritableDatabase(), listener);
		this.listener = listener;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SENSORS_TABLE_CREATE);
		db.execSQL(DATA_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		if (oldV < 2) {
			db.execSQL("ALTER TABLE " + DATA_TABLE_NAME + " ADD COLUMN profile INTEGER");
			db.execSQL("UPDATE " + DATA_TABLE_NAME + " SET profile='1'");
		}
	}

	protected String getInternalSensorId(String sensorId) {
		if (!this.sensorIdsCacheE2I.containsKey(sensorId)) {
			String internalId;
			Cursor cursor = getReadableDatabase().query(SENSORS_TABLE_NAME, SENSORS_TABLE_QUERY_COLUMNS_E2I, SENSORS_TABLE_QUERY_WHERE_E2I, new String[] { sensorId }, null, null, null, "1");
			if (cursor.getCount() == 1) {
				cursor.moveToFirst();
				internalId = cursor.getString(0);
			} else {
				ContentValues values = new ContentValues();
				values.put("name", sensorId);
				internalId = "" + getWritableDatabase().insert(SENSORS_TABLE_NAME, null, values);
			}

			this.sensorIdsCacheE2I.put(sensorId, internalId);
			this.sensorIdsCacheI2E.put(internalId, sensorId);
			return internalId;
		} else {
			return this.sensorIdsCacheE2I.get(sensorId);
		}
	}

	public String getExternalSensorId(String internalSensorId) {
		if (!this.sensorIdsCacheI2E.containsKey(internalSensorId)) {
			String externalId;
			Cursor cursor = getReadableDatabase().query(SENSORS_TABLE_NAME, SENSORS_TABLE_QUERY_COLUMNS_I2E, SENSORS_TABLE_QUERY_WHERE_I2E, new String[] { internalSensorId }, null, null, null, "1");
			if (cursor.getCount() == 0) {
				return "";
			} else {
				cursor.moveToFirst();
				externalId = cursor.getString(0);
				this.sensorIdsCacheI2E.put(internalSensorId, externalId);
				this.sensorIdsCacheE2I.put(externalId, internalSensorId);
				return externalId;
			}
		} else {
			return this.sensorIdsCacheI2E.get(internalSensorId);
		}
	}

	public int dataCount() {
		return dataCount(null);
	}

	public Hashtable<String, Integer> detailedDataCount(String profileId) {
		Cursor cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_COUNT_COLUMNS_BY_SENSOR, DATA_TABLE_QUERY_WHERE_PROFILE, new String[] { profileId }, DATA_TABLE_QUERY_COUNT_COLUMNS_BY_SENSOR_GROUP_BY, null, null);

		Hashtable<String, Integer> result = new Hashtable<String, Integer>();

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String internalSensorId = cursor.getString(0);
				String externalSensorId = getExternalSensorId(internalSensorId);
				int count = cursor.getInt(1);
				result.put(externalSensorId, count);
				cursor.moveToNext();
			}
		}

		return result;
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
			return cursor.getInt(0);
		} else {
			return 0;
		}
	}

	public void save(String profileId, String sensorId, String data) {
		String internalSensorId = getInternalSensorId(sensorId);
		this.runnable.addData(profileId, internalSensorId, data);
	}

	public void emptyData(String profileId) {
		if (profileId == null) {
			getWritableDatabase().delete(DATA_TABLE_NAME, null, null);
		} else {
			getWritableDatabase().delete(DATA_TABLE_NAME, DATA_TABLE_QUERY_WHERE_PROFILE, new String[] { profileId });
		}

		listener.dataLoggerDataModified("all");
	}

	public Cursor getDataCursor(String profileId) {
		Cursor cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_ALL_COLUMNS, DATA_TABLE_QUERY_WHERE_PROFILE, new String[] { profileId }, null, null, null);
		return cursor;
	}

	public Cursor getListViewCursor(String profileId) {
		Cursor cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_LIST_VIEW_COLUMNS, DATA_TABLE_QUERY_WHERE_PROFILE, new String[] { profileId }, null, null, null);
		cursor.moveToFirst();
		return cursor;
	}

	public boolean getRange(long[] values, String profileId) {
		Cursor cursor = getReadableDatabase().query(DATA_TABLE_NAME, DATA_TABLE_QUERY_RANGE, DATA_TABLE_QUERY_WHERE_PROFILE, new String[] { profileId }, null, null, null);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			values[0] = cursor.getLong(0);
			values[1] = cursor.getLong(1);
			return true;
		} else {
			return false;
		}
	}

}
