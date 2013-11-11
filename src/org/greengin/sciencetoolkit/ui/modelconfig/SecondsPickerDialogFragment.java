package org.greengin.sciencetoolkit.ui.modelconfig;

import java.util.GregorianCalendar;

import net.simonvt.numberpicker.NumberPicker;
import net.simonvt.numberpicker.NumberPicker.Formatter;

import org.greengin.sciencetoolkit.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class SecondsPickerDialogFragment extends DialogFragment implements NumberPicker.OnValueChangeListener {

	NumberPicker hnp;
	NumberPicker mnp;
	NumberPicker snp;
	NumberPicker msnp;
	
	OnSecondsSetListener listener;
	long initValue;

	public SecondsPickerDialogFragment() {
		super();
	}
	
	public SecondsPickerDialogFragment init(OnSecondsSetListener listener, long initValue) {
		this.listener = listener;
		this.initValue = initValue;
		return this;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View contentView = inflater.inflate(R.layout.dialog_seconds_picker, null);
		builder.setTitle(R.string.dialog_seconds_picker_title).setView(contentView).setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dismiss();
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dismiss();
			}
		});
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(initValue);

		SecondsPickerFormatter spf2 = new SecondsPickerFormatter(2);

		hnp = (NumberPicker) contentView.findViewById(R.id.hourPicker);
		hnp.setMaxValue(23);
		hnp.setMinValue(0);
		hnp.setFormatter(spf2);
		hnp.setValue(calendar.get(GregorianCalendar.HOUR_OF_DAY));
		hnp.setOnValueChangedListener(this);

		mnp = (NumberPicker) contentView.findViewById(R.id.minPicker);
		mnp.setMaxValue(59);
		mnp.setMinValue(0);
		mnp.setFormatter(spf2);
		mnp.setValue(calendar.get(GregorianCalendar.MINUTE));
		mnp.setOnValueChangedListener(this);

		snp = (NumberPicker) contentView.findViewById(R.id.secPicker);
		snp.setMaxValue(59);
		snp.setMinValue(0);
		snp.setFormatter(spf2);
		snp.setValue(calendar.get(GregorianCalendar.SECOND));
		snp.setOnValueChangedListener(this);

		msnp = (NumberPicker) contentView.findViewById(R.id.msPicker);
		msnp.setMaxValue(999);
		msnp.setMinValue(0);
		msnp.setFormatter(new SecondsPickerFormatter(3));
		msnp.setValue(calendar.get(GregorianCalendar.MILLISECOND));
		msnp.setOnValueChangedListener(this);

		return builder.create();
	}

	private static class SecondsPickerFormatter implements Formatter {
		String format;

		public SecondsPickerFormatter(int size) {
			format = "%0" + size + "d";
		}

		@Override
		public String format(int value) {
			return String.format(format, value);
		}
	}

	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

		int delta = 0;
		if (oldVal == picker.getMaxValue() && newVal == picker.getMinValue()) {
			delta = 1;
		} else if (oldVal == picker.getMinValue() && newVal == picker.getMaxValue()) {
			delta = -1;
		}

		if (delta != 0) {

			NumberPicker np = null;

			if (picker == msnp) {
				np = snp;
			} else if (picker == snp) {
				np = mnp;
			} else if (picker == mnp) {
				np = hnp;
			}

			if (np != null) {
				int oldNpValue = np.getValue();
				int newNpValue = oldNpValue + delta;
				if (newNpValue > np.getMaxValue()) {
					newNpValue = np.getMinValue();
				} else if (newNpValue < np.getMinValue()) {
					newNpValue = np.getMaxValue();
				}
				np.setValue(newNpValue);
				onValueChange(np, oldNpValue, newNpValue);
			}
		}
	}

}
