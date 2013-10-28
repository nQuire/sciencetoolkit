package org.greengin.sciencetoolkit.ui.datafilters;

import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.logic.streams.DataInput;
import org.greengin.sciencetoolkit.ui.SensorUIData;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class SensorLiveXYSeries implements DataInput {

	private Lock lock = new ReentrantLock();

	Timer timer;

	boolean[] showSeries;
	SensorLiveXYSeries_[] seriesList;
	int valueCount;
	TimeValue[] values;
	int head;
	int size;
	
	
	String[] seriesTitle;

	Context context;
	String filter;

	public SensorLiveXYSeries(SensorWrapper sensor, Context context, String filter) {
		values = new TimeValue[100];
		Arrays.fill(values, null);

		valueCount = sensor.getValueCount();
		showSeries = new boolean[valueCount];
		Arrays.fill(showSeries, true);

		seriesList = new SensorLiveXYSeries_[valueCount];
		for (int i = 0; i < seriesList.length; i++) {
			seriesList[i] = new SensorLiveXYSeries_(i);
		}

		head = 0;
		size = 0;
		timer = null;

		this.context = context;
		this.filter = filter;
		
		seriesTitle = new String[valueCount];
		String[] labels = SensorUIData.getValueLabels(sensor.getType());
		String[] units = SensorUIData.getValueUnits(sensor.getType());
		for (int i = 0; i < valueCount; i++) {
			seriesTitle[i] = labels[i] + "\n" + units[i];
		}
	}

	public void clear() {
		this.head = this.size = 0;
	}

	public boolean isRunning() {
		return timer != null;
	}

	public void add(float[] value) {
		lock.lock();

		long time = System.currentTimeMillis();

		if (values[head] == null) {
			values[head] = new TimeValue(time, value);
		} else {
			values[head].set(time, value);
		}

		head++;

		if (head == values.length) {
			head = 0;
		}
		if (size < values.length) {
			size++;
		}

		lock.unlock();
		Intent i = new Intent(filter);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
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

	private class SensorLiveXYSeries_ implements XYSeries {
		int index;

		public SensorLiveXYSeries_(int index) {
			this.index = index;
		}

		@Override
		public String getTitle() {
			return seriesTitle[index];
		}

		@Override
		public Number getX(int i) {
			int pos = (values.length + head - 1 - i) % values.length;
			return values[pos].time;
		}

		@Override
		public Number getY(int i) {
			int pos = (values.length + head - 1 - i) % values.length;
			return values[pos].value[index];
		}

		@Override
		public int size() {
			return size;
		}

	}

	@Override
	public void value(float[] values, int valueCount) {
		this.add(values);
	}
}
