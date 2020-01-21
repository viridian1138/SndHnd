





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
import core.TestPlayer2;


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
public class MicroPhaseAdjustmentCv extends LowPassFilterCoeffMultiCore {
	
	/**
	 * Temporary value containing the calculated minimum or maximum value where the slope crosses zero.
	 */
	protected double tmpCalcValue;
	
	// protected double tmpAvgValue;
	
	/**
	 * Finds the location where the slope of the wave crosses zero, indicating a maximum or minimum.
	 * @param param The center of the interval to evaluate.
	 * @param core The number of the code thread.
	 * @return Best estimated parameter value where the crossing happens.  Also puts the estimated min/max value in tmpCalcValue.
	 */
	protected double findCrossing(double param, final int core) {
		double intervalStart = param - intervalHalfLength;
		double intervalEnd = param + intervalHalfLength;
		
		double b0 = calcApproxB0( sampleLen , intervalStart , intervalEnd , core );
		double b2 = calcApproxB2( sampleLen , intervalStart , intervalEnd , core );
		double b1 = calcApproxB1( sampleLen + 2 , b0 , b2 , intervalStart , intervalEnd , core );
		
		double db0 = b1 - b0;
		double db1 = b2 - b1;
		
		double u = -db0 / ( db1 - db0 );
		
		if( Double.isNaN( u ) )
		{
			u = - 1E+10;
		}
		
		double b10 = (1-u) * b0 + u * b1;
		double b11 = (1-u) * b1 + u * b2;
		
		double b20 = (1-u) * b10 + u * b11;
		
		tmpCalcValue = b20;
		
		// tmpAvgValue = ( b0 + b2 ) / 2.0;
		
		return( u );
	}
	

	/**
	 * Constructs the phase adjuster.
	 * @param _wave The input wave to be sampled.
	 * @param sampleLen_ The number of samples to be used to determine the phase.
	 */
	public MicroPhaseAdjustmentCv( NonClampedCoefficientMultiCore _wave , int _sampleLen ) {
		super(  _wave , ( 1.0 / TestPlayer2.getSamplingRate() ) / 2.0  , _sampleLen );
	}

	
	@Override
	public double eval(double param , final int core ) {
		
		double u0 = findCrossing( param , core );
		
		if( ( u0 >= 0.0 ) && ( u0 <= 1.0 ) )
		{
			return( tmpCalcValue );
		}
		
		// double tmp = tmpAvgValue;
		
		u0 = findCrossing( param - intervalHalfLength , core );
		
		if( ( u0 >= 0.5 ) && ( u0 <= 1.0 ) )
		{
			return( tmpCalcValue );
		}
		
		u0 = findCrossing( param + intervalHalfLength , core );
		
		if( ( u0 >= 0.0 ) && ( u0 <= 0.5 ) )
		{
			return( tmpCalcValue );
		}
		
		return( wave.eval( param , core ) );
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

		out.writeObject(myv);
	}

}
