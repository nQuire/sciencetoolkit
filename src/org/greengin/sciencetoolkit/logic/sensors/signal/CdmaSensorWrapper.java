package org.greengin.sciencetoolkit.logic.sensors.signal;

import org.greengin.sciencetoolkit.logic.sensors.SensorWrapperManager;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

public class CdmaSensorWrapper extends AbstractSignalSensorWrapper {

	public CdmaSensorWrapper(Context applicationContext) {
		super(applicationContext);
		
		this.listener = new GsmPhoneStateListener();
	}

	


	@Override
	public int getType() {
		return SensorWrapperManager.CUSTOM_SENSOR_TYPE_CDMA;
	}


	@Override
	public String getName() {
		return "CDMA signal strength";
	}

	
	private class GsmPhoneStateListener extends PhoneStateListener {
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			fireInput(new float[] {signalStrength.getCdmaDbm()}, 1);
		}
	}

}
