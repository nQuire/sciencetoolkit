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
	private int fftN;
	private FFT fft;
	private double fftRe[];

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

					this.fftN = (int) Math.pow(2, Math.floor(Math.log(this.bufferLength) / Math.log(2)));
					this.fft = new FFT(this.fftN);
					this.fftRe = new double[this.fftN];
					
					if (record != null) {
						record.stop();
						record.release();
						record = null;
					}

					this.requestedLength = 0;
				}

				recordLock.unlock();

				if (record == null) {
					this.record = new AudioRecord(this.mediaSource, this.freq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 2 * this.bufferLength);
					record.startRecording();
				}

				record.read(buffer, 0, bufferLength);

				float value = 0;
				int is;
				for (short s : buffer) {
					is = s;
					value += is * is;
				}
				value = (float) (10 * Math.log10(value / bufferLength));

				for (int i = 0; i < fftN; i++) {
					fftRe[i] = buffer[i];
				}

				fft.fft(fftRe);
				fft.filter(fftRe);

				float maxfreq = (float) fft.getMaxFreq(fftRe, this.freq);
				intent = new Intent(SoundSensorWrapper.STK_SOUND_SENSOR_NEWVALUE);
				intent.putExtra("value", value);
				intent.putExtra("maxfreq", maxfreq);

			} catch (IllegalArgumentException e) {
			} finally {
				if (intent != null) {
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				}
			}
		}

		if (record != null) {
			record.stop();
			record.release();
			record = null;
		}
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public void stopSensor() {
		this.enabled = false;
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
