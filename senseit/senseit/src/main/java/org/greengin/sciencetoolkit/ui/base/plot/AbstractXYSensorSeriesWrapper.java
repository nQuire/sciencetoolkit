package org.greengin.sciencetoolkit.ui.base.plot;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;

import android.graphics.Color;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public abstract class AbstractXYSensorSeriesWrapper {

	static final int[] COLORS = new int[] {R.color.plot_1, R.color.plot_2, R.color.plot_3, R.color.plot_4};
	
	boolean[] showSeries;
	SensorXYSeries[] seriesList;
	protected int valueCount;
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
				
				int color = plot.getContext().getResources().getColor(COLORS[i % COLORS.length]);

				LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
				seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
				seriesFormat.getFillPaint().setColor(Color.TRANSPARENT);
				seriesFormat.getLinePaint().setStrokeWidth(2f);
				seriesFormat.getLinePaint().setColor(color);;
				seriesFormat.getVertexPaint().setColor(color);
				
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

	protected abstract Number getDataX(int i);

	protected abstract Number getDataY(int i, int seriesIndex);

	protected abstract int getDataSize();

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
