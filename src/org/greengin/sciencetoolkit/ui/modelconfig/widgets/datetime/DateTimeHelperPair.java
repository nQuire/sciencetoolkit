package org.greengin.sciencetoolkit.ui.modelconfig.widgets.datetime;

public class DateTimeHelperPair {

	DateTimePickerHelper date;
	DateTimePickerHelper time;

	public DateTimeHelperPair(DateTimePickerHelper date, DateTimePickerHelper time) {
		this.date = date;
		this.time = time;
	}
	
	public void updateValue() {
		date.updateValue();
		time.updateValue();
	}
}
