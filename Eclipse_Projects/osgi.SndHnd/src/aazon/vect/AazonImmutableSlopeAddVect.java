




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







package aazon.vect;

import aazon.dbl.AazonBaseImmutableDbl;
import aazon.dbl.AazonImmutableDbl;


/**
 * Aazon immutable vector representing the cubic Bezier point after the previous interpolation point given the previous interpolation point, the next interpolation point, and the slope at the previous interpolation point.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableSlopeAddVect extends AazonImmutableVect {
	
	/**
	 * The previous interpolation point.
	 */
	protected AazonImmutableVect prev;
	
	/**
	 * The slope at the previous interpolation point.
	 */
	protected AazonImmutableDbl prevSlope;
	
	/**
	 * The next interpolation point.
	 */
	protected AazonImmutableVect nxt;
	
	/**
	 * Private constructor.
	 * @param _prev The previous interpolation point.
	 * @param _prevSlope The slope at the previous interpolation point.
	 * @param _nxt The next interpolation point.
	 */
	private AazonImmutableSlopeAddVect( AazonImmutableVect _prev , AazonImmutableDbl _prevSlope , AazonImmutableVect _nxt )
	{
		prev = _prev;
		prevSlope = _prevSlope;
		nxt = _nxt;
	}
	
	/**
	 * Constructs an Aazon immutable vector representing the cubic Bezier point after the previous interpolation point given the previous interpolation point, the next interpolation point, and the slope at the previous interpolation point.
	 * @param _prev The previous interpolation point.
	 * @param _prevSlope The slope at the previous interpolation point.
	 * @param _nxt The next interpolation point.
	 * @return The constructed vector.
	 */
	public static AazonImmutableVect construct( AazonImmutableVect _prev , AazonImmutableDbl _prevSlope , AazonImmutableVect _nxt )
	{
		if( ( _prev instanceof AazonBaseImmutableVect ) && ( _prevSlope instanceof AazonBaseImmutableDbl ) && ( _nxt instanceof AazonBaseImmutableVect ) )
		{
			final double px = _prev.getX();
			final double py = _prev.getY();
			final double nx = _nxt.getX();
			final double x = px + ( ( nx - px ) / 3.0 );
			final double delX = x - px;
			final double slope = _prevSlope.getX();
			final double y = py + ( delX * slope );
			return( new AazonBaseImmutableVect( x , y ) );
		}
		return( new AazonImmutableSlopeAddVect( _prev , _prevSlope , _nxt ) );
	}

	@Override
	public double getX() {
		final double px = prev.getX();
		final double nx = nxt.getX();
		return( px + ( ( nx - px ) / 3.0 ) );
	}

	@Override
	public double getY() {
		final double px = prev.getX();
		final double py = prev.getY();
		final double delX = getX() - px;
		final double slope = prevSlope.getX();
		return( py + ( delX * slope ) );
	}

	
}

