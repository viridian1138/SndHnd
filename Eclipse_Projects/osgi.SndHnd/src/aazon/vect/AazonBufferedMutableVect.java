




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
 * An Aazon mutable vector that buffers the state of an input mutable vector that requires more computational work to evaluate.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedMutableVect extends AazonMutableVect implements AazonListener {
	
	/**
	 * The input mutable vector.
	 */
	protected AazonMutableVect a;
	
	/**
	 * The X-coordinate value of the vector.
	 */
	protected double x;
	
	/**
	 * The Y-coordinate value of the vector.
	 */
	protected double y;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The input mutable vector.
	 */
	private AazonBufferedMutableVect( AazonMutableVect _a )
	{
		a = _a;
		a.add( this );
	}

	@Override
	public double getX() {
		if( !enabled )
		{
			x = a.getX();
			y = a.getY();
			enabled = true;
		}
		
		return( x );
	}

	@Override
	public double getY() {
		if( !enabled )
		{
			x = a.getX();
			y = a.getY();
			enabled = true;
		}
		
		return( y );
	}
	
	/**
	 * Handles a change to the input vector by invalidating the buffer, and then firing events.
	 */
	public void handleListen()
	{
		enabled = false;
		fire();
	}

	/**
	 * Returns an Aazon vector that buffers the state of an input vector that requires more computational work to evaluate.
	 * @param _a The input vector.
	 * @return The Aazon vector that buffers the state of an input vector that requires more computational work to evaluate.
	 */
	public static AazonVect construct( AazonVect _a )
	{
		if( _a instanceof AazonImmutableVect )
		{
			return( AazonBufferedImmutableVect.construct( _a ) );
		}
		
		return( new AazonBufferedMutableVect( (AazonMutableVect) _a ) );
	}

	
}

