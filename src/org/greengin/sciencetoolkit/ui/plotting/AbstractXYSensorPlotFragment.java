package org.greengin.sciencetoolkit.ui.plotting;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.SensorUIData;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbstractXYSensorPlotFragment extends Fragment {

	protected String sensorId;
	protected SensorWrapper sensor;
	
	protected AbstractXYSensorSeriesWrapper series;

	protected Model seriesSettings;
	protected String seriesSettingPrefix;
	
	protected XYPlot plot;

	
	abstract void fetchSettings();

	abstract void createSeries();
	
	protected String getDomainLabel() {
		return "Time";
	}

	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(Arguments.ARG_SENSOR);
		this.sensor = SensorWrapperManager.getInstance().getSensor(this.sensorId);
		
		fetchSettings();
		createSeries();
		
		this.plot = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_plot_sensor, container, false);

		plot = (XYPlot) rootView;

		plot.setRangeLowerBoundary(0, BoundaryMode.AUTO);
		plot.setRangeUpperBoundary(0, BoundaryMode.AUTO);

		plot.setTicksPerRangeLabel(3);
		plot.setDomainStep(XYStepMode.SUBDIVIDE, 5);
		plot.setDomainValueFormat(new TimePlotDomainFormat());
		
		String[] units = SensorUIData.getValueUnits(sensor.getType());
		String unit = units != null && units.length > 0 ? units[0] : "";
		plot.setRangeLabel(unit);
		
		plot.setDomainLabel(getDomainLabel());

		updateSeriesConfig(true);

		return rootView;
	}


	public void updateSeriesConfig(boolean anyway) {
		this.series.updateSeriesConfiguration(plot, anyway);
	}
	

	private class TimePlotDomainFormat extends NumberFormat {

		private static final long serialVersionUID = 2941418063711671809L;

		DecimalFormat df;

		public TimePlotDomainFormat() {
			this.df = new DecimalFormat();
		}

		@Override
		public StringBuffer format(double d, StringBuffer sb, FieldPosition fp) {
			return format((long) d, sb, fp);
		}

		@Override
		public StringBuffer format(long l, StringBuffer sb, FieldPosition fp) {
			long dm = Math.max(0, System.currentTimeMillis() - l);
			int ds = (int) Math.log10(Math.max(1, dm));

			if (ds < 3) {
				return sb.append(dm).append(" ms");
			} else {
				df.setMaximumFractionDigits(Math.max(0, ds - 2));
				return sb.append(df.format(dm * .001)).append(" s");
			}
		}

		@Override
		public Number parse(String arg0, ParsePosition arg1) {
			return null;
		}

	}

}
