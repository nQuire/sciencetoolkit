package org.greengin.sciencetoolkit.ui.base.plot;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public abstract class AbstractXYSensorSeriesWrapper {

	private final static int[] LINE_STYLES = new int[] { R.xml.line_point_formatter_with_plf1, R.xml.line_point_formatter_with_plf2, R.xml.line_point_formatter_with_plf3, R.xml.line_point_formatter_with_plf4 };

	boolean[] showSeries;
	SensorXYSeries[] seriesList;
	int valueCount;
	String[] seriesTitle;

	public AbstractXYSensorSeriesWrapper(SensorWrapper sensor) {

		valueCount = sensor.getValueCount();
		showSeries = new boolean[valueCount];

		seriesList = new SensorXYSeries[valueCount];
		for (int i = 0; i < seriesList.length; i++) {
			showSeries[i] = true;
			seriesList[i] = new SensorXYSeries(i);
		}

		seriesTitle = SensorUIData.getValueLabels(sensor.getType());
	}

	public void removeFromPlot(XYPlot plot) {
		for (int i = 0; i < this.seriesList.length; i++) {
			plot.removeSeries(this.seriesList[i]);
		}
	}

	public void initSeries(XYPlot plot) { 
		for (int i = 0; i < this.seriesList.length; i++) {
			if (this.showSeries[i]) {
				int resource = LINE_STYLES[i];

				LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
				seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
				seriesFormat.configure(plot.getContext(), resource);
				seriesFormat.setPointLabeler(null);
				plot.addSeries(this.seriesList[i], seriesFormat);
			}
		}
		
	}

	/*public boolean updateSeriesConfiguration(XYPlot plot, boolean anyway) {
		if (updateShowSeries() || anyway) {
			removeFromPlot(plot);
			initSeries(plot);
			return true;
		} else {
			return false;
		}
	}*/

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
