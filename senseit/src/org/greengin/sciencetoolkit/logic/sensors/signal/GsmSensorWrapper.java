package org.greengin.sciencetoolkit.logic.sensors.signal;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

public class GsmSensorWrapper extends AbstractSignalSensorWrapper {

	public GsmSensorWrapper(Context applicationContext) {
		super(applicationContext);
		
		this.listener = new GsmPhoneStateListener();
	}

	


	@Override
	public int getType() {
		return SensorWrapperManager.CUSTOM_SENSOR_TYPE_GSM;
	}



	@Override
	public String getName() {
		return "GSM signal strength";
	}
	
	
	
	private class GsmPhoneStateListener extends PhoneStateListener {
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			fireInput(new float[] {signalStrength.getGsmSignalStrength()}, 1);
		}
	}

}
