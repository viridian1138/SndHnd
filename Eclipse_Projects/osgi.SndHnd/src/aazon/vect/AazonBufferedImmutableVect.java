




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


/**
 * Aazon class for an immutable vector returning a buffered version of the current value captured from a mutable vector.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedImmutableVect extends AazonImmutableVect {

	/**
	 * The mutable vector.
	 */
	protected AazonVect a;
	
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
	 * @param _a The mutable vector.
	 */
	private AazonBufferedImmutableVect( AazonVect _a )
	{
		a = _a;
	}

	@Override
	public double getX() {
		if( !enabled )
		{
			x = a.getX();
			y = a.getY();
			a = null;
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
			a = null;
			enabled = true;
		}
		
		return( y );
	}
	
	/**
	 * Constructs an immutable vector from the input.
	 * @param _a The input vector.
	 * @return An immutable vector instance.
	 */
	public static AazonImmutableVect construct( AazonVect _a )
	{
		if( _a instanceof AazonBaseImmutableVect )
		{
			return( (AazonBaseImmutableVect) _a );
		}
		
		if( _a instanceof AazonBufferedImmutableVect )
		{
			return( (AazonBufferedImmutableVect) _a );
		}
		
		return( new AazonBufferedImmutableVect( _a ) );
	}

	
}

