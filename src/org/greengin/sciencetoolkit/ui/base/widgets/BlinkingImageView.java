package org.greengin.sciencetoolkit.ui.base.widgets;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.greengin.sciencetoolkit.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class BlinkingImageView extends ImageView {
	private static final String EVENT_FILTER_PREFIX = "BLINKING_IMAGE_";
	
	String filter;
	int period;
	Timer timer = null;
	boolean running = false;
	BroadcastReceiver receiver;

	public BlinkingImageView(Context context) throws IOException {
		super(context);
		this.init(null);
	}

	public BlinkingImageView(Context context, AttributeSet attrs) throws IOException {
		super(context, attrs);
		this.init(attrs);
	}

	public BlinkingImageView(Context context, AttributeSet attrs, int defStyle) throws IOException {
		super(context, attrs, defStyle);
		this.init(attrs);
	}

	private void init(AttributeSet attrs) {
		int defaultValue = 400;
		if (attrs != null) {
		TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.BlinkingImage);
		period = array.getInt(R.styleable.BlinkingImage_period, defaultValue);
		array.recycle();
		} else {
			period = 400;
		}
		
		this.receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				toggle();
			}
		};
		this.filter = EVENT_FILTER_PREFIX + System.identityHashCode(this);

		return;
	}
	
	private void toggle() {
		if (this.running) {
			setVisibility(getVisibility() != View.VISIBLE ? View.VISIBLE : View.INVISIBLE);
		}
	}

	private void start() {
		if (timer == null) {
			setVisibility(View.VISIBLE);
			LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(filter));

			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(filter));
				}
			}, period, period);
		}
	}

	private void stop() {
		if (timer != null) {
			LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);

			setVisibility(View.GONE);
			timer.cancel();
			timer = null;
		}
	}

	public void setBlinking(boolean blinking) {
		this.running = blinking;
		if (getWindowVisibility() == View.VISIBLE) {
			if (blinking) {
				start();
			} else {
				stop();
			}
		}
	}

	public void onAttachedToWindow() {
		Log.d("stk blinking", "attached");
		super.onAttachedToWindow();
		start();
	}

	public void onDetachedFromWindow() {
		Log.d("stk blinking", "detached");
		super.onDetachedFromWindow();
		stop();
	}

	public void onWindowVisibilityChanged(int visibility) {
		Log.d("stk blinking", "changed: " + visibility);
		if (visibility == View.VISIBLE) {
			if (running) {
				start();
			} else {
				stop();
			}
		}
	}

}