package org.greengin.sciencetoolkit.ui.base.plot;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.YValueMarker;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public abstract class AbstractXYSensorPlotFragment extends EventFragment implements OnClickListener {

	protected String sensorId;
	protected SensorWrapper sensor;

	protected AbstractXYSensorSeriesWrapper series;

	protected Model seriesSettings;

	protected View plotPanel;
	protected LinearLayout plotContainer;
	protected XYPlot plot;
	ImageButton closeButton;

	PlotScaleGestureDetector scaleDetector;
	SensorBrowserLayout sensorBrowser;
	
	
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
	public void onDetach() {
		this.close();
		super.onDetach();
	}

	/*
	 * 
	 * <com.androidplot.xy.XYPlot android:id="@+id/plot"
	 * android:layout_width="match_parent" android:layout_height="match_parent"
	 * android:layout_below="@id/plot_header"
	 * android:layout_margin="@dimen/plot_margin"
	 * androidPlot.domainLabelWidget.labelPaint
	 * .textSize="@dimen/plot_domain_label_font_size"
	 * androidPlot.graphWidget.domainLabelPaint
	 * .textSize="@dimen/plot_domain_tick_label_font_size"
	 * androidPlot.graphWidget
	 * .domainOriginLabelPaint.textSize="@dimen/plot_domain_tick_label_font_size"
	 * androidPlot.graphWidget.rangeLabelPaint.textSize=
	 * "@dimen/plot_range_tick_label_font_size"
	 * androidPlot.graphWidget.rangeOriginLabelPaint
	 * .textSize="@dimen/plot_range_tick_label_font_size"
	 * androidPlot.legendWidget.heightMetric.value="25dp"
	 * androidPlot.legendWidget.iconSizeMetrics.heightMetric.value="15dp"
	 * androidPlot.legendWidget.iconSizeMetrics.widthMetric.value="15dp"
	 * androidPlot.legendWidget.positionMetrics.anchor="right_bottom"
	 * androidPlot
	 * .legendWidget.textPaint.textSize="@dimen/plot_legend_text_font_size"
	 * androidPlot
	 * .rangeLabelWidget.labelPaint.textSize="@dimen/plot_range_label_font_size"
	 * androidPlot.titleWidget.labelPaint.textSize="@dimen/plot_title_font_size"
	 * android:paddingTop="@dimen/plot_padding"
	 * android:paddingLeft="@dimen/plot_padding"
	 * android:paddingRight="@dimen/plot_padding"
	 * android:paddingBottom="@dimen/plot_padding_bottom" />
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		plotPanel = inflater.inflate(R.layout.panel_plot, container, false);
		plotContainer = (LinearLayout) plotPanel.findViewById(R.id.plot_container);

		scaleDetector = new PlotScaleGestureDetector();
		
		sensorBrowser = (SensorBrowserLayout) plotPanel.findViewById(R.id.plot_sensor_browser).findViewById(R.id.sensor_browser_layout);
		
		closeButton = (ImageButton) this.plotPanel.findViewById(R.id.plot_close);
		closeButton.setOnClickListener(this);

		this.close();

		this.series = null;
		return plotPanel;
	}

	private void createPlot() {
		plot = new XYPlot(plotContainer.getContext(), "plot");
		plotContainer.addView(plot);
		
		

		plot.setRangeLowerBoundary(0, BoundaryMode.GROW);
		plot.setRangeUpperBoundary(0, BoundaryMode.GROW);
		plot.setDomainValueFormat(new TimePlotDomainFormat());

		int background = this.getActivity().getResources().getColor(R.color.plot_outter_background);
		int gridBackground = this.getActivity().getResources().getColor(R.color.plot_inner_background);
		int lineColor = this.getActivity().getResources().getColor(R.color.plot_main_line);
		int sublineColor = this.getActivity().getResources().getColor(R.color.plot_minor_line);
		int transparent = this.getActivity().getResources().getColor(R.color.transparent);
		
		plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);

		plot.getBackgroundPaint().setColor(background);
		plot.getGraphWidget().getBackgroundPaint().setColor(background);
		plot.getGraphWidget().getCursorLabelBackgroundPaint().setColor(background);
		plot.getGraphWidget().getGridBackgroundPaint().setColor(gridBackground);

		plot.getGraphWidget().getDomainGridLinePaint().setColor(lineColor);

		plot.getGraphWidget().getDomainGridLinePaint().setColor(lineColor);
		plot.getGraphWidget().getDomainSubGridLinePaint().setColor(sublineColor);
		plot.getGraphWidget().getDomainOriginLinePaint().setColor(lineColor);
		plot.getGraphWidget().getRangeGridLinePaint().setColor(lineColor);
		plot.getGraphWidget().getRangeSubGridLinePaint().setColor(sublineColor);
		plot.getGraphWidget().getRangeOriginLinePaint().setColor(lineColor);
		
		YValueMarker zero = new YValueMarker(0, null);
		zero.getLinePaint().setColor(lineColor);
		zero.getLinePaint().setStrokeWidth(5);
		zero.getTextPaint().setColor(transparent);
		
		plot.addMarker(zero);
		
		plot.getGraphWidget().setMargins(10f, 20f, 20f, 20f);
		plot.getGraphWidget().setPadding(0f, 0f, 0f, 20f);

		plot.setOnTouchListener(scaleDetector);
	}

	/*public void updateSeriesConfig(boolean anyway) {
		this.series.updateSeriesConfiguration(plot, anyway);
	}*/

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
		destroyPlot();
		this.plotPanel.setVisibility(View.GONE);
	}
	
	protected void destroyPlot() {
		if (plot != null) {
			plotContainer.removeView(plot);
			plot = null;
		}
	}
	

	public void openPlot() {
		this.plotPanel.setVisibility(View.VISIBLE);
		this.createPlot();
	}
	

	@Override
	public void onClick(View v) {
		if (v == closeButton) {
			close();
		}
	}

	private class PlotScaleGestureDetector implements OnTouchListener {

		float rt, rh;
		float pt0, pb0;
		float pt1, pb1;
		float max, min;
		boolean scaling;

		public PlotScaleGestureDetector() {
			pt1 = pb1 = 0;
			scaling = false;
		}

		private void updateValues(MotionEvent event) {
			RectF rect = plot.getGraphWidget().getGridRect();
			rt = rect.top;
			rh = rect.bottom - rt;
			pt0 = pt1;
			pb0 = pb1;
			pt1 = (Math.min(event.getY(0), event.getY(1)) - rt) / rh;
			pb1 = (Math.max(event.getY(0), event.getY(1)) - rt) / rh;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				if (event.getPointerCount() == 2) {
					scaling = true;
					max = plot.getCalculatedMaxY().floatValue();
					min = plot.getCalculatedMinY().floatValue();
					updateValues(event);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				if (scaling) {
					plot.setRangeLowerBoundary(0, BoundaryMode.GROW);
					plot.setRangeUpperBoundary(0, BoundaryMode.GROW);
				}
				scaling = false;
				break;
			case MotionEvent.ACTION_MOVE: {
				if (scaling) {
					updateValues(event);
					if (pt1 != pb1) {
						float nmax = (((min - max) * pb0 + max) * pt1 + (max - min) * pb1 * pt0 - max * pb1) / (pt1 - pb1);
						float nmin = nmax + ((min - max) * pt0 + (max - min) * pb0) / (pt1 - pb1);

						max = nmax;
						min = nmin;

						plot.setRangeLowerBoundary(min, BoundaryMode.FIXED);
						plot.setRangeUpperBoundary(max, BoundaryMode.FIXED);
					}
				}
				break;
			}
			}
			return true;
		}
	}
}
