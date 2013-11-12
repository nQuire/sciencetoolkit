package org.greengin.sciencetoolkit.ui.components.main.data.view.visualizations;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.Arguments;
import org.greengin.sciencetoolkit.ui.components.main.data.view.AbstractDataVisualizationFragment;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class XYDataPlotFragment extends AbstractDataVisualizationFragment {

	String sensorId;
	XYDataPlotSeries series;
	XYPlot plot;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = getArguments().getString(Arguments.ARG_SENSOR);


		this.series = new XYDataPlotSeries(profileId, sensorId, settingsId, getActivity());
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

		updatePlotConfig();

		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		plot.redraw();
		updatePlotConfig();
	}

	@Override
	public void onPause() {
		super.onPause();
	}


	public void eventSeriesUpdated() {
		if (plot != null) {
			plot.redraw();
		}
	}

	public void updatePlotConfig() {
		this.series.updateConfiguration();
		this.series.updateSeries(plot);
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

	@Override
	protected void updateDataRange() {
		series.updateCursor(getCursor());
		plot.redraw();
	}
}