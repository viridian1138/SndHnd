




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


/**
 * Aazon mutable vector using one input vector as the affine scaling of another vector.
 * 
 * @author tgreen
 *
 */
public class AazonMutableScalingVect extends AazonMutableVect implements AazonListener {
	
	/**
	 * First argument of the vector scaling.
	 */
	protected AazonVect a;
	
	/**
	 * Second argument of the vector scaling.
	 */
	protected AazonVect b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the vector scaling.
	 * @param _b Second argument of the vector scaling.
	 */
	private AazonMutableScalingVect( AazonVect _a , AazonVect _b )
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
	 * Constructs an Aazon vector using one input vector as the affine scaling of another vector.
	 * @param _a First argument of the vector scaling.
	 * @param _b Second argument of the vector scaling.
	 * @return The constructed vector.
	 */
	public static AazonVect construct( AazonVect _a , AazonVect _b )
	{
		if( ( _a instanceof AazonImmutableVect ) && ( _b instanceof AazonImmutableVect ) )
		{
			return( AazonImmutableScalingVect.construct( (AazonImmutableVect) _a , (AazonImmutableVect) _b ) );
		}
		return( new AazonMutableScalingVect( _a , _b ) );
	}

	@Override
	public double getX() {
		return( a.getX() * b.getX() );
	}

	@Override
	public double getY() {
		return( a.getY() * b.getY() );
	}
	
	/**
	 * Handles a change to one of the arguments by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

