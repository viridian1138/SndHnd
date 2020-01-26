




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

import aazon.AazonListener;


/**
 * Aazon mutable boolean representing the boolean "and" of two input booleans.
 * 
 * @author tgreen
 *
 */
public class AazonMutableAndBool extends AazonMutableBool implements AazonListener {
	
	/**
	 * First argument of the boolean "and".
	 */
	protected AazonBool a;
	
	/**
	 * Second argument of the boolean "and".
	 */
	protected AazonBool b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the boolean "and".
	 * @param _b Second argument of the boolean "and".
	 */
	private AazonMutableAndBool( AazonBool _a , AazonBool _b )
	{
		a = _a;
		b = _b;
		
		if( a instanceof AazonMutableBool )
		{
			( (AazonMutableBool) a ).add( this );
		}
		
		if( b instanceof AazonMutableBool )
		{
			( (AazonMutableBool) b ).add( this );
		}
	}
	
	/**
	 * Constructs an Aazon boolean representing the boolean "and" of two input booleans.
	 * @param _a First argument of the boolean "and".
	 * @param _b Second argument of the boolean "and".
	 * @return The constructed boolean.
	 */
	public static AazonBool construct( AazonBool _a , AazonBool _b )
	{
		if( ( _a instanceof AazonImmutableBool ) && ( _b instanceof AazonImmutableBool ) )
		{
			return( AazonImmutableAndBool.construct( (AazonImmutableBool) _a , (AazonImmutableBool) _b ) );
		}
		return( new AazonMutableAndBool( _a , _b ) );
	}

	@Override
	public AazonBaseImmutableBool getBool()
	{
		return( a.getBool().and( b.getBool() ) );
	}
	
	/**
	 * Handles a change to one of the arguments by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

