




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








package core;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;



/**
 * Evaluation coefficient for emulating a simple stereo effect by applying a time offset to
 * a particular channel to model the time required for sound to reach a particular ear (as
 * opposed to the other ear).
 * 
 * @author tgreen
 *
 */
public class StereoEvalCoefficient extends NonClampedCoefficientMultiCore {
	
	/**
	 * The input coefficient value to be time-offset.
	 */
	protected NonClampedCoefficientMultiCore coeff;
	
	/**
	 * The time offset in seconds.
	 */
	protected double timeOffset = 0.0;

	/**
	 * Constructor.
	 * @param _coeff The input coefficient value to be time-offset.
	 * @param _timeOffset The time offset in seconds.
	 */
	public StereoEvalCoefficient( NonClampedCoefficientMultiCore _coeff , double _timeOffset ) {
		coeff = _coeff;
		timeOffset = _timeOffset;
	}

	
	@Override
	public double eval(final double non_phase_distorted_param, final int core) {
		return( coeff.eval( non_phase_distorted_param + timeOffset , core ) );
	}

	
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		out.writeObject(myv);
	}

	
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}


}

