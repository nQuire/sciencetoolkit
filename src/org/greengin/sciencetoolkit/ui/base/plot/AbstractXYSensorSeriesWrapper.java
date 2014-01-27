package org.greengin.sciencetoolkit.ui.base.plot;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public abstract class AbstractXYSensorSeriesWrapper {

	boolean[] showSeries;
	SensorXYSeries[] seriesList;
	int valueCount;
	String[] seriesTitle;
	Model seriesSettings;
	String seriesSettingPrefix;

	public AbstractXYSensorSeriesWrapper(SensorWrapper sensor, Model seriesSettings, String seriesSettingPrefix) {
		this.seriesSettings = seriesSettings;
		this.seriesSettingPrefix = seriesSettingPrefix;

		valueCount = sensor.getValueCount();
		showSeries = new boolean[valueCount];
		updateShowSeries();

		seriesList = new SensorXYSeries[valueCount];
		for (int i = 0; i < seriesList.length; i++) {
			seriesList[i] = new SensorXYSeries(i);
		}

		seriesTitle = SensorUIData.getValueLabels(sensor.getType());
	}

	private boolean updateShowSeries() {
		boolean modified = false;
		for (int i = 0; i < valueCount; i++) {
			boolean setting = seriesSettings.getBool(seriesSettingPrefix + i, true);
			if (showSeries[i] != setting) {
				showSeries[i] = setting;
				modified = true;
			}
		}
		return modified;
	}

	public void removeFromPlot(XYPlot plot) {
		for (int i = 0; i < this.seriesList.length; i++) {
			plot.removeSeries(this.seriesList[i]);
		}
	}

	public boolean updateSeriesConfiguration(XYPlot plot, boolean anyway) {
		if (updateShowSeries() || anyway) {
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

			return true;
		} else {
			return false;
		}
	}

	abstract Number getDataX(int i);

	abstract Number getDataY(int i, int seriesIndex);

	abstract int getDataSize();

	private class SensorXYSeries implements XYSeries {
		int index;

		public SensorXYSeries(int index) {
			this.index = index;
		}

		@Override
		public String getTitle() {
			return seriesTitle[index];
		}

		@Override
		public Number getX(int i) {
			return getDataX(i);
		}

		@Override
		public Number getY(int i) {
			return getDataY(i, index);
		}

		@Override
		public int size() {
			return getDataSize();
		}

	}
}
