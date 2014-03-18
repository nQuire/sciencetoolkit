package org.greengin.sciencetoolkit.ui.base.plot.record;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.datalogging.DataLogger;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.logic.sensors.TimeValue;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.model.ProfileManager;
import org.greengin.sciencetoolkit.ui.base.events.EventManagerListener;
import org.greengin.sciencetoolkit.ui.base.plot.ClosableXYSensorPlotFragment;
import org.greengin.sciencetoolkit.ui.base.plot.SensorBrowserListener;

import android.os.Bundle;
import android.view.View;

public class RecordXYSensorPlotFragment extends ClosableXYSensorPlotFragment implements SensorBrowserListener {
	

	RecordXYSensorDataWrapper series;
	
	SensorWrapper sensor;
	String profileSensorId;
	boolean plotActive;
	Vector<TimeValue> record;
	RedrawTimer redrawTimer;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		eventManager.setListener(new RecordEventManagerListener());
		eventManager.listenToLoggedData();
		eventManager.listenToLoggerStatus();
		eventManager.listenToSettings("profiles");

		redrawTimer = new RedrawTimer();

		profileSensorId = null;
		record = null;
	}

	@Override
	protected String getDomainLabel() {
		return "Time";
	}

	@Override
	protected void close() {
		this.profileSensorId = null;
		super.close();
	}

	public void openPlot(String profileSensorId) {
		super.openPlot();

		setTitle(getResources().getString(R.string.plot_recording_label));

		this.profileSensorId = profileSensorId;

		Model profileSensor = ProfileManager.get().getActiveProfile().getModel("sensors", true).getModel(profileSensorId);

		String sensorId = profileSensor.getString("sensorid", null);
		this.sensor = sensorId != null ? SensorWrapperManager.get().getSensor(sensorId) : null;
		this.setHeaderTitle(sensor != null ? sensor.getName() : "");

		this.sensorBrowserContainer.setVisibility(View.GONE);

		tryToInit();
	}

	private void tryToInit() {
		if (profileSensorId != null) {
			record = DataLogger.get().getCurrentRecord(profileSensorId);
			if (record != null) {
				this.series = new RecordXYSensorDataWrapper(this.sensor, getActivity(), record);
				this.series.initSeries(plot);
			}
		}
	}

	@Override
	public void sensorBrowserSelected(String sensorId) {
		destroyPlot();
		String profileSensorId = ProfileManager.get().getSensorProfileIdInActiveProfile(sensorId);
		if (profileSensorId != null) {
			openPlot(profileSensorId);
		} else {
			close();
		}
	}

	private class RecordEventManagerListener extends EventManagerListener {

		@Override
		public void eventSetting(String settingsId, boolean whilePaused) {
			if ("profiles".equals(settingsId)) {
				close();
			}
		}

		@Override
		public void eventNewData(String event, boolean whilePaused) {
			if (profileSensorId != null && profileSensorId.equals(event)) {
				redrawTimer.redraw();
			}
		}

		@Override
		public void eventDataStatus(String event, boolean whilePaused) {
			if ("start".equals(event) && profileSensorId != null) {
				destroyPlot();
				openPlot(profileSensorId);
			}
		}
	}

	@Override
	protected NumberFormat createDomainNumberFormat() {
		return new RecordTimePlotDomainFormat();
	}

	private class RecordTimePlotDomainFormat extends NumberFormat {

		private static final long serialVersionUID = -5396301066688752617L;
		DecimalFormat df;

		public RecordTimePlotDomainFormat() {
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

	private class RedrawTimer extends Timer {
		boolean idle;
		boolean requestRedraw;

		public RedrawTimer() {
			idle = true;
		}

		synchronized public void redraw() {
			if (idle) {
				idle = false;
				requestRedraw = false;
				this.schedule(new TimerTask() {
					@Override
					public void run() {
						waitComplete();
					}
				}, 1000);
				plot.redraw();
			} else {
				requestRedraw = true;
			}
		}

		synchronized public void waitComplete() {
			idle = true;
			if (requestRedraw) {
				requestRedraw = false;
				if (plot != null) {
					plot.redraw();
				}
			}
		}
	}
}