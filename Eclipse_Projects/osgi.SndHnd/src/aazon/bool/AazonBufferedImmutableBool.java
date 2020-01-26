




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







package aazon.bool;

/**
 * Aazon class for an immutable boolean returning a buffered version of the current state captured from a mutable boolean.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedImmutableBool extends AazonImmutableBool {

	/**
	 * The mutable boolean.
	 */
	protected AazonBool a;
	
	/**
	 * The buffered version of the current state captured from the mutable boolean.
	 */
	protected AazonBaseImmutableBool b;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The mutable boolean.
	 */
	private AazonBufferedImmutableBool( AazonBool _a )
	{
		a = _a;
	}

	@Override
	public AazonBaseImmutableBool getBool() {
		if( !enabled )
		{
			b = a.getBool();
			a = null;
			enabled = true;
		}
		
		return( b );
	}
	
	/**
	 * Constructs an immutable boolean from the input.
	 * @param _a The input boolean.
	 * @return An immutable boolean instance.
	 */
	public static AazonImmutableBool construct( AazonBool _a )
	{
		if( _a instanceof AazonBaseImmutableBool )
		{
			return( (AazonBaseImmutableBool) _a );
		}
		
		if( _a instanceof AazonBufferedImmutableBool )
		{
			return( (AazonBufferedImmutableBool) _a );
		}
		
		return( new AazonBufferedImmutableBool( _a ) );
	}

	
}

