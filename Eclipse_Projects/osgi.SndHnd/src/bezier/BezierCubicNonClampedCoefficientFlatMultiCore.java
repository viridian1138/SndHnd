




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







package bezier;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import core.InterpolationPoint;
import core.NonClampedCoefficientMultiCore;

import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * Non-Clamped coefficient for a piecewise cubic monotone Bezier curve with flat end-slopes.  Supports multi-core evaluation.
 * 
 * @author thorngreen
 *
 */
public class BezierCubicNonClampedCoefficientFlatMultiCore extends NonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * Piecewise cubic monotone Bezier curve with flat end-slopes used to evaluate the coefficient.
	 */
	PiecewiseCubicMonotoneBezierFlatMultiCore bez;

	/**
	 * Constructs the coefficient.
	 * @param _bez Piecewise cubic monotone Bezier curve with flat end-slopes.
	 */
	public BezierCubicNonClampedCoefficientFlatMultiCore( PiecewiseCubicMonotoneBezierFlatMultiCore _bez ) {
		super();
		bez = _bez;
	}
	
	/**
	 * Gets the piecewise cubic monotone Bezier curve with flat end-slopes used to evaluate the coefficient.
	 * @return Piecewise cubic monotone Bezier curve with flat end-slopes used to evaluate the coefficient.
	 */
	public PiecewiseCubicMonotoneBezierFlatMultiCore getBez()
	{
		return( bez );
	}
	
	/**
	 * Constructor for persistence purposes only.
	 *
	 */
	public BezierCubicNonClampedCoefficientFlatMultiCore()
	{
	}

	@Override
	public double eval(final double non_phase_distorted_param, final int core) {
		double u0 = non_phase_distorted_param;
		
		InterpolationPoint first = bez.getFirstPoint();
		InterpolationPoint last = bez.getLastPoint();
		
		if( u0 < first.getParam() )
		{
			return( first.getValue() );
		}
		
		if( u0 > last.getParam() )
		{
			return( last.getValue() );
		}
		
		return( bez.eval( u0 , core ) );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (PiecewiseCubicMonotoneBezierFlatMultiCore)( myv.getPropertyEx("Bez") );
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("Bez", bez);

		out.writeObject(myv);
	}

}

