package org.greengin.sciencetoolkit.ui.base.plot;

import java.text.NumberFormat;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.ui.base.SwipeActivity;
import org.greengin.sciencetoolkit.ui.base.events.EventFragment;

import com.androidplot.Plot;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.YValueMarker;

import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
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
	
	protected boolean growAfterScale = true;
	protected boolean scaleX = false;

	PlotScaleGestureDetector scaleDetector;
	
	protected View sensorBrowserContainer;
	protected SensorBrowserLayout sensorBrowser;
	
	
	
	public void setScaleParams(boolean growAfterScale, boolean scaleX) {
		this.growAfterScale = growAfterScale;
		this.scaleX = scaleX;
	}
	
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
		int transparent = this.getActivity().getResources().getColor(android.R.color.transparent);
		
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
		
		
		plot.getLegendWidget().getPositionMetrics().getYPositionMetric().set(25, YLayoutStyle.ABSOLUTE_FROM_BOTTOM);
		plot.getLegendWidget().getHeightMetric().set(30, SizeLayoutType.ABSOLUTE);
		plot.getLegendWidget().getTextPaint().setTextSize(20f);
		
		YValueMarker zero = new YValueMarker(0, null);
		zero.getLinePaint().setColor(lineColor);
		zero.getLinePaint().setStrokeWidth(5);
		zero.getTextPaint().setColor(transparent);
		
		plot.addMarker(zero);
		
		plot.getGraphWidget().setMargins(10f, 20f, 20f, 30f);
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
		float pmin0y, pmax0y;
		float pmin1y, pmax1y;
		float maxy, miny;
		
		float rl, rw;
		float pmax0x, pmin0x;
		float pmax1x, pmin1x;
		float maxx, minx;

		boolean scaling;

		public PlotScaleGestureDetector() {
			pmin1y = pmax1y = 0;
			pmin1x = pmax1x = 0;
			scaling = false;
		}

		private void updateValues(MotionEvent event) {
			RectF rect = plot.getGraphWidget().getGridRect();
			rt = rect.top;
			rh = rect.bottom - rt;
			pmin0y = pmin1y;
			pmax0y = pmax1y;
			pmin1y = (Math.min(event.getY(0), event.getY(1)) - rt) / rh;
			pmax1y = (Math.max(event.getY(0), event.getY(1)) - rt) / rh;
			
			rl = rect.right;
			rw = rect.left - rl;
			pmin0x = pmin1x;
			pmax0x = pmax1x;
			pmin1x = (Math.min(event.getX(0), event.getX(1)) - rl) / rw;
			pmax1x = (Math.max(event.getX(0), event.getX(1)) - rl) / rw;

		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				if (event.getPointerCount() == 2) {
					scaling = true;
					maxy = plot.getCalculatedMaxY().floatValue();
					miny = plot.getCalculatedMinY().floatValue();
					
					minx = plot.getCalculatedMinX().floatValue();
					maxx = plot.getCalculatedMaxX().floatValue();
					updateValues(event);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				if (scaling && growAfterScale) {
					plot.setRangeLowerBoundary(0, BoundaryMode.GROW);
					plot.setRangeUpperBoundary(0, BoundaryMode.GROW);
				}
				scaling = false;
				break;
			case MotionEvent.ACTION_MOVE: {
				if (scaling) {
					updateValues(event);
					if (pmin1y != pmax1y) {
						float nmaxy = (((miny - maxy) * pmax0y + maxy) * pmin1y + (maxy - miny) * pmax1y * pmin0y - maxy * pmax1y) / (pmin1y - pmax1y);
						float nminy = nmaxy + ((miny - maxy) * pmin0y + (maxy - miny) * pmax0y) / (pmin1y - pmax1y);
						maxy = nmaxy;
						miny = nminy;

						plot.setRangeLowerBoundary(miny, BoundaryMode.FIXED);
						plot.setRangeUpperBoundary(maxy, BoundaryMode.FIXED);
					}
					if (scaleX && pmin1x != pmax1x) {
						float nmaxx = (((minx - maxx) * pmax0x + maxx) * pmin1x + (maxx - minx) * pmax1x * pmin0x - maxx * pmax1x) / (pmin1x - pmax1x);
						float nminx = nmaxx + ((minx - maxx) * pmin0x + (maxx - minx) * pmax0x) / (pmin1x - pmax1x);
						maxx = nmaxx;
						minx = nminx;
						plot.setDomainBoundaries(minx, BoundaryMode.FIXED, maxx, BoundaryMode.FIXED);
					}
					plot.redraw();
				}
				break;
			}
			}
			
			((SwipeActivity) getActivity()).setPagingEnabled(!scaling);
			return true;
		}
	}
	
	protected abstract NumberFormat createDomainNumberFormat();
}
