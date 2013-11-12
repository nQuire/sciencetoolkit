package org.greengin.sciencetoolkit.ui.plotting;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.logic.streams.DataInput;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ModelDefaults;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class LiveXYSensorSeriesWrapper extends AbstractXYSensorSeriesWrapper implements DataInput {

	private Lock lock = new ReentrantLock();

	LinkedList<TimeValue> values;
	long period;

	Context context;
	String filter;

	public LiveXYSensorSeriesWrapper(SensorWrapper sensor, Model settings, String seriesSettingPrefix, Context context, String filter) {
		super(sensor, settings, seriesSettingPrefix);
		
		this.context = context;
		this.filter = filter;

		this.values = new LinkedList<TimeValue>();
	}

	public void updateViewPeriod() {
		this.period = (long) (1000 * seriesSettings.getDouble("view_period", ModelDefaults.LIVEPLOT_VIEW_PERIOD));
		lock.lock();
		removeOutstandingValues();
		lock.unlock();
	}

	private void removeOutstandingValues() {
		long limit = System.currentTimeMillis() - period;
		while (values.size() > 0) {
			if (values.getFirst().time < limit) {
				values.removeFirst();
			} else {
				break;
			}
		}
	}

	private void add(float[] value) {
		lock.lock();

		long time = System.currentTimeMillis();
		values.add(new TimeValue(time, value));
		removeOutstandingValues();
		lock.unlock();
		Intent i = new Intent(filter);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
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
