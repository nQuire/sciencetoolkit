package org.greengin.sciencetoolkit;

import org.greengin.sciencetoolkit.data.ScienceToolkitSQLiteOpenHelper;

import android.content.Context;

public class DataManager {
	public static final String DATA_MODIFIED = "DATA_MODIFIED";
	
	private static ScienceToolkitSQLiteOpenHelper instance;

	public static ScienceToolkitSQLiteOpenHelper getInstance() {
		return instance;
	}

	public static void init(Context context) {
		DataManager.instance = new ScienceToolkitSQLiteOpenHelper(context);
	}
}
