




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
 * Aazon immutable boolean representing the boolean "and" of two input immutable booleans.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableAndBool extends AazonImmutableBool {
	
	/**
	 * First argument of the boolean "and".
	 */
	protected AazonImmutableBool a;
	
	/**
	 * Second argument of the boolean "and".
	 */
	protected AazonImmutableBool b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the boolean "and".
	 * @param _b Second argument of the boolean "and".
	 */
	private AazonImmutableAndBool( AazonImmutableBool _a , AazonImmutableBool _b )
	{
		a = _a; 
		b = _b;
	}
	
	/**
	 * Constructs an Aazon immutable boolean representing the boolean "and" of two input immutable booleans.
	 * @param _a First argument of the boolean "and".
	 * @param _b Second argument of the boolean "and".
	 * @return The constructed boolean.
	 */
	public static AazonImmutableBool construct( AazonImmutableBool _a , AazonImmutableBool _b )
	{
		if( ( _a instanceof AazonBaseImmutableBool ) && ( _b instanceof AazonBaseImmutableBool ) )
		{
			return( _a.getBool().and( _b.getBool() ) );
		}
		return( new AazonImmutableAndBool( _a , _b ) );
	}

	@Override
	public AazonBaseImmutableBool getBool()
	{
		return( a.getBool().and( b.getBool() ) );
	}

	
}

