package org.greengin.sciencetoolkit.ui.base.plot.record;

import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.ui.base.plot.AbstractXYSensorSeriesWrapper;

import android.content.Context;

public class RecordXYSensorDataWrapper extends AbstractXYSensorSeriesWrapper {
	private Lock lock = new ReentrantLock();

	long period;

	Context context;
	
	Vector<TimeValue> record;
	
	public RecordXYSensorDataWrapper(SensorWrapper sensor, Context context, Vector<TimeValue> record) {
		super(sensor);
		this.context = context;
		this.record = record;
	}


	@Override
	protected Number getDataX(int i) {
		lock.lock();
		long value = record.size() > i ? record.get(i).time - record.get(0).time : 0;
		lock.unlock();
		return value;
	}

	@Override
	protected Number getDataY(int i, int seriesIndex) {
		lock.lock();
		float value = record.size() > i ? record.get(i).value[seriesIndex] : 0;
		lock.unlock();
		return value;
	}

	@Override
	protected int getDataSize() {
		return record.size();
	}
}
