




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







package aazon.dbl;

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;


/**
 * Aazon immutable double representing the end-point slope of two input immutable vectors.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableEndSlopeDbl extends AazonImmutableDbl {
	
	/**
	 * First endpoint from which to take the slope.
	 */
	protected AazonImmutableVect a;
	
	/**
	 * Second endpoint from which to take the slope.
	 */
	protected AazonImmutableVect b;
	
	/**
	 * Private constructor.
	 * @param _a First endpoint from which to take the slope.
	 * @param _b Second endpoint from which to take the slope.
	 */
	private AazonImmutableEndSlopeDbl( AazonImmutableVect _a , AazonImmutableVect _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Constructs an Aazon immutable double representing the end-point slope of two input immutable vectors.
	 * @param _a First endpoint from which to take the slope.
	 * @param _b Second endpoint from which to take the slope.
	 * @return The constructed double.
	 */
	public static AazonImmutableDbl construct( AazonImmutableVect _a , AazonImmutableVect _b )
	{
		if( ( _a instanceof AazonBaseImmutableVect ) && ( _b instanceof AazonBaseImmutableVect ) )
		{
			final double delX = Math.max( _b.getX() - _a.getX() , 1E-10 );
			return( new AazonBaseImmutableDbl( ( _b.getY() - _a.getY() ) / delX ) );
		}
		return( new AazonImmutableEndSlopeDbl( _a , _b ) );
	}

	@Override
	public double getX() {
		final double delX = Math.max( b.getX() - a.getX() , 1E-10 );
		return( ( b.getY() - a.getY() ) / delX );
	}

	
}

