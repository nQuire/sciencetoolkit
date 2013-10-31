package org.greengin.sciencetoolkit.ui.modelconfig;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.greengin.sciencetoolkit.model.Model;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class DateTimePickerHelper implements OnDateSetListener, OnTimeSetListener, OnClickListener {

	TextView activeView;
	Model settings;
	String key;
	String type;
	GregorianCalendar calendar;

	public DateTimePickerHelper(Model settings, String key, String type) {
		this.settings = settings;
		this.key = key;
		this.type = type;
		this.activeView = null;
		this.calendar = new GregorianCalendar();
		calendar.setTimeInMillis((Long) settings.getLong(key));
	}


	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		settings.setLong(key, calendar.getTimeInMillis());
		activeView.setText(DateFormat.format("dd/MM/yy", calendar.getTimeInMillis()));
	}

	@Override
	public void onClick(View view) {
		activeView = (TextView) view;

		if ("date".equals(type)) {
			new DatePickerDialog(view.getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
		} else if ("time".equals(type)) {
			new TimePickerDialog(view.getContext(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
		}

	}


	@Override
	public void onTimeSet(TimePicker picker, int hour, int minute) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		settings.setLong(key, calendar.getTimeInMillis());
		activeView.setText(DateFormat.format("hh:mm", calendar.getTimeInMillis()));
	}
}
