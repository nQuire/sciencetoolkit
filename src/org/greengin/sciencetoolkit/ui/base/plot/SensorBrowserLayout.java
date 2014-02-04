package org.greengin.sciencetoolkit.ui.base.plot;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;

import org.greengin.sciencetoolkit.R;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapper;
import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;
import org.greengin.sciencetoolkit.ui.base.SensorUIData;
import org.greengin.sciencetoolkit.ui.base.SwipeActivity;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorBrowserLayout extends ViewGroup implements OnTouchListener {

	Vector<String> sensorIds;
	Hashtable<String, View> sensorViews;
	Hashtable<String, Integer> viewPos;
	ColorMatrixColorFilter bwfilter;
	
	SensorBrowserListener listener;

	String selected;
	int selectedIndex;
	int deltaX;
	float downX;
	long downTime;
	float touchX;
	String touchedView;
	int itemWidth;

	public SensorBrowserLayout(Context context) {
		super(context);
		init();
	}

	public SensorBrowserLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SensorBrowserLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		ColorMatrix matrix = new ColorMatrix();
	    matrix.setSaturation(0);
	    bwfilter = new ColorMatrixColorFilter(matrix);
		this.sensorIds = null;
	}

	public void setSensors(SensorBrowserListener listener, Vector<String> sensorIds, String selected) {
		this.listener = listener;
		this.sensorIds = sensorIds;
		this.sensorViews = new Hashtable<String, View>();

		this.removeAllViews();

		LayoutInflater inflater = LayoutInflater.from(getContext());
		for (String sensorId : sensorIds) {
			SensorWrapper sensor = SensorWrapperManager.get().getSensor(sensorId);
			View view = inflater.inflate(R.layout.panel_sensor_browser_item, null);
			view.setTag(sensorId);
			((TextView) view.findViewById(R.id.sensor_name)).setText(sensor.getName());
			((ImageView) view.findViewById(R.id.sensor_icon)).setImageResource(SensorUIData.getSensorResource(sensor.getType()));
			this.addView(view);
			view.setOnTouchListener(this);
			sensorViews.put(sensorId, view);
		}

		setSelectedSensor(selected);
	}

	public void setSelectedSensor(String selected) {
		this.selected = selected;
		this.selectedIndex = sensorIds.indexOf(selected);
		this.deltaX = 0;

		for (Entry<String, View> entry : sensorViews.entrySet()) {
			((ImageView) entry.getValue().findViewById(R.id.sensor_icon)).setColorFilter(entry.getKey().equals(selected) ? null : bwfilter);
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = 0;

		int hspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int wspec = MeasureSpec.makeMeasureSpec(150, MeasureSpec.AT_MOST);
		itemWidth = 0;

		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.measure(wspec, hspec);
			height = Math.max(height, v.getMeasuredHeight());
			itemWidth = Math.max(itemWidth, v.getMeasuredWidth());
		}

		this.setMeasuredDimension(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int height = b - t;
		int width = r - l;

		int x = deltaX + width / 2 - itemWidth / 2 - itemWidth * this.selectedIndex;
		
		for (int i = 0; i < this.getChildCount(); i++, x += itemWidth) {
			View v = getChildAt(i);
			v.layout(x, 0, x + itemWidth, height);
		}
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		processTouchEvent(event);
		return true;		
	}
	

	
	private void processTouchEvent(MotionEvent event) {
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			((SwipeActivity) getContext()).setPagingEnabled(false);
			touchX = event.getX(0);
			downX = touchX;
			downTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (Math.abs(event.getX(0) - downX) < 5 && System.currentTimeMillis() - downTime < 500 && touchedView != null) {
				listener.sensorBrowserSelected(touchedView);
			}
			((SwipeActivity) getContext()).setPagingEnabled(true);
			break;
		case MotionEvent.ACTION_MOVE:
			deltaX += event.getX(0) - touchX;
			touchX = event.getX(0);
			requestLayout();
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		touchedView = (String) v.getTag();
		return false;
	}
	

}