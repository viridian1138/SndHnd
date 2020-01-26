




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


import gredit.GNonClampedCoefficient;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.InterpolationPoint;
import core.NonClampedCoefficient;


import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * Non-Clamped coefficient for a piecewise cubic Bezier curve with a linear slope extending off from each end of the piecewise domain.
 * 
 * @author thorngreen
 *
 */
public class BezierCubicNonClampedCoefficientSloping extends NonClampedCoefficient implements Externalizable {
	
	/**
	 * Piecewise cubic Bezier curve with a linear slope extending off from each end of the piecewise domain used to evaluate the coefficient.
	 */
	PiecewiseCubicMonotoneBezierSloping bez;

	/**
	 * Constructs the coefficient.
	 * @param _bez Piecewise cubic Bezier curve with a linear slope extending off from each end of the piecewise domain.
	 */
	public BezierCubicNonClampedCoefficientSloping( PiecewiseCubicMonotoneBezierSloping _bez ) {
		super();
		bez = _bez;
	}
	
	/**
	 * Gets the piecewise cubic Bezier curve with a linear slope extending off from each end of the piecewise domain used to evaluate the coefficient.
	 * @return Piecewise cubic Bezier curve with a linear slope extending off from each end of the piecewise domain used to evaluate the coefficient.
	 */
	public PiecewiseCubicMonotoneBezierSloping getBez()
	{
		return( bez );
	}
	
	/**
	 * Constructor for persistence purposes only.
	 *
	 */
	public BezierCubicNonClampedCoefficientSloping()
	{
	}
	
	/**
	 * Sets the end-slopes of the curve.
	 * @param s1 The end-slope for the left side of the curve.
	 * @param s2 The end slope for the right side of the curve.
	 */
	public void setSlopes( double s1 , double s2 )
	{
		bez.setSlopes( s1 , s2 );
	}

	@Override
	public double eval(double non_phase_distorted_param) {
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
		
		return( bez.eval( u0 ) );
	}
	
	/**
	 * Evaluates the derivative of the curve with respect to the parameter.
	 * @param non_phase_distorted_param The parameter value at which to evaluate the derivative.
	 * @return The value of the derivative.
	 */
	public double evalDerivative(double non_phase_distorted_param) {
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
		
		return( bez.evalDerivative( u0 ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		PiecewiseCubicMonotoneBezierSloping bz = (PiecewiseCubicMonotoneBezierSloping)( bez.genCloneWave() );
		if( bz == bez )
		{
			return( this );
		}
		else
		{
			return( new BezierCubicNonClampedCoefficientSloping( bz ) );
		}
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (PiecewiseCubicMonotoneBezierSloping)( myv.getPropertyEx("Bez") );
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

	@Override
	public GNonClampedCoefficient genCoeff(HashMap s) {
		throw( new RuntimeException( "Not Supported For BezierCubicNonClampedCoefficientSloping" ) );
	}

}

