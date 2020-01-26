




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
import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;

import core.NonClampedCoefficient;


/**
 * Node representing a non-Clamped coefficient for a piecewise cubic monotone Bezier curve with flat end-slopes.
 * 
 * @author thorngreen
 *
 */
public class GBezierCubicNonClampedCoefficientFlat extends
		GNonClampedCoefficient {
	
	/**
	 * Piecewise cubic Bezier curve with a zero slope extending off from each end of the piecewise domain.
	 */
	GPiecewiseCubicMonotoneBezierFlat bez;

	@Override
	public NonClampedCoefficient genCoeff(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (BezierCubicNonClampedCoefficientFlat)( s.get( this ) ) );
		}
		
		s.put(this, new Integer(5));
		
		PiecewiseCubicMonotoneBezierFlat w = bez.genBez(s);
		
		BezierCubicNonClampedCoefficientFlat wv = new BezierCubicNonClampedCoefficientFlat( w );
		s.put(this, wv);
		
		return( wv );
	}

	@Override
	public String getName() {
		return( "BezierCubicNonClampedCoefficientFlat" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GPiecewiseCubicMonotoneBezierFlat );
	}

	@Override
	public void performAssign(GNode in) {
		bez = (GPiecewiseCubicMonotoneBezierFlat) in;
	}

	@Override
	public void removeChld() {
		bez = null;
	}

	public Object getChldNodes() {
		return( bez );
	}
	
	/**
	 * Loads new values into the node.
	 * @param in Piecewise cubic Bezier curve with a zero slope extending off from each end of the piecewise domain.
	 */
	public void load( GPiecewiseCubicMonotoneBezierFlat in )
	{
		bez = in;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( bez != null ) myv.setProperty("Bez", bez);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (GPiecewiseCubicMonotoneBezierFlat)( myv.getProperty("Bez") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

