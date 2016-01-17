package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.greengin.sciencetoolkit.common.model.Model;

import android.content.Context;
import android.os.Environment;

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

	public File seriesFile(String profileId, String fileName) {
		return new File(getPath(profileId), fileName);
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
	
	public void deleteSeries(File series) {
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
	
	public File getPublicFile(Model profile, File series) {
		String state = Environment.getExternalStorageState();
		if (profile != null && series != null && Environment.MEDIA_MOUNTED.equals(state)) {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File export = null;
			String filename = series.getName().replaceFirst("[.][^.]+$", "");
			String baseName = String.format("senseit_%s_%s%%s.csv",  profile.getString("id"), filename);
			for (int i = 0;; i++) {
				String name = String.format(baseName, i == 0 ? "" : ("_" + i));
				export = new File(path, name);
				if (!export.exists()) {
					break;
				}
			}

			try {
				InputStream in = new FileInputStream(series);
			    OutputStream out = new FileOutputStream(export);

			    // Transfer bytes from in to out
			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = in.read(buf)) > 0) {
			        out.write(buf, 0, len);
			    }
			    in.close();
			    out.close();
			    
				return export;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
}
