package org.greengin.sciencetoolkit.logic.signal;

import java.util.HashMap;
import java.util.Vector;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SignalStrengthManager extends PhoneStateListener {
	
	HashMap<String, Float> values;
	Vector<SignalStrengthListener> listeners;
	
	
	private static SignalStrengthManager instance;
	
	public static void init(Context context) {
		instance = new SignalStrengthManager();
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		manager.listen(instance, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	public static SignalStrengthManager get() {
		return instance;
	}
	
	private SignalStrengthManager() {
		values = new HashMap<String, Float>();
		values.put("gsm", 0f);
		values.put("cdma", 0f);
		
		listeners = new Vector<SignalStrengthListener>();
	}
	
	public void addListener(SignalStrengthListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(SignalStrengthListener listener) {
		listeners.remove(listener);
	}
	
	private void fireEvent() {
		for (SignalStrengthListener listener : listeners) {
			listener.signalStrengthChange();
		}
	}

	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		values.put("gsm", (float) signalStrength.getGsmSignalStrength());	
		values.put("cdma", (float) signalStrength.getCdmaDbm());	
		
		Log.d("stk signal", values.get("gsm") + " " + values.get("cdma"));
		fireEvent();
	}

	
	public float getSignalStrength(String network) {
		return values.get(network);
	}
	
	
}
