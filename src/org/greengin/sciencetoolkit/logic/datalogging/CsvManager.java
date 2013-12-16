package org.greengin.sciencetoolkit.logic.datalogging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.List;

import android.database.Cursor;
import android.os.Environment;

public class CsvManager {

	public static File exportCSV(DataLogger logger, Cursor cursor, String filename) {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File path = path();
			File export = null;

			if (filename != null) {
				export = new File(path, filename);
			} else {
				for (int i = 0;; i++) {
					export = new File(path, "science_toolkit_" + i + ".csv");
					if (!export.exists()) {
						break;
					}
				}
			}

			try {

				BufferedWriter bw = new BufferedWriter(new FileWriter(export));

				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						bw.write(cursor.getString(0));
						bw.write(" , ");
						bw.write(logger.sensorName(cursor.getString(1)));
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

					cursor.close();
				}

				bw.close();

				return export;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static String[] fileList() {
		File path = path();
		return path.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return isCsvFile(filename);
			}
		});
	}

	private static File path() {
		return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	}

	private static boolean isCsvFile(String filename) {
		return filename.startsWith("science_toolkit_") && filename.endsWith(".csv");
	}

	public static void deleteFiles(List<String> files) {
		File path = path();
		for (String filename : files) {
			if (isCsvFile(filename)) {
				File file = new File(path, filename);
				file.delete();
			}
		}
	}

	public static File getFile(String filename) {
		if (isCsvFile(filename)) {
			File file = new File(path(), filename);
			if (file.exists() && file.isFile()) {
				return file;
			}
		}

		return null;
	}

}
