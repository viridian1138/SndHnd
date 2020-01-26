




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


import gredit.GNode;
import gredit.GNonClampedCoefficient;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.InterpolationPoint;
import core.NonClampedCoefficient;



/**
 * Non-Clamped coefficient for a piecewise cubic monotone Bezier curve with flat end-slopes.
 * 
 * @author thorngreen
 *
 */
public class BezierCubicNonClampedCoefficientFlat extends NonClampedCoefficient implements Externalizable {
	
	/**
	 * Piecewise cubic monotone Bezier curve with flat end-slopes used to evaluate the coefficient.
	 */
	PiecewiseCubicMonotoneBezierFlat bez;

	/**
	 * Constructs the coefficient.
	 * @param _bez Piecewise cubic monotone Bezier curve with flat end-slopes.
	 */
	public BezierCubicNonClampedCoefficientFlat( PiecewiseCubicMonotoneBezierFlat _bez ) {
		super();
		bez = _bez;
	}
	
	/**
	 * Gets the piecewise cubic monotone Bezier curve with flat end-slopes used to evaluate the coefficient.
	 * @return Piecewise cubic monotone Bezier curve with flat end-slopes used to evaluate the coefficient.
	 */
	public PiecewiseCubicMonotoneBezierFlat getBez()
	{
		return( bez );
	}
	
	/**
	 * Constructor for persistence purposes only.
	 *
	 */
	public BezierCubicNonClampedCoefficientFlat()
	{
	}

	@Override
	public double eval(double non_phase_distorted_param) {
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
		
		return( bez.eval( u0 ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		PiecewiseCubicMonotoneBezierFlat bz = bez.genCloneWave();
		if( bz == bez )
		{
			return( this );
		}
		else
		{
			return( new BezierCubicNonClampedCoefficientFlat( bz ) );
		}
	}
	
	@Override
	public GNonClampedCoefficient genCoeff( HashMap<Object,GNode> s )
	{
		if( s.get( this ) != null )
		{
			return( (GNonClampedCoefficient)( s.get( this ) ) );
		}
		
		GBezierCubicNonClampedCoefficientFlat wv = new GBezierCubicNonClampedCoefficientFlat();
		s.put(this, wv);
		
		GPiecewiseCubicMonotoneBezierFlat w = bez.genBez(s);
		
		wv.load(w);
		
		return( wv );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (PiecewiseCubicMonotoneBezierFlat)( myv.getPropertyEx("Bez") );
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

