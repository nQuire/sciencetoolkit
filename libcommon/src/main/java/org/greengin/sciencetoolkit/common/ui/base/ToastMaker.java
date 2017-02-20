package org.greengin.sciencetoolkit.common.ui.base;

import org.greengin.sciencetoolkit.common.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.widget.Toast;

public class ToastMaker {

	public static void t(Activity activity, String text, int duration,
			int background, boolean onUiThread) {
		
		if (onUiThread) {
			activity.runOnUiThread(new ToastRunnable(activity, text, duration, background));
		} else {
			Toast toast = Toast.makeText(activity, text, duration);
			toast.getView().setBackgroundResource(background);
			toast.show();
		}
	}

	public static void te(Activity activity, String text, int duration, boolean onUiThread) {
		t(activity, text, duration, R.drawable.toast_error_shape, onUiThread);
	}

	public static void ti(Activity activity, String text, int duration, boolean onUiThread) {
		t(activity, text, duration, R.drawable.toast_shape, onUiThread);
	}

	public static void l(Activity activity, String text, boolean onUiThread) {
		ti(activity, text, Toast.LENGTH_LONG, onUiThread);
	}

	public static void s(Activity activity, String text, boolean onUiThread) {
		ti(activity, text, Toast.LENGTH_SHORT, onUiThread);
	}

	public static void le(Activity activity, String text, boolean onUiThread) {
		te(activity, text, Toast.LENGTH_LONG, onUiThread);
	}

	public static void se(Activity activity, String text, boolean onUiThread) {
		te(activity, text, Toast.LENGTH_SHORT, onUiThread);
	}

	public static void l(Activity activity, String text) {
		l(activity, text, false);
	}

	public static void s(Activity activity, String text) {
		s(activity, text, false);
	}

	public static void le(Activity activity, String text) {
		le(activity, text, false);
	}

	public static void se(Activity activity, String text) {
		se(activity, text, false);
	}

	
	private static class ToastRunnable implements Runnable {
		
		Activity activity;
		String text;
		int duration;
		int background;
		
		public ToastRunnable(Activity activity, String text, int duration,
				int background) {
			this.activity = activity;
			this.text = text;
			this.duration = duration;
			this.background = background;
		}

		@Override
		public void run() {
			Toast toast = Toast.makeText(activity, text, duration);
			toast.getView().setBackgroundResource(background);
			toast.show();			
		}
		
	}
	

}
