package org.greengin.sciencetoolkit.ui.base.plot.series;

import java.io.File;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Vector;

import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.base.SenseItArguments;
import org.greengin.sciencetoolkit.ui.base.events.SenseItEventManagerListener;
import org.greengin.sciencetoolkit.ui.base.plot.AbstractXYSensorPlotFragment;
import org.greengin.sciencetoolkit.ui.base.plot.SensorBrowserListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SeriesXYSensorPlotFragment extends AbstractXYSensorPlotFragment implements SensorBrowserListener {


	SeriesXYSensorDataWrapper series;

	String profileId;

	SensorWrapper sensor;
	HashMap<String, String> sensors2profile;
	Vector<String> sensors;
	File seriesFile;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.profileId = getArguments().getString(SenseItArguments.ARG_PROFILE);

		eventManager.setListener(new SeriesEventManagerListener());
		eventManager.listenToProfiles();
		
		setScaleParams(false, true);

		series = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		update();
		return v;
	}

	public void update() {
		String selected = ProfileManager.get().get(profileId).getModel("dataviewer", true).getString("series", null);
		
		if (selected != null) {
			File newSeriesFile = DataLogger.get().getSeriesFile(profileId, selected);
			if (newSeriesFile != null && (seriesFile == null || !newSeriesFile.getName().equals(seriesFile.getName()))) {
				this.seriesFile = newSeriesFile;
				sensors2profile = DataLogger.get().getSensorsInSeries(seriesFile);
				sensors = new Vector<String>();
				sensors.addAll(sensors2profile.keySet());
				
				String previousSensorId = ProfileManager.get().get(profileId).getModel("dataviewer", true).getString("sensor", null);
				String sensorId = sensors.contains(previousSensorId) ? previousSensorId : 
					(sensors.size() > 0 ? sensors.get(0) : null);
				
				if (sensorId != null) {
					setSensor(sensorId);
					sensorBrowser.setVisibility(View.VISIBLE);
				}
			}
		} else {
			destroyPlot();
			sensorBrowser.setVisibility(View.GONE);
		}
	}

	public void setSensor(String sensorId) {
		super.destroyPlot();
		ProfileManager.get().get(profileId).getModel("dataviewer", true).setString("sensor", sensorId);
		sensor = SensorWrapperManager.get().getSensor(sensorId);
		this.sensorBrowser.setSensors(this, sensors, sensorId);
		createPlot();
		series = new SeriesXYSensorDataWrapper(plot, seriesFile, sensors2profile.get(sensorId), sensor);
		series.initSeries(plot);
	}

	@Override
	protected String getDomainLabel() {
		return "Time";
	}

	@Override
	public void sensorBrowserSelected(String sensorId) {
		setSensor(sensorId);
	}

	private class SeriesEventManagerListener extends SenseItEventManagerListener {
		@Override
		public void eventProfile(String event, boolean whilePaused) {
			update();
		}
	}

	@Override
	protected NumberFormat createDomainNumberFormat() {
		return new SeriesTimePlotDomainFormat();
	}

	private class SeriesTimePlotDomainFormat extends NumberFormat {

		private static final long serialVersionUID = -5396301066688752617L;
		DecimalFormat df;

		public SeriesTimePlotDomainFormat() {
			this.df = new DecimalFormat();
		}

		@Override
		public StringBuffer format(double d, StringBuffer sb, FieldPosition fp) {
			return format((long) d, sb, fp);
		}

		@Override
		public StringBuffer format(long l, StringBuffer sb, FieldPosition fp) {
			int ds = (int) Math.log10(Math.max(1, l));
			if (ds < 3) {
				return sb.append(l).append(" ms");
			} else {
				df.setMaximumFractionDigits(Math.max(0, ds - 2));
				return sb.append(df.format(l * .001)).append(" s");
			}
		}

		@Override
		public Number parse(String arg0, ParsePosition arg1) {
			return null;
		}

	}

}