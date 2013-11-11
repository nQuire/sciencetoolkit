package org.greengin.sciencetoolkit.ui.modelconfig.widgets.datetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.greengin.sciencetoolkit.model.Model;
import org.greengin.sciencetoolkit.ui.modelconfig.ModelKeyChangeListener;
import org.greengin.sciencetoolkit.ui.modelconfig.OnSecondsSetListener;
import org.greengin.sciencetoolkit.ui.modelconfig.SecondsPickerDialogFragment;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class DateTimePickerHelper implements OnDateSetListener, OnTimeSetListener, OnSecondsSetListener, OnClickListener {

	FragmentActivity activity;
	TextView activeView;
	Model settings;
	String key;
	String type;
	long defaultValue;
	GregorianCalendar calendar;
	SimpleDateFormat formatter;
	ModelKeyChangeListener listener;

	public DateTimePickerHelper(FragmentActivity activity, TextView activeView, Model settings, String key, String type, long defaultValue, ModelKeyChangeListener listener) {
		this.activity = activity;
		this.settings = settings;
		this.key = key;
		this.type = type;
		this.activeView = activeView;
		this.defaultValue = defaultValue;
		this.calendar = new GregorianCalendar();

		String format = null;
		if ("date".equals(type)) {
			format = "dd/MM/yy";
		} else if ("time".equals(type)) {
			format = "hh:mm";
		} else {
			format = "hh:mm:ss.SSS";
		}
		formatter = new SimpleDateFormat(format, activity.getResources().getConfiguration().locale);
		
		updateValue();

		this.listener = listener;
	}
	
	public void updateValue() {
		calendar.setTimeInMillis(settings.getLong(key, defaultValue));
		updateActiveView();
	}

	private void updateActiveView() {
		activeView.setText(formatter.format(new Date(calendar.getTimeInMillis())));
	}

	@Override
	public void onClick(View view) {
		if ("date".equals(type)) {
			new DatePickerDialog(view.getContext(), this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
		} else if ("time".equals(type)) {
			new TimePickerDialog(view.getContext(), this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
		} else if ("millis".equals(type)) {
			new SecondsPickerDialogFragment().init(this, calendar.getTimeInMillis()).show(activity.getSupportFragmentManager(), null);
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		setValue();
	}

	@Override
	public void onTimeSet(TimePicker picker, int hour, int minute) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		setValue();
	}

	@Override
	public void onSecondsSet(long value) {
		GregorianCalendar temp = new GregorianCalendar();
		temp.setTimeInMillis(value);

		calendar.set(Calendar.HOUR_OF_DAY, temp.get(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, temp.get(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, temp.get(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, temp.get(Calendar.MILLISECOND));
		
		setValue();
	}

	private void setValue() {
		boolean modified = settings.setLong(key, calendar.getTimeInMillis());
		if (modified) {
			updateActiveView();
			
			if (listener != null) {
				listener.modelKeyModified(key);
			}
		}
	}

}
