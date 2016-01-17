package org.greengin.sciencetoolkit.ui.base.plot.live;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greengin.sciencetoolkit.common.model.Model;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.logic.streams.DataInput;
import org.greengin.sciencetoolkit.model.SenseItModelDefaults;
import org.greengin.sciencetoolkit.ui.base.plot.AbstractXYSensorSeriesWrapper;

import com.androidplot.xy.XYPlot;

import android.content.Context;

public class LiveXYSensorDataWrapper extends AbstractXYSensorSeriesWrapper implements DataInput {

	private Lock lock = new ReentrantLock();

	LinkedList<TimeValue> values;
	long period;

	Context context;
	XYPlot plot;
	Model seriesSettings;
	
	public LiveXYSensorDataWrapper(XYPlot plot, SensorWrapper sensor, Model settings, Context context) {
		super(context, sensor);
		this.context = context;
		this.values = new LinkedList<TimeValue>();
		this.plot = plot;
		this.seriesSettings = settings;
		updateViewPeriod();
	}

	public void updateViewPeriod() {
		this.period = (long) (1000 * seriesSettings.getDouble("view_period", SenseItModelDefaults.LIVEPLOT_VIEW_PERIOD));
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
	protected Number getDataX(int i) {
		lock.lock();
		long value = values.size() > i ? values.get(i).time : 0;
		lock.unlock();
		return value;
	}

	@Override
	protected Number getDataY(int i, int seriesIndex) {
		lock.lock();
		float value = values.size() > i ? values.get(i).value[seriesIndex] : 0;
		lock.unlock();
		return value;
	}

	@Override
	protected int getDataSize() {
		return values.size();
	}
}
