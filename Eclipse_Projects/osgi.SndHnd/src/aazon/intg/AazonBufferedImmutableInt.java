




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







package aazon.intg;


/**
 * Aazon class for an immutable integer returning a buffered version of the current value captured from a mutable integer.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedImmutableInt extends AazonImmutableInt {

	/**
	 * The mutable integer.
	 */
	protected AazonInt a;
	
	/**
	 * The buffered version of the current value captured from the mutable integer.
	 */
	protected int x;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The mutable integer.
	 */
	private AazonBufferedImmutableInt( AazonInt _a )
	{
		a = _a;
	}

	@Override
	public int getX() {
		if( !enabled )
		{
			x = a.getX();
			a = null;
			enabled = true;
		}
		
		return( x );
	}
	
	/**
	 * Constructs an immutable integer from the input.
	 * @param _a The input integer.
	 * @return An immutable integer instance.
	 */
	public static AazonImmutableInt construct( AazonInt _a )
	{
		if( _a instanceof AazonBaseImmutableInt )
		{
			return( (AazonBaseImmutableInt) _a );
		}
		
		if( _a instanceof AazonBufferedImmutableInt )
		{
			return( (AazonBufferedImmutableInt) _a );
		}
		
		return( new AazonBufferedImmutableInt( _a ) );
	}

}

