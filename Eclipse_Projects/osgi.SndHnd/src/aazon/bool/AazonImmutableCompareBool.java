




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

import aazon.intg.AazonBaseImmutableInt;
import aazon.intg.AazonImmutableInt;


/**
 * Aazon immutable boolean representing the boolean comparison of two input immutable booleans.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableCompareBool extends AazonImmutableBool {
	
	/**
	 * First argument of the boolean comparison.
	 */
	protected AazonImmutableInt a;
	
	/**
	 * Second argument of the boolean comparison.
	 */
	protected AazonImmutableInt b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the boolean comparison.
	 * @param _b Second argument of the boolean comparison.
	 */
	private AazonImmutableCompareBool( AazonImmutableInt _a , AazonImmutableInt _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Constructs an Aazon immutable boolean representing the boolean comparison of two input immutable booleans.
	 * @param _a First argument of the boolean comparison.
	 * @param _b Second argument of the boolean comparison.
	 * @return The constructed boolean.
	 */
	public static AazonImmutableBool construct( AazonImmutableInt _a , AazonImmutableInt _b )
	{
		if( ( _a instanceof AazonBaseImmutableInt ) && ( _b instanceof AazonBaseImmutableInt ) )
		{
			return( AazonBaseImmutableBool.construct( ( _a.getX() ) == ( _b.getX() ) ) );
		}
		return( new AazonImmutableCompareBool( _a , _b ) );
	}

	@Override
	public AazonBaseImmutableBool getBool()
	{
		return( AazonBaseImmutableBool.construct( ( a.getX() ) == ( b.getX() ) ) );
	}

	
}

