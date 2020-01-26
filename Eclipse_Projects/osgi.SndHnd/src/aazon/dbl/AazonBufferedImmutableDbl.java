




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


/**
 * Aazon class for an immutable double returning a buffered version of the current value captured from a mutable double.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedImmutableDbl extends AazonImmutableDbl {

	/**
	 * The mutable double.
	 */
	protected AazonDbl a;
	
	/**
	 * The buffered version of the current value captured from the mutable double.
	 */
	protected double x;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The mutable double.
	 */
	private AazonBufferedImmutableDbl( AazonDbl _a )
	{
		a = _a;
	}

	@Override
	public double getX() {
		if( !enabled )
		{
			x = a.getX();
			a = null;
			enabled = true;
		}
		
		return( x );
	}
	
	/**
	 * Constructs an immutable double from the input.
	 * @param _a The input double.
	 * @return An immutable double instance.
	 */
	public static AazonImmutableDbl construct( AazonDbl _a )
	{
		if( _a instanceof AazonBaseImmutableDbl )
		{
			return( (AazonBaseImmutableDbl) _a );
		}
		
		if( _a instanceof AazonBufferedImmutableDbl )
		{
			return( (AazonBufferedImmutableDbl) _a );
		}
		
		return( new AazonBufferedImmutableDbl( _a ) );
	}

	
}

