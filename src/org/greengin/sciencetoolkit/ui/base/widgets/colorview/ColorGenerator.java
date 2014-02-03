package org.greengin.sciencetoolkit.ui.base.widgets.colorview;

import android.util.Log;

public class ColorGenerator {

	private final static int K = 3;
	
	
	public static String getHtmlColor(int n) {
		return getHtmlColor(n, 1f, 1f);
	}

	public static String getHtmlColor(int n, float s, float v) {
		return getHtml(getRGB(getAngle(n), s, v));
	}
	public static int[] getRbgColor(int n) {
		return getRgbColor(n, 1f, 1f);
	}

	public static int[] getRgbColor(int n, float s, float v) {
		return getIntRGB(getRGB(getAngle(n), s, v));
	}

	public static String getHtml(float[] rgb) {
		StringBuffer output = new StringBuffer("#");

		for (int i = 0; i < 3; i++) {
			output.append(String.format("%02x", (int) (255 * rgb[i])));
		}

		return output.toString();
	}
	
	public static int[] getIntRGB(float[] frgb) {
		int[] irgb = new int[3];
		for (int i = 0; i < 3; i++) {
			irgb[i] = (int) (255 * frgb[i]);
		}
		return irgb;
	}

	public static float[] getRGB(float h, float s, float v) {

		float c = v * s;
		float x = c * (1 - Math.abs((h / 60) % 2 - 1));
		float m = v - c;

		float r, g, b;
		if (h < 60) {
			r = c;
			g = x;
			b = 0;
		} else if (h < 120) {
			r = x;
			g = c;
			b = 0;
		} else if (h < 180) {
			r = 0;
			g = c;
			b = x;
		} else if (h < 240) {
			r = 0;
			g = x;
			b = c;
		} else if (h < 300) {
			r = x;
			g = 0;
			b = c;
		} else {
			r = c;
			g = 0;
			b = x;
		}

		return new float[] { r + m, g + m, b + m };
	}

	public static float getAngle(int n) {
		int n_k = n / K;

		int turn, n_in_turn, pow2;

		if (n_k > 0) {
			int l2 = (int) Math.floor(Math.log(n_k) / Math.log(2));
			turn = l2 + 1;
			pow2 = (int) Math.pow(2, l2);
			n_in_turn = n - 3 * pow2;
		} else {
			turn = 0;
			n_in_turn = n;
			pow2 = 0;
		}

		float angle_step, angle_0;

		if (turn == 0) {
			angle_step = 120f;
			angle_0 = 0f;
		} else {
			angle_0 = 60f / pow2;
			angle_step = angle_0 * 2f;
		}
		float angle = angle_0 + n_in_turn * angle_step;
		Log.d("stk color", "" + n + " " + angle);
		return angle;
	}

}
