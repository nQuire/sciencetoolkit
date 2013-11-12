package org.greengin.sciencetoolkit.ui.components.main.data.view.visualizations;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.SettingsManager;
import org.greengin.sciencetoolkit.ui.SensorUIData;

import android.content.Context;
import android.database.Cursor;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class XYDataPlotSeries {

	boolean[] showSeries;
	XYDataPlotSeries_[] seriesList;
	int valueCount;

	String[] seriesTitle;

	String settingsId;
	String profileId;
	String sensorId;

	Model settings;

	Context context;

	Cursor cursor;

	public XYDataPlotSeries(String profileId, String sensorId, String settingsId, Context context) {
		this.profileId = profileId;
		this.sensorId = sensorId;
		this.settingsId = settingsId;

		this.settings = SettingsManager.getInstance().get(settingsId);

		this.context = context;

		SensorWrapper sensor = SensorWrapperManager.getInstance().getSensor(sensorId);
		valueCount = sensor.getValueCount();
		showSeries = new boolean[valueCount];
		seriesList = new XYDataPlotSeries_[valueCount];

		updateConfiguration();

		for (int i = 0; i < seriesList.length; i++) {
			seriesList[i] = new XYDataPlotSeries_(i);
		}

		seriesTitle = new String[valueCount];
		String[] labels = SensorUIData.getValueLabels(sensor.getType());
		String[] units = SensorUIData.getValueUnits(sensor.getType());
		for (int i = 0; i < valueCount; i++) {
			seriesTitle[i] = labels[i] + "\n" + units[i];
		}

		this.cursor = null;
	}

	public void updateCursor(Cursor cursor) {
		if (this.cursor != null) {
			this.cursor.close();
		}

		this.cursor = cursor;
	}

	public boolean updateConfiguration() {
		boolean modified = false;
		for (int i = 0; i < valueCount; i++) {
			boolean setting = settings.getBool("show:" + sensorId + ":" + i, true);
			if (this.showSeries[i] != setting) {
				this.showSeries[i] = setting;
				modified = true;
			}
		}
		return modified;
	}

	public int seriesCount() {
		return valueCount;
	}

	public void updateSeries(XYPlot plot) {
		for (int i = 0; i < this.seriesList.length; i++) {
			plot.removeSeries(this.seriesList[i]);
		}

		for (int i = 0; i < this.seriesList.length; i++) {
			if (this.showSeries[i]) {
				int resource = i == 0 ? R.xml.line_point_formatter_with_plf1 : i == 1 ? R.xml.line_point_formatter_with_plf2 : R.xml.line_point_formatter_with_plf3;

				LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
				seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
				seriesFormat.configure(plot.getContext(), resource);
				seriesFormat.setPointLabeler(null);
				plot.addSeries(this.seriesList[i], seriesFormat);
			}
		}
	}

	private class XYDataPlotSeries_ implements XYSeries {
		int index;

		public XYDataPlotSeries_(int index) {
			this.index = index;
		}

		@Override
		public String getTitle() {
			return seriesTitle[index];
		}

		@Override
		public Number getX(int i) {
			cursor.moveToPosition(i);
			return cursor.getLong(0);
		}

		@Override
		public Number getY(int i) {
			cursor.moveToPosition(i);
			String[] parts = cursor.getString(1).split("\\|");
			try {
				return Float.parseFloat(parts[index]);
			} catch (Exception e) {
				return 0.f;
			}
		}

		@Override
		public int size() {
			return cursor != null ? cursor.getCount() : 0;
		}
	}
}
