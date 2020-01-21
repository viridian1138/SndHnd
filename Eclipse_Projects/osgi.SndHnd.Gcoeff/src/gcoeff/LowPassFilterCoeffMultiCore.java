





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







package gcoeff;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficientMultiCore;

/**
 * A simple (not necessarily good) low-pass filter.
 * 
 * The evaluation interval is basically the inverse of the cutoff frequency for the filter.
 * 
 * @author tgreen
 *
 */
public class LowPassFilterCoeffMultiCore extends NonClampedCoefficientMultiCore {

	/**
	 * The wave to be filtered.
	 */
	protected NonClampedCoefficientMultiCore wave;
	
	/**
	 * The evaluation interval half-length.
	 */
	protected double intervalHalfLength;
	
	/**
	 * The number of sampling points to use in the filtering.
	 */
	protected int sampleLen;
	
	/**
	 * Constructs the filter.
	 * @param _wave The wave to be filtered.
	 * @param _intervalHalfLength The evaluation interval half-length.
	 * @param _sampleLen The number of sampling points to use in the filtering.
	 */
	public LowPassFilterCoeffMultiCore( NonClampedCoefficientMultiCore _wave , double _intervalHalfLength , int _sampleLen )
	{
		super();
		wave = _wave;
		intervalHalfLength = _intervalHalfLength;
		sampleLen = _sampleLen;
	}

	/**
	 * Default constructor.
	 */
	public LowPassFilterCoeffMultiCore() {
		super();
	}
	
	/**
	 * Gets the number of sampling points to use in the filtering.
	 * @return The number of sampling points to use in the filtering.
	 */
	public int getSampleLen()
	{
		return( sampleLen );
	}

	@Override
	public double eval(double param, final int core) {
		double intervalStart = param - intervalHalfLength;
		double intervalEnd = param + intervalHalfLength;
		
		double b0 = calcApproxB0( sampleLen , intervalStart , intervalEnd , core);
		double b2 = calcApproxB2( sampleLen , intervalStart , intervalEnd , core);
		double b1 = calcApproxB1( sampleLen + 2 , b0 , b2 , intervalStart , intervalEnd , core);
		
		double u = 0.5;
		
		double b10 = ( 1 - u ) * b0 + u * b1;
		double b11 = ( 1 - u ) * b1 + u * b2;
		
		double b20 = ( 1 - u ) * b10 + u * b11;
		
		return( b20 );
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("Wave", wave);
		myv.setDouble("IntervalHalfLength", intervalHalfLength);
		myv.setInt("SampleLen", sampleLen);

		out.writeObject(myv);
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (NonClampedCoefficientMultiCore)( myv.getProperty("Wave") );
			intervalHalfLength = myv.getDouble("IntervalHalfLength");
			sampleLen = myv.getInt("SampleLen");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	
	/**
	 * Calculates an approximate Bezier point B0 from sampling.
	 * @param len The number of sample points to collect.
	 * @param intervalStart The start of the evaluation interval.
	 * @param intervalEnd The end of the evaluation interval.
	 * @param core The number of the core thread.
	 * @return The approximate B0 point.
	 */
	public double calcApproxB0( int len , double intervalStart , double intervalEnd , final int core)
	{
		double tot = 0.0;
		double totParam = 0.0;
		int count;
		for( count = 0 ; count < len ; count++ )
		{
			double uv = ( (double) count ) / ( len - 1 );
			double u = ( 1 - uv ) * ( -1.0 ) + ( uv ) * ( 1.0 );
			double param = ( 1 - u ) * intervalStart + u * intervalEnd;
			double multp = 1.0 - Math.abs( u );
			tot += wave.eval( param , core ) * multp;
			totParam += multp;
		}
		return( tot / totParam );
	}
	
	/**
	 * Calculates an approximate Bezier point B2 from sampling.
	 * @param len The number of sample points to collect.
	 * @param intervalStart The start of the evaluation interval.
	 * @param intervalEnd The end of the evaluation interval.
	 * @param core The number of the core thread.
	 * @return The approximate B2 point.
	 */
	public double calcApproxB2( int len , double intervalStart , double intervalEnd , final int core )
	{
		double tot = 0.0;
		double totParam = 0.0;
		int count;
		for( count = 0 ; count < len ; count++ )
		{
			double uv = ( (double) count ) / ( len - 1 );
			double u = ( 1 - uv ) * ( 0.0 ) + ( uv ) * ( 2.0 );
			double param = ( 1 - u ) * intervalStart + u * intervalEnd;
			double multp = 1.0 - Math.abs( 1.0 - u );
			tot += wave.eval( param , core ) * multp;
			totParam += multp;
		}
		return( tot / totParam );
	}

	/**
	 * Calculates an approximate Bezier point B1 from sampling.
	 * @param len The number of sample points to collect.
	 * @param b0 The calculated approximation to the B0 Bezier point.
	 * @param b2 The calculated approximation to the B2 Bezier point.
	 * @param intervalStart The start of the evaluation interval.
	 * @param intervalEnd The end of the evaluation interval.
	 * @param core The number of the core thread.
	 * @return The approximate B1 point.
	 */
	public double calcApproxB1(int len, double b0, double b2, double intervalStart, double intervalEnd, final int core ) {
		int count = 0;
		double ptTotal = 0.0;

		for (count = 1; count < ( len - 1 ); count++) {
			double uv = ( (double) count ) / ( len - 1 );
			
			double cnstb10 = (1 - uv) * b0;
			double multb10 = uv;

			double cnstb11 = (uv) * b2;
			double multb11 = (1 - uv);

			double cnstb20 = (1 - uv) * cnstb10 + (uv) * cnstb11;
			double multb20 = (1 - uv) * multb10 + (uv) * multb11;
			
			double u = (1-uv) * intervalStart + uv * intervalEnd;
			double eval = wave.eval( u , core );

			ptTotal += ( eval - cnstb20 ) / multb20;
		}

		return (ptTotal / len);
	}

	
} 

