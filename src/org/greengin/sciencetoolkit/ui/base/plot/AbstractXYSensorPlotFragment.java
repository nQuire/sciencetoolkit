package org.greengin.sciencetoolkit.ui.base.plot;

import java.text.NumberFormat;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.YValueMarker;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class AbstractXYSensorPlotFragment extends EventFragment {

	protected View plotPanel;
	protected LinearLayout plotContainer;
	protected XYPlot plot;

	PlotScaleGestureDetector scaleDetector;
	
	protected View sensorBrowserContainer;
	protected SensorBrowserLayout sensorBrowser;
	
	protected String getDomainLabel() {
		return "Time";
	}
	
	public void setTitle(String title) {
		((TextView) plotPanel.findViewById(R.id.plot_header_label)).setTag(title);
	}
	
	protected int layoutId() {
		return R.layout.panel_plot;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		plotPanel = inflater.inflate(layoutId(), container, false);
		plotContainer = (LinearLayout) plotPanel.findViewById(R.id.plot_container);

		scaleDetector = new PlotScaleGestureDetector();
		
		sensorBrowserContainer = plotPanel.findViewById(R.id.plot_sensor_browser);
		sensorBrowser = (SensorBrowserLayout) sensorBrowserContainer.findViewById(R.id.sensor_browser_layout);
		
		return plotPanel;
	}

	protected void createPlot() {
		plot = new XYPlot(plotContainer.getContext(), (String) null);
		plotContainer.addView(plot);

		plot.setRangeLowerBoundary(0, BoundaryMode.GROW);
		plot.setRangeUpperBoundary(0, BoundaryMode.GROW);
		plot.setDomainValueFormat(createDomainNumberFormat());

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

		
	protected void destroyPlot() {
		if (plot != null) {
			plotContainer.removeView(plot);
			plot = null;
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
			Log.d("stk plot", "on touch");
			
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
						Log.d("stk plot", "on touch " + nmax + " " + max);
						max = nmax;
						min = nmin;

						plot.setRangeLowerBoundary(min, BoundaryMode.FIXED);
						plot.setRangeUpperBoundary(max, BoundaryMode.FIXED);
						plot.redraw();
					}
				}
				break;
			}
			}
			return true;
		}
	}
	
	protected abstract NumberFormat createDomainNumberFormat();
}
