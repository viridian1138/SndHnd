




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







package abzon;

import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;

import aazon.vect.AazonImmutableVect;

/**
 * An immutable AbzonPathIteratorFactory for a cubic Bezier curve.
 * 
 * @author tgreen
 *
 */
public class AbzonImmutableCubicCurveFactory implements
		AbzonImmutablePathIteratorFactory {
	
	/**
	 * Cubic curve used to generate the iterator.
	 */
	protected CubicCurve2D.Double crv = null;
	
	/**
	 * Vector for the b0 Bezier point.
	 */
	protected AazonImmutableVect a0;
	
	/**
	 * Vector for the b1 Bezier point.
	 */
	protected AazonImmutableVect a1;
	
	/**
	 * Vector for the b2 Bezier point.
	 */
	protected AazonImmutableVect a2;
	
	/**
	 * Vector for the b3 Bezier point.
	 */
	protected AazonImmutableVect a3;
	
	/**
	 * Constructs the factory.
	 * @param _a0 Vector for the b0 Bezier point.
	 * @param _a1 Vector for the b1 Bezier point.
	 * @param _a2 Vector for the b2 Bezier point.
	 * @param _a3 Vector for the b3 Bezier point.
	 */
	public AbzonImmutableCubicCurveFactory( AazonImmutableVect _a0 , AazonImmutableVect _a1 , AazonImmutableVect _a2 , AazonImmutableVect _a3 )
	{
		a0 = _a0;
		a1 = _a1;
		a2 = _a2;
		a3 = _a3;
	}

	/**
	 * Returns a path iterator for the factory.
	 * @param at The affine transform for the iterator.
	 * @param flatness The desired flatness for the iterator.
	 * @return The path iterator.
	 */
	public PathIterator iterator(AffineTransform at, double flatness) {
		if( crv == null )
		{
			crv = new CubicCurve2D.Double( a0.getX() , a0.getY() , a1.getX() , a1.getY() , a2.getX() , a2.getY() , a3.getX() , a3.getY() );
			a0 = null;
			a1 = null;
			a2 = null;
			a3 = null;
		}
		
		return( crv.getPathIterator(at,flatness) );
	}

	
}

