




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

import aazon.AazonListener;
import aazon.dbl.AazonDbl;
import aazon.dbl.AazonImmutableDbl;
import aazon.dbl.AazonMutableDbl;


/**
 * Aazon mutable vector representing the cubic Bezier point after the previous interpolation point given the previous interpolation point, the next interpolation point, and the slope at the previous interpolation point.
 * 
 * @author tgreen
 *
 */
public class AazonMutableSlopeAddVect extends AazonMutableVect implements AazonListener {
	
	/**
	 * The previous interpolation point.
	 */
	protected AazonVect prev;
	
	/**
	 * The slope at the previous interpolation point.
	 */
	protected AazonDbl prevSlope;
	
	/**
	 * The next interpolation point.
	 */
	protected AazonVect nxt;
	
	/**
	 * Private constructor.
	 * @param _prev The previous interpolation point.
	 * @param _prevSlope The slope at the previous interpolation point.
	 * @param _nxt The next interpolation point.
	 */
	private AazonMutableSlopeAddVect( AazonVect _prev , AazonDbl _prevSlope , AazonVect _nxt )
	{
		prev = _prev;
		prevSlope = _prevSlope;
		nxt = _nxt;
		
		if( prev instanceof AazonMutableVect )
		{
			( (AazonMutableVect) prev ).add( this );
		}
		
		if( prevSlope instanceof AazonMutableDbl )
		{
			( (AazonMutableDbl) prevSlope ).add( this );
		}
		
		if( nxt instanceof AazonMutableVect )
		{
			( (AazonMutableVect) nxt ).add( this );
		}
	}
	
	/**
	 * Constructs an Aazon vector representing the cubic Bezier point after the previous interpolation point given the previous interpolation point, the next interpolation point, and the slope at the previous interpolation point.
	 * @param _prev The previous interpolation point.
	 * @param _prevSlope The slope at the previous interpolation point.
	 * @param _nxt The next interpolation point.
	 * @return The constructed vector.
	 */
	public static AazonVect construct( AazonVect _prev , AazonDbl _prevSlope , AazonVect _nxt )
	{
		if( ( _prev instanceof AazonImmutableVect ) && ( _prevSlope instanceof AazonImmutableDbl ) && ( _nxt instanceof AazonImmutableVect ) )
		{
			return( AazonImmutableSlopeAddVect.construct( (AazonImmutableVect) _prev , (AazonImmutableDbl) _prevSlope , (AazonImmutableVect) _nxt ) );
		}
		return( new AazonMutableSlopeAddVect( _prev , _prevSlope , _nxt ) );
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
	
	/**
	 * Handles a change to one of the arguments by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

