




//$$strtCprt
/**
* SndHnd
* 
* Copyright (C) 1992-2020 Thornton Green
* 
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* see <http://www.gnu.org/licenses>.
* Additional permission under GNU GPL version 3 section 7
*
*/
//$$endCprt







package cwaves;
import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * A waveform implementing a low-pass filter on an input waveform.
 * 
 * Note: this is still a work in progress.
 * 
 * See:  https://beausievers.com/synth/synthbasics/#lowpasshighpass
 * 
 * @author tgreen
 *
 */
public class LowPassFilter extends WaveForm {

	/**
	 * The input waveform.
	 */
	protected WaveForm wave;

	/**
	 * The half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 */
	protected double intervalHalfLength;

	/**
	 * The number of samples used to perform the filtering.
	 */
	protected int sampleLen;

	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 * @param _intervalHalfLength The half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 * @param _sampleLen The number of samples used to perform the filtering.
	 */
	public LowPassFilter(WaveForm _wave, double _intervalHalfLength,
			int _sampleLen) {
		super();
		wave = _wave;
		intervalHalfLength = _intervalHalfLength;
		sampleLen = _sampleLen;
	}

	/**
	 * Constructor used for persistence purposes only.
	 */
	public LowPassFilter() {
		super();
	}

	@Override
	public double eval(double param) {
		double intervalStart = param - intervalHalfLength;
		double intervalEnd = param + intervalHalfLength;

		double b0 = calcApproxB0(sampleLen, intervalStart, intervalEnd);
		double b2 = calcApproxB2(sampleLen, intervalStart, intervalEnd);
		double b1 = calcApproxB1(sampleLen + 2, b0, b2, intervalStart,
				intervalEnd);

		double u = 0.5;

		double b10 = (1 - u) * b0 + u * b1;
		double b11 = (1 - u) * b1 + u * b2;

		double b20 = (1 - u) * b10 + u * b11;

		return (b20);
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( wave.genClone() );
		if( wv == wave )
		{
			return( this );
		}
		else
		{
			return( new LowPassFilter( wv , intervalHalfLength , sampleLen ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GLowPassFilter wv = new GLowPassFilter();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w,intervalHalfLength,sampleLen);
		
		return( wv );
	}

	
	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	
	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	/**
	 * Estimates the b0 Bezier point for the quadratic Bezier curve approximating the half-wave to result from the filter.
	 * @param len The number of points to sample to generate the estimate.
	 * @param intervalStart The start of the interval.
	 * @param intervalEnd The end of the interval.
	 * @return The estimated b0 Bezier point.
	 */
	public double calcApproxB0(int len, double intervalStart, double intervalEnd) {
		double tot = 0.0;
		double totParam = 0.0;
		int count;
		for (count = 0; count < len; count++) {
			double uv = ((double) count) / (len - 1);
			double u = (1 - uv) * (-1.0) + (uv) * (1.0);
			double param = (1 - u) * intervalStart + u * intervalEnd;
			double multp = 1.0 - Math.abs(u);
			tot += wave.eval(param) * multp;
			totParam += multp;
		}
		return (tot / totParam);
	}

	/**
	 * Estimates the b2 Bezier point for the quadratic Bezier curve approximating the half-wave to result from the filter.
	 * @param len The number of points to sample to generate the estimate.
	 * @param intervalStart The start of the interval.
	 * @param intervalEnd The end of the interval.
	 * @return The estimated b2 Bezier point.
	 */
	public double calcApproxB2(int len, double intervalStart, double intervalEnd) {
		double tot = 0.0;
		double totParam = 0.0;
		int count;
		for (count = 0; count < len; count++) {
			double uv = ((double) count) / (len - 1);
			double u = (1 - uv) * (0.0) + (uv) * (2.0);
			double param = (1 - u) * intervalStart + u * intervalEnd;
			double multp = 1.0 - Math.abs(1.0 - u);
			tot += wave.eval(param) * multp;
			totParam += multp;
		}
		return (tot / totParam);
	}

	/**
	 * Estimates the b1 Bezier point for the quadratic Bezier curve approximating the half-wave to result from the filter.
	 * @param len The number of points to sample to generate the estimate.
	 * @param b0 The input estimated b0 Bezier point.
	 * @param b2 The input estimated b2 Bezier point.
	 * @param intervalStart The start of the interval.
	 * @param intervalEnd The end of the interval.
	 * @return The estimated b1 Bezier point.
	 */
	public double calcApproxB1(int len, double b0, double b2,
			double intervalStart, double intervalEnd) {
		int count = 0;
		double ptTotal = 0.0;

		for (count = 1; count < (len - 1); count++) {
			double uv = ((double) count) / (len - 1);

			double cnstb10 = (1 - uv) * b0;
			double multb10 = uv;

			double cnstb11 = (uv) * b2;
			double multb11 = (1 - uv);

			double cnstb20 = (1 - uv) * cnstb10 + (uv) * cnstb11;
			double multb20 = (1 - uv) * multb10 + (uv) * multb11;

			double u = (1 - uv) * intervalStart + uv * intervalEnd;
			double eval = wave.eval(u);

			ptTotal += (eval - cnstb20) / multb20;
		}

		return (ptTotal / len);
	}

}
