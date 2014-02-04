package org.greengin.sciencetoolkit.ui.base.plot.series;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.base.widgets.colorview.ColorGenerator;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import android.content.Context;

public class SeriesXYSensorDataWrapper {

	Context context;
	Model profile;
	String profileSensorId;
	Model seriesDataContainer;
	XYPlot plot;
	SensorWrapper sensor;
	Hashtable<String, SeriesSensorXYSeries> seriesTable;

	public SeriesXYSensorDataWrapper(XYPlot plot, Model profile, String profileSensorId) {
		this.plot = plot;
		this.profile = profile;
		this.profileSensorId = profileSensorId;
		String sensorId = profile.getModel("sensors", true).getModel(profileSensorId).getString("sensorid");
		this.sensor = SensorWrapperManager.get().getSensor(sensorId);
		
		seriesTable = new Hashtable<String, SeriesSensorXYSeries>();
		seriesDataContainer = profile.getModel("series", true);
	}


	public void update() {
		updateSeries();
	}
	
	private void updateSeries() {
		File[] fs = DataLogger.get().getSeries(profile.getString("id"));

		Hashtable<String, Boolean> remove = new Hashtable<String, Boolean>();
		for (String key : seriesTable.keySet()) {
			remove.put(key, true);
		}
		
		for (File f : fs) {
			int index = seriesDataContainer.getModel(f.getName(), true).getInt("dataviewershow", -1);
			if (index >= 0) {
				remove.put(f.getName(), false);
				if (!seriesTable.containsKey(f.getName())) {
					loadSeries(f, index);
				}
			}
		}

		for (Entry<String, Boolean> entry : remove.entrySet()) {
			if (entry.getValue()) {
				SeriesSensorXYSeries removed = seriesTable.remove(entry.getKey());
				removed.removeFromPlot();
			}
		}
		
		plot.redraw();
	}
	
	private void loadSeries(File f, int index) {
		SeriesSensorXYSeries series = new SeriesSensorXYSeries(f, index);
		seriesTable.put(f.getName(), series);
		series.addToPlot();
	}
	
	
	private class SeriesSensorXYSeries {
		
		Vector<TimeValue> values;		
		SeriesVariableXYSeries[] series;
		LineAndPointFormatter formatter;

		public SeriesSensorXYSeries(File file, int index) {
			values = new Vector<TimeValue>();
			series = new SeriesVariableXYSeries[sensor.getValueCount()];
			
			try {
			BufferedReader reader = new BufferedReader(new FileReader(file));				
				while(true) {
					String line = reader.readLine();
					if (line != null) {
						String[] parts = line.split(",");
						if (parts.length == series.length + 2 && parts[0].equals(profileSensorId)) {
							long t = Long.parseLong(parts[1]);
							
							float[] v = new float[series.length];
							
							for (int i = 0; i < series.length; i++) {
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
			
			int color = ColorGenerator.getAndroidColor(index);
			
			formatter = new LineAndPointFormatter();
			formatter.setPointLabelFormatter(new PointLabelFormatter());
			formatter.getLinePaint().setColor(color);
			formatter.getLinePaint().setStrokeWidth(2f);
			formatter.getVertexPaint().setColor(color);
			formatter.setFillPaint(null);
			formatter.setPointLabeler(null);
			
			for (int i = 0; i < series.length; i++) {
				series[i] = new SeriesVariableXYSeries(i);
			}
		}
		
		public void addToPlot() {
			
			for (SeriesVariableXYSeries s : series) {
				
				plot.addSeries(s, formatter);
			}
		}
		
		public void removeFromPlot() {
			for (SeriesVariableXYSeries s : series) {
				plot.removeSeries(s);
			}			
		}
		
		private class SeriesVariableXYSeries implements XYSeries {
			int index;
			
			public SeriesVariableXYSeries(int index) {
				this.index = index;
			}

			@Override
			public String getTitle() {
				return null;
			}

			@Override
			public Number getX(int i) {
				return values.get(i).time - values.firstElement().time;
			}

			@Override
			public Number getY(int i) {
				return values.get(i).value[index];
			}

			@Override
			public int size() {
				return values.size();
			}

		}
	}

}
