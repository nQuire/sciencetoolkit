package org.greengin.sciencetoolkit.ui.base.plot.series;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.ui.base.plot.AbstractXYSensorSeriesWrapper;

import com.androidplot.xy.XYPlot;


public class SeriesXYSensorDataWrapper extends AbstractXYSensorSeriesWrapper {

	String profileSensorId;
	SensorWrapper sensor;
	File series;
	XYPlot plot;
	Vector<TimeValue> values;

	public SeriesXYSensorDataWrapper(XYPlot plot, File series, String profileSensorId, SensorWrapper sensor) {
		super(sensor);

		this.plot = plot;
		this.profileSensorId = profileSensorId;
		this.series = series;
		this.sensor = sensor;
		values = new Vector<TimeValue>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(series));
			while (true) {
				String line = reader.readLine();
				if (line != null) {
					String[] parts = line.split(",");
					if (parts[0].equals(profileSensorId) && parts.length == valueCount + 2) {
						long t = Long.parseLong(parts[1]);

						float[] v = new float[valueCount];

						for (int i = 0; i < valueCount; i++) {
							v[i] = Float.parseFloat(parts[2 + i]);
						}
						values.add(new TimeValue(t, v));
					}
				} else {
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		updateSeries();
	}

	private void updateSeries() {
		plot.redraw();
	}

	@Override
	protected Number getDataX(int i) {
		return values.size() > i ? values.get(i).time - values.get(0).time : 0;
	}

	@Override
	protected Number getDataY(int i, int seriesIndex) {
		return values.size() > i ? values.get(i).value[seriesIndex] : 0;
	}

	@Override
	protected int getDataSize() {
		return values.size();
	}
}
