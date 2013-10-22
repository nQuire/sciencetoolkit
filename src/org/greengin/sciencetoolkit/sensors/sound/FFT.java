/*
 *  Copyright 2006-2007 Columbia University.
 *
 *  This file is part of MEAPsoft.
 *
 *  MEAPsoft is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 *
 *  MEAPsoft is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MEAPsoft; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA
 *
 *  See the file "COPYING" for the text of the license.
 */

package org.greengin.sciencetoolkit.sensors.sound;

public class FFT {

	int n, n_2, m;

	// Lookup tables. Only need to recompute when size of FFT changes.
	double[] cos;
	double[] sin;

	public FFT(int n) {
		this.n = n;
		this.n_2 = n/2;
		this.m = (int) (Math.log(n) / Math.log(2));

		// Make sure n is a power of 2
		if (n != (1 << m))
			throw new RuntimeException("FFT length must be power of 2");

		// precompute tables
		cos = new double[n / 2];
		sin = new double[n / 2];

		// for(int i=0; i<n/4; i++) {
		// cos[i] = Math.cos(-2*Math.PI*i/n);
		// sin[n/4-i] = cos[i];
		// cos[n/2-i] = -cos[i];
		// sin[n/4+i] = cos[i];
		// cos[n/2+i] = -cos[i];
		// sin[n*3/4-i] = -cos[i];
		// cos[n-i] = cos[i];
		// sin[n*3/4+i] = -cos[i];
		// }

		for (int i = 0; i < n / 2; i++) {
			cos[i] = Math.cos(-2 * Math.PI * i / n);
			sin[i] = Math.sin(-2 * Math.PI * i / n);
		}
	}

	/***************************************************************
	 * fft.c Douglas L. Jones University of Illinois at Urbana-Champaign January
	 * 19, 1992 http://cnx.rice.edu/content/m12016/latest/
	 * 
	 * fft: in-place radix-2 DIT DFT of a complex input
	 * 
	 * input: n: length of FFT: must be a power of two m: n = 2**m input/output
	 * x: double array of length n with real part of data y: double array of
	 * length n with imag part of data
	 * 
	 * Permission to copy and use this program is granted as long as this header
	 * is included.
	 ****************************************************************/
	/* modified to work only with real part*/
	public void fft(double[] x) {
		int i, j, k, n1, n2, a;
		double c, t1;

		// Bit-reverse
		j = 0;
		n2 = n_2;
		for (i = 1; i < n - 1; i++) {
			n1 = n2;
			while (j >= n1) {
				j = j - n1;
				n1 = n1 / 2;
			}
			j = j + n1;

			if (i < j) {
				t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
			}
		}

		// FFT
		n1 = 0;
		n2 = 1;

		for (i = 0; i < m; i++) {
			n1 = n2;
			n2 = n2 + n2;
			a = 0;

			for (j = 0; j < n1; j++) {
				c = cos[a];
				a += 1 << (m - i - 1);

				for (k = j; k < n; k = k + n2) {
					t1 = c * x[k + n1];
					x[k + n1] = x[k] - t1;
					x[k] = x[k] + t1;
				}
			}
		}
	}

	public void filter(double[] re) {
		int alllast = n - 1;
		for (int i = 0; i < n_2; i++) {
			re[i] = .5 * (re[i] + re[alllast - i]);
		}
		
		int last = n_2 - 1;
		
		double[] oldValues = new double[5];
		
		for (int i = 0; i < n_2; i++) {
			int previous = Math.min(i,  oldValues.length);
			int after = Math.min(last - i, oldValues.length);

			double v = 0;

			for (int j = 0; i < previous; i++) {
				v += oldValues[j];
			}
			for (int j = 1; j < oldValues.length; j++) {
				oldValues[j] = oldValues[j-1];
			}
			oldValues[0] = re[i];
			
			v += re[i];
			
			for (int j = 0; j < after; j++) {
				v += re[i+j+1];
			}
			
			re[i] = v / (previous + after + 1);
		}
	}
	
	
	public double getMaxFreq(double[] re, double samplerate) {
		double max = -1;
		int maxI = 0;
		for (int i = 0; i < n_2; i++) {
			if (re[i] > max) {
				max = re[i];
				maxI = i;
			}
		}
		
		return maxI * samplerate / n;
	}
	
}