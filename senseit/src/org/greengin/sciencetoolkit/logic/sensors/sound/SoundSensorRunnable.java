package org.greengin.sciencetoolkit.logic.sensors.sound;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.LocalBroadcastManager;

public class SoundSensorRunnable implements Runnable {
	private Lock recordLock = new ReentrantLock();

	private boolean enabled;
	private int mediaSource;
	private Context context;

	private int freq;
	private int length;
	private int requestedLength;
	private int bufferLength;
	private short buffer[];
	private AudioRecord record;

	public SoundSensorRunnable(Context context) {
		this.context = context;
		this.mediaSource = MediaRecorder.AudioSource.MIC;
		this.record = null;
	}

	@Override
	public void run() {
		this.enabled = true;

		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		while (this.enabled) {
			Intent intent = null;

			try {
				recordLock.lock();

				if (this.requestedLength > 0) {
					this.length = this.requestedLength;
					this.bufferLength = this.length * this.freq / 1000;
					this.buffer = new short[this.bufferLength];

					if (record != null) {
						record.stop();
						record.release();
						record = null;
					}

					this.requestedLength = 0;
				}

				recordLock.unlock();

				recordLock.lock();

				if (record == null) {
					this.record = new AudioRecord(this.mediaSource, this.freq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * this.bufferLength);
					record.startRecording();
				}
				recordLock.unlock();

				try {
					record.read(buffer, 0, bufferLength);

					float value = 0;
					int is;
					for (short s : buffer) {
						is = s;
						value += is * is;
					}
					if (value > 0) {
						value = (float) (10 * Math.log10(value / bufferLength));

						intent = new Intent(SoundSensorFFTWrapper.STK_SOUND_SENSOR_NEWVALUE);
						intent.putExtra("value", value);
					} 
				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} finally {
				if (intent != null) {
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				}
			}

		}
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public void stopSensor() {

		recordLock.lock();
		this.enabled = false;
		if (record != null) {
			record.stop();
			record.release();
			record = null;
		}
		recordLock.unlock();
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		recordLock.lock();
		this.requestedLength = length;
		recordLock.unlock();
	}

}
