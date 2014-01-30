package org.greengin.sciencetoolkit.ui.base.plot;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.logic.streams.DataInput;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;

import com.androidplot.xy.XYPlot;

import android.content.Context;

public class LiveXYSensorSeriesWrapper extends AbstractXYSensorSeriesWrapper implements DataInput {
	public static final String EVENT_FILTER = "LIVEPLOT";
	public static final String SERIES_SHOW_PREFIX_FILTER = "LIVEPLOT";

	private Lock lock = new ReentrantLock();

	LinkedList<TimeValue> values;
	long period;

	Context context;
	XYPlot plot;
	
	public LiveXYSensorSeriesWrapper(XYPlot plot, SensorWrapper sensor, Model settings, Context context) {
		super(sensor, settings, SERIES_SHOW_PREFIX_FILTER);
		this.context = context;
		this.values = new LinkedList<TimeValue>();
		this.plot = plot;
		updateViewPeriod();
	}

	public void updateViewPeriod() {
		this.period = (long) (1000 * seriesSettings.getDouble("view_period", ModelDefaults.LIVEPLOT_VIEW_PERIOD));
		lock.lock();
		removeOutstandingValues();
		lock.unlock();
	}

	private void removeOutstandingValues() {
		long limit = System.currentTimeMillis() - period;
		while (values.size() > 0 && values.getFirst().time < limit) {
			values.removeFirst();
		}
	}

	private void add(float[] value) {
		lock.lock();

		long time = System.currentTimeMillis();
		values.add(new TimeValue(time, value));
		removeOutstandingValues();
		lock.unlock();
		//Intent i = new Intent(EVENT_FILTER);
		plot.redraw();
//		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}

	@Override
	public void value(float[] values, int valueCount) {
		this.add(values);
	}

	@Override
	Number getDataX(int i) {
		lock.lock();
		long value = values.size() > i ? values.get(i).time : 0;
		lock.unlock();
		return value;
	}

	@Override
	Number getDataY(int i, int seriesIndex) {
		lock.lock();
		float value = values.size() > i ? values.get(i).value[seriesIndex] : 0;
		lock.unlock();
		return value;
	}

	@Override
	int getDataSize() {
		return values.size();
	}
}
