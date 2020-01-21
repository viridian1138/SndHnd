





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

import core.NonClampedCoefficientMultiCore;
import core.TestPlayer2;

import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * As suggested in Section 18 of "Computer Graphics Principles and Practice, Third Edition" by
 * John F. Hughes et. al., sampling at a Nyquist frequency is necessary but
 * not necessarily sufficient to reproduce a particular wave.  In fact, a wave
 * sampled at the Nyquist frequency can still be severely aliased.  This sampler class
 * makes small adjustments to the phase of the input wave to attempt to 
 * capture high-frequency harmonics up to the Nyquist limit.
 * 
 * MicroPhaseAdjustmentCv uses a uses a aggregated estimated value for
 * a critical point, whereas MicroPhaseAdjustment uses a single sample
 * collected at the estimated parameter of the critical point.
 * 
 * @author thorngreen
 *
 */
public class MicroPhaseAdjustment extends LowPassFilterCoeffMultiCore {
	
	/**
	 * The length of the interval over which the sampling happens.
	 */
	protected double intervalLength;
	
	/**
	 * Finds the location where the slope of the wave crosses zero, indicating a maximum or minimum.
	 * @param param The center of the interval to evaluate.
	 * @param core The number of the core thread.
	 * @return Best estimated parameter value where the crossing happens.
	 */
	protected double findCrossing(double param, final int core ) {
		double intervalStart = param - intervalHalfLength;
		double intervalEnd = param + intervalHalfLength;
		
		double b0 = calcApproxB0( sampleLen , intervalStart , intervalEnd , core );
		double b2 = calcApproxB2( sampleLen , intervalStart , intervalEnd , core );
		double b1 = calcApproxB1( sampleLen + 2 , b0 , b2 , intervalStart , intervalEnd, core  );
		
		double db0 = b1 - b0;
		double db1 = b2 - b1;
		
		double u = -db0 / ( db1 - db0 );
		
		if( Double.isNaN( u ) )
		{
			u = - 1E+10;
		}
		
		return( u );
	}
	
	/**
	 * Estimates the best offset phase at which to evaluate to capture the minimum or maximum on the sample.
	 * @param param The parameter at the center of the sample.
	 * @param core The number of the core thread.
	 * @return The parameter value of the offset phase.
	 */
	protected double getPhaseOffset( double param , final int core )
	{
		double ret = param;
		
		double u0 = findCrossing( param , core );
		
		if( ( u0 >= 0.0 ) && ( u0 <= 1.0 ) )
		{
			double intervalStart = param - intervalHalfLength;
			double intervalEnd = param + intervalHalfLength;
			ret = ( 1 - u0 ) * intervalStart + u0 * intervalEnd;
			return( ret );
		}
		
		u0 = findCrossing( param - intervalHalfLength , core );
		
		if( ( u0 >= 0.5 ) && ( u0 <= 1.0 ) )
		{
			double intervalStart = param - intervalLength;
			double intervalEnd = param;
			ret = ( 1 - u0 ) * intervalStart + u0 * intervalEnd;
			return( ret );
		}
		
		u0 = findCrossing( param + intervalHalfLength , core );
		
		if( ( u0 >= 0.0 ) && ( u0 <= 0.5 ) )
		{
			double intervalStart = param;
			double intervalEnd = param + intervalLength;
			ret = ( 1 - u0 ) * intervalStart + u0 * intervalEnd;
			return( ret );
		}
		
		return( ret );
	}
	
	/**
	 * Evaluates the sampling of the phase.
	 * @param param The parameter at which to sample.
	 * @param intervalCenter The parameter at the center of the sampling interval.
	 * @param core The number of the core thread.
	 * @return The sampled value.
	 */
	protected double eval( double param , double intervalCenter , final int core )
	{
		// System.out.println( interval - getPhaseOffset( param ) );
		
		if( param < intervalCenter )
		{
			double u0 = intervalCenter - intervalLength;
			double u1 = intervalCenter;
			
			double d0 = getPhaseOffset( u0 , core );
			double d1 = getPhaseOffset( u1 , core );
			
			double b0 = d0;
			double b1 = d0;
			double b2 = d1;
			double b3 = d1;
			
			double u = ( param - u0 ) / ( u1 - u0);
			
			double b10 = (1-u)*b0 + u*b1;
			double b11 = (1-u)*b1 + u*b2;
			double b12 = (1-u)*b2 + u*b3;
			
			double b20 = (1-u)*b10 + u*b11;
			double b21 = (1-u)*b11 + u*b12;
			
			double b30 = (1-u)*b20 + u*b21;
			
			return( wave.eval( b30 , core ) );
		}
		
		double u0 = intervalCenter;
		double u1 = intervalCenter + intervalLength;
		
		double d0 = getPhaseOffset( u0 , core );
		double d1 = getPhaseOffset( u1 , core );
		
		double b0 = d0;
		double b1 = d0;
		double b2 = d1;
		double b3 = d1;
		
		double u = ( param - u0 ) / ( u1 - u0);
		
		double b10 = (1-u)*b0 + u*b1;
		double b11 = (1-u)*b1 + u*b2;
		double b12 = (1-u)*b2 + u*b3;
		
		double b20 = (1-u)*b10 + u*b11;
		double b21 = (1-u)*b11 + u*b12;
		
		double b30 = (1-u)*b20 + u*b21;
		
		return( wave.eval( b30 , core ) );
	}

	/**
	 * Constructs the phase adjuster.
	 * @param _wave The input wave to be sampled.
	 * @param sampleLen_ The number of samples to be used to determine the phase.
	 */
	public MicroPhaseAdjustment( NonClampedCoefficientMultiCore _wave , int _sampleLen ) {
		super(  _wave , ( 1.0 / TestPlayer2.getSamplingRate() ) / 2.0  , _sampleLen );
		intervalLength = 1.0 / TestPlayer2.getSamplingRate();
	}

	@Override
	public double eval(double elapsedTimeSeconds , final int core ) {
		long sval = (long)( elapsedTimeSeconds / intervalLength );
		double intervalCenter = ( sval + 0.5 ) * intervalLength;
		return( eval( elapsedTimeSeconds , intervalCenter , core ) );
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		super.readExternal(in);
		
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			intervalLength = myv.getDouble("IntervalLength");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal( out );
		
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("IntervalLength", intervalLength);

		out.writeObject(myv);
	}

}
