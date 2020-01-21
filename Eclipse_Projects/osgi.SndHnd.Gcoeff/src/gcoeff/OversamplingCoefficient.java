





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
 * A sampler for performing straightforward oversampling on an input.
 * 
 * Note: Variations of MicroPhaseAdjustment often produce results that are superior to oversampling.
 * 
 * @author tgreen
 *
 */
public class OversamplingCoefficient extends NonClampedCoefficientMultiCore {
	
	/**
	 * The input wave to be oversampled.
	 */
	protected NonClampedCoefficientMultiCore coeff;
	
	/**
	 * The number of samples to use in the oversampling (i.e. whether it's 2X oversampling, 3X oversampling, 4X oversampling, etc.).
	 */
	protected int oversampling;

	/**
	 * Constructs the sampler.
	 * @param _coeff  The input wave to be oversampled.
	 * @param _oversampling  The number of samples to use in the oversampling (i.e. whether it's 2X oversampling, 3X oversampling, 4X oversampling, etc.).
	 */
	public OversamplingCoefficient( NonClampedCoefficientMultiCore _coeff , int _oversampling ) {
		coeff = _coeff;
		oversampling = _oversampling;
	}

	@Override
	public double eval(double elapsed_time_seconds , final int core ) {
		int count;
		double ret = 0.0;
		final double samplingRate = TestPlayer2.getSamplingRate();
		double b0 = elapsed_time_seconds - 0.5 / samplingRate;
		double b1 = elapsed_time_seconds + 0.5 / samplingRate;
		for( count = 0 ; count < oversampling ; count++ )
		{
			double u = ( count + 0.5 ) / ( oversampling );
			double samp_time_seconds = (1-u) * b0 + u * b1;
			ret += coeff.eval( samp_time_seconds , core );
		}
		return( ret / oversampling );
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coeff = (NonClampedCoefficientMultiCore)( myv.getProperty("coeff") );
			oversampling = myv.getInt("oversampling");

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
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("coeff", coeff);
		myv.setInt("oversampling", oversampling);

		out.writeObject(myv);
	}

}

