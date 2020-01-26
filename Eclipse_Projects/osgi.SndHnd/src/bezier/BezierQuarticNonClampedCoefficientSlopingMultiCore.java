




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
 * Non-clamped coefficient for a piecewise quartic Bezier curve with a linear slope extending off from each end of the piecewise domain.
 * 
 * @author thorngreen
 *
 */
public class BezierQuarticNonClampedCoefficientSlopingMultiCore extends NonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * Piecewise quartic Bezier curve with a linear slope extending off from each end of the piecewise domain.
	 */
	PiecewiseQuarticBezierSlopingMultiCore bez;

	/**
	 * Constructs the coefficient
	 * @param _bez Piecewise quartic Bezier curve with a linear slope extending off from each end of the piecewise domain.
	 */
	public BezierQuarticNonClampedCoefficientSlopingMultiCore( PiecewiseQuarticBezierSlopingMultiCore _bez ) {
		super();
		bez = _bez;
	}
	
	/**
	 * Gets the piecewise quartic Bezier curve with a linear slope extending off from each end of the piecewise domain.
	 * @return Piecewise quartic Bezier curve with a linear slope extending off from each end of the piecewise domain.
	 */
	public PiecewiseQuarticBezierSlopingMultiCore getBez()
	{
		return( bez );
	}
	
	/**
	 * Constructor for persistence purposes only.
	 *
	 */
	public BezierQuarticNonClampedCoefficientSlopingMultiCore()
	{
	}

	@Override
	public double eval(double non_phase_distorted_param, final int core) {
		double u0 = non_phase_distorted_param;
		
		InterpolationPoint first = bez.getFirstPoint();
		InterpolationPoint last = bez.getLastPoint();
		
		if( u0 < first.getParam() )
		{
			return( first.getValue() + ( u0 - first.getParam() ) * ( bez.getStrtSlope() ) );
		}
		
		if( u0 > last.getParam() )
		{
			return( last.getValue() + ( u0 - last.getParam() ) * ( bez.getEndSlope() ) );
		}
		
		return( bez.eval( u0 , core ) );
	}
	
	/**
	 * Evaluates the first derivative of the curve with respect to the parameter.
	 * @param non_phase_distorted_param The parameter value at which to evaluate the derivative.
	 * @param core The number of the core thread.
	 * @return The value of the derivative.
	 */
	public double evalSlope(final double non_phase_distorted_param, final int core) {
		double u0 = non_phase_distorted_param;
		
		InterpolationPoint first = bez.getFirstPoint();
		InterpolationPoint last = bez.getLastPoint();
		
		if( u0 < first.getParam() )
		{
			return( bez.getStrtSlope() );
		}
		
		if( u0 > last.getParam() )
		{
			return( bez.getEndSlope() );
		}
		
		return( bez.evalSlope( u0 , core ) );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (PiecewiseQuarticBezierSlopingMultiCore)( myv.getPropertyEx("Bez") );
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

