




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

import aazon.AazonListener;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;


/**
 * Aazon double representing the end-point slope of two input vectors.
 * 
 * @author tgreen
 *
 */
public class AazonMutableEndSlopeDbl extends AazonMutableDbl implements AazonListener {
	
	/**
	 * First endpoint from which to take the slope.
	 */
	protected AazonVect a;
	
	/**
	 * Second endpoint from which to take the slope.
	 */
	protected AazonVect b;
	
	/**
	 * Private constructor.
	 * @param _a First endpoint from which to take the slope.
	 * @param _b Second endpoint from which to take the slope.
	 */
	private AazonMutableEndSlopeDbl( AazonVect _a , AazonVect _b )
	{
		a = _a;
		b = _b;
		
		if( a instanceof AazonMutableVect )
		{
			( (AazonMutableVect) a ).add( this );
		}
		
		if( b instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b ).add( this );
		}
	}
	
	/**
	 * Constructs an Aazon double representing the end-point slope of two input vectors.
	 * @param _a First endpoint from which to take the slope.
	 * @param _b Second endpoint from which to take the slope.
	 * @return The constructed double.
	 */
	public static AazonDbl construct( AazonVect _a , AazonVect _b )
	{
		if( ( _a instanceof AazonImmutableVect ) && ( _b instanceof AazonImmutableVect ) )
		{
			return( AazonImmutableEndSlopeDbl.construct( (AazonImmutableVect) _a , (AazonImmutableVect) _b ) );
		}
		return( new AazonMutableEndSlopeDbl( _a , _b ) );
	}

	@Override
	public double getX() {
		final double delX = Math.max( b.getX() - a.getX() , 1E-10 );
		return( ( b.getY() - a.getY() ) / delX );
	}
	
	/**
	 * Handles a change to one of the arguments by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

