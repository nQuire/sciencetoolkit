package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.File;
import java.util.HashMap;

import android.content.Context;

public class DataLoggerFileManager {

	HashMap<String, Integer> series;
	Context applicationContext;

	public DataLoggerFileManager(Context applicationContext) {
		this.series = new HashMap<String, Integer>();
		this.applicationContext = applicationContext;
	}

	public int getCurrentSeries(String profileId) {
		if (series.containsKey(profileId)) {
			return series.get(profileId);
		} else {
			return 0;
		}
	}

	public File getCurrentSeriesFile(String profileId) {
		int series = getCurrentSeries(profileId);
		if (series > 0) {
			return new File(getPath(profileId), String.format("%d.csv", series));
		} else {
			return null;
		}
	}
	
	public int seriesCount(String profileId) {
		File[] series = series(profileId);
		return series != null ? series.length : 0;		
	}
	
	public File[] series(String profileId) {
		return getPath(profileId).listFiles();		
	}

	public int startNewSeries(String profileId) {
		int newSeries;

		if (series.containsKey(profileId)) {
			newSeries = series.get(profileId) + 1;
		} else {
			File path = getPath(profileId);
			newSeries = 1;
			for (File f : path.listFiles()) {
				String name = f.getName();
				String[] parts = name.split("\\.");
				if (parts.length == 2 && "csv".equals(parts[1])) {
					try {
						int id = Integer.parseInt(parts[0]);
						if (id >= newSeries) {
							newSeries = id + 1;
						}
					} finally {
					}
				}
			}
		}
		series.put(profileId, newSeries);

		return newSeries;
	}

	private File getPath(String profileId) {
		File base = new File(applicationContext.getFilesDir(), "series");
		if (!base.exists()) {
			base.mkdir();
		}

		File path = new File(base, profileId);
		if (!path.exists()) {
			path.mkdir();
		}

		return path;
	}
	
	public void deleteSeries(String profileId) {
		File base = new File(applicationContext.getFilesDir(), "series");
		if (!base.exists()) {
			base.mkdir();
		}

		File path = new File(base, profileId);
		
		if (path.exists()) {
			deleteFolder(path);
		}
	}
	
	public void deleteSeries(String profileId, File series) {
		series.delete();
	}
	
	private void deleteFolder(File f) {
		if (f.isDirectory()) {
			for (File cf : f.listFiles()) {
				deleteFolder(cf);
			}			
		}
		
		f.delete();
	}
}
