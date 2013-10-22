package org.greengin.sciencetoolkit.plots;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.sensors.SettingsListener;
import org.greengin.sciencetoolkit.sensors.TimeValue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

public class SensorLiveXYSeries implements SettingsListener {
	public static final String SENSOR_LIVE_SERIES_UPDATE = "SENSOR_LIVE_SERIES_UPDATE";

	private Lock lock = new ReentrantLock();

	Timer timer;
	SensorWrapper sensor;
	Context context;
	int period;

	float[] currentValues;
	boolean[] showSeries;
	SensorLiveXYSeries_[] seriesList;
	
	SensorLiveXYSeriesListener listener;

	TimeValue[] values;
	int head;
	int size;

	public SensorLiveXYSeries(SensorWrapper sensor, Context context, SensorLiveXYSeriesListener listener) {
		values = new TimeValue[10];
		currentValues = new float[sensor.getValueCount()];
		showSeries = new boolean[sensor.getValueCount()];
		Arrays.fill(showSeries, true);
		seriesList = new SensorLiveXYSeries_[sensor.getValueCount()];
		for (int i = 0; i < seriesList.length; i++) {
			seriesList[i] = new SensorLiveXYSeries_(i);
		}

		head = 0;
		size = 0;
		timer = null;
		period = 250;
		this.sensor = sensor;
		this.context = context;
		this.listener = listener;
	}

	public void clear() {
		this.head = this.size = 0;
	}

	public void start() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				add(currentValues);
				Intent i = new Intent(SensorLiveXYSeries.SENSOR_LIVE_SERIES_UPDATE);
				LocalBroadcastManager.getInstance(context).sendBroadcast(i);
			}

		}, 0, this.period);
	}

	public void stop() {
		timer.cancel();
		timer.purge();
		timer = null;
	}

	public void updateCurrentValue() {
		for (int i = 0; i < this.currentValues.length; i++) {
			this.currentValues[i] = this.sensor.getValue()[i];
		}
	}

	public boolean isRunning() {
		return timer != null;
	}

	public void add(float[] value) {
		lock.lock();

		values[head] = new TimeValue(System.currentTimeMillis(), value);
		head++;

		if (head == values.length) {
			head = 0;
		}
		if (size < values.length) {
			size++;
		}

		lock.unlock();
	}

	public int seriesCount() {
		return this.sensor.getValueCount();
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
				seriesFormat.configure(context, resource);
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
			StringBuffer buffer = new StringBuffer();
			buffer.append(sensor.getValueLabels()[index]).append("\n").append(sensor.getValueUnits()[index]);
			return buffer.toString();
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
	public List<Bundle> getOptions() {
		Vector<Bundle> list = new Vector<Bundle>();

		Bundle samples = new Bundle();
		samples.putString("key", "samples");
		samples.putString("name", "Sample count");
		samples.putString("description", "The number of samples shown in the plot.");
		samples.putString("type", "number");
		samples.putInt("min", 1);
		samples.putInt("max", 100);
		list.add(samples);

		Bundle period = new Bundle();
		period.putString("key", "period");
		period.putString("name", "Sample period");
		period.putString("description", "Time period betweeb plot samples (ms).");
		period.putString("type", "number");
		period.putInt("min", 100);
		list.add(period);

		for (int i = 0; i < sensor.getValueCount(); i++) {
			Bundle showSeries = new Bundle();
			showSeries.putString("key", "show:" + i);
			showSeries.putString("name", "Show " + sensor.getValueLabels()[i]);
			showSeries.putString("description", "Show or hide a plot series.");
			showSeries.putString("type", "checkbox");
			list.add(showSeries);
		}

		return list;
	}

	@Override
	public Object getOptionValue(String key) {
		if (key == "samples") {
			return this.values.length;
		} else if (key == "period") {
			return this.period;
		} else if (key.startsWith("show:")) {
			int index = Integer.parseInt(key.substring(5));
			return this.showSeries[index];
		}
		
		return null;
	}

	@Override
	public boolean setOptionValue(String key, Object value) {
		lock.lock();

		boolean result = false;

		boolean enabled = timer != null;
		if (enabled) {
			stop();
		}

		if (key == "samples") {
			int length = Math.max(5, Math.min(100, (Integer) value));
			if (length != values.length) {
				TimeValue[] nvalues = new TimeValue[length];
				int nsize = Math.min(length, this.size);

				for (int i = 0; i < nsize; i++) {
					int pos = (values.length + head - 1 - i) % values.length;
					nvalues[nsize - 1 - i] = values[pos];
				}

				values = nvalues;
				size = nsize;
				head = nsize == values.length ? 0 : nsize;
			}
			result = true;
		} else if (key == "period") {
			this.period = Math.max(10, (Integer) value);
			result = true;
		} else if (key.startsWith("show:")) {
			int index = Integer.parseInt(key.substring(5));
			this.showSeries[index] = (Boolean) value;
			result = true;
		}

		if (enabled) {
			start();
		}

		lock.unlock();
		
		if (result) {
			listener.seriesSettingsUpdated();
		}

		return result;
	}
}
