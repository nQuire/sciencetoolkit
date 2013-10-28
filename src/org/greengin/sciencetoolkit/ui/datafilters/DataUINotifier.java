package org.greengin.sciencetoolkit.ui.datafilters;

import org.greengin.sciencetoolkit.logic.streams.DataInput;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class DataUINotifier implements DataInput {

	
	Context context;
	String filter;
	
	public DataUINotifier(Context context, String filter) {
		this.context = context;
		this.filter = filter;
	}
	
	@Override
	public void value(float[] values, int valueCount) {
		Intent i = new Intent(filter);
		i.putExtra("values", values);
		i.putExtra("valueCount", valueCount);
		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}

}
