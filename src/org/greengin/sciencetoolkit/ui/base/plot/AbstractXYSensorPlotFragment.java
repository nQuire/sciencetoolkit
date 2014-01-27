package org.greengin.sciencetoolkit.ui.base.plot;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public abstract class AbstractXYSensorPlotFragment extends EventFragment implements OnClickListener, OnScaleGestureListener {

	protected String sensorId;
	protected SensorWrapper sensor;

	protected AbstractXYSensorSeriesWrapper series;

	protected Model seriesSettings;

	protected View plotPanel;
	protected XYPlot plot;
	ImageButton closeButton;

	ScaleGestureDetector scaleDetector;

	protected String getDomainLabel() {
		return "Time";
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		this.sensorId = null;
		this.sensor = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		plotPanel = inflater.inflate(R.layout.panel_plot, container, false);

		plot = (XYPlot) plotPanel.findViewById(R.id.plot);

		plot.setRangeLowerBoundary(0, BoundaryMode.GROW);
		plot.setRangeUpperBoundary(0, BoundaryMode.GROW);

		plot.setUserRangeOrigin(0);

		plot.setDomainValueFormat(new TimePlotDomainFormat());

		int background = this.getActivity().getResources().getColor(R.color.plot_outter_background);
		int gridBackground = this.getActivity().getResources().getColor(R.color.plot_inner_background);
		int lineColor = this.getActivity().getResources().getColor(R.color.plot_main_line);
		int sublineColor = this.getActivity().getResources().getColor(R.color.plot_minor_line);
		int transparent = this.getActivity().getResources().getColor(R.color.transparent);

		plot.getGraphWidget().setPaddingBottom(20f);
		plot.getGraphWidget().setPaddingTop(20f);

		plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);

		plot.getBackgroundPaint().setColor(background);
		plot.getGraphWidget().getBackgroundPaint().setColor(background);
		plot.getGraphWidget().getCursorLabelBackgroundPaint().setColor(background);
		plot.getGraphWidget().getGridBackgroundPaint().setColor(gridBackground);

		plot.getGraphWidget().getDomainGridLinePaint().setColor(lineColor);
		plot.getGraphWidget().getDomainSubGridLinePaint().setColor(sublineColor);
		plot.getGraphWidget().getDomainOriginLinePaint().setColor(lineColor);
		plot.getGraphWidget().getRangeGridLinePaint().setColor(lineColor);
		plot.getGraphWidget().getRangeSubGridLinePaint().setColor(sublineColor);
		plot.getGraphWidget().getRangeOriginLinePaint().setColor(lineColor);

		scaleDetector = new ScaleGestureDetector(getActivity(), this);

		plot.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return scaleDetector.onTouchEvent(event);
			}
		});

		closeButton = (ImageButton) this.plotPanel.findViewById(R.id.plot_close);
		closeButton.setOnClickListener(this);

		this.close();

		this.series = null;
		return plotPanel;
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

	protected void close() {
		this.plotPanel.setVisibility(View.GONE);
	}

	public void open() {
		this.plotPanel.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (v == closeButton) {
			close();
		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float k = 1f / detector.getScaleFactor();
		float top = Math.max(0, plot.getCalculatedMaxY().floatValue() * k);
		float bottom = Math.min(0, plot.getCalculatedMinY().floatValue() * k);
		plot.setRangeTopMax(top);
		plot.setRangeTopMin(top);
		plot.setRangeBottomMin(bottom);
		plot.setRangeBottomMax(bottom);
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}
}
