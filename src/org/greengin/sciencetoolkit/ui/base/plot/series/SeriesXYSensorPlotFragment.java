package org.greengin.sciencetoolkit.ui.base.plot.series;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Vector;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.Arguments;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;
import org.greengin.sciencetoolkit.ui.base.plot.AbstractXYSensorPlotFragment;
import org.greengin.sciencetoolkit.ui.base.plot.SensorBrowserListener;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SeriesXYSensorPlotFragment extends AbstractXYSensorPlotFragment implements SensorBrowserListener {

	SeriesXYSensorDataWrapper series;
	
	String profileId;
	String profileSensorId;
	SensorWrapper sensor;
	Model profile;
	Vector<String> sensors;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		eventManager.setListener(new SeriesEventManagerListener());
		eventManager.listenToProfiles();
		
		profileId = getArguments().getString(Arguments.ARG_PROFILE);
		profile = ProfileManager.get().get(profileId);
		series = null;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		if (profile != null) {
			sensors = new Vector<String>();
			for (Model profileSensor : profile.getModel("sensors", true).getModels()) {
				String sensorId = profileSensor.getString("sensorid", null);
				if (sensorId != null) {
					sensors.add(sensorId);
				}
			}

			if (sensors.size() > 0) {
				setSensor(sensors.firstElement());
			}
		}	
		return v;
	}
	
	public void update() {
		if (series != null) {
		this.series.update();
		}
	}
	
	public void setSensor(String sensorId) {
		super.destroyPlot();
		sensor = SensorWrapperManager.get().getSensor(sensorId);
		profileSensorId = ProfileManager.get().getSensorProfileId(sensorId, profile);
		
		this.sensorBrowser.setSensors(this, sensors, sensorId);
		createPlot();
		plot.getLayoutManager().remove(plot.getLegendWidget());

		series = new SeriesXYSensorDataWrapper(plot, profile, profileSensorId);
		series.update();
	}

	@Override
	protected String getDomainLabel() {
		return "Time";
	}
	
	@Override
	public void sensorBrowserSelected(String sensorId) {
		setSensor(sensorId);
	}

	private class SeriesEventManagerListener extends EventManagerListener {
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