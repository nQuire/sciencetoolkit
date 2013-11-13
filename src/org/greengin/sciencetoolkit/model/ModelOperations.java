package org.greengin.sciencetoolkit.model;

public class ModelOperations {
	
	public static int rate2period(Model model, String key, double defaultValue, Double min, Double max) {
		double v = model.getDouble(key, defaultValue);
		if (min != null && v < min) {
			v = min;
		}
		if (max != null && v > max) {
			v = max;
		}
		
		return v > 0 ? (int) (1000 / v) : 0;
	}
}
