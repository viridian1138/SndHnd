




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

import aazon.AazonListener;


/**
 * Aazon mutable integer representing the integer addition of two input integers.
 * 
 * @author tgreen
 *
 */
public class AazonMutableAdditiveInt extends AazonMutableInt implements AazonListener {
	
	/**
	 * First argument of the integer addition.
	 */
	protected AazonInt a;
	
	/**
	 * Second argument of the integer addition.
	 */
	protected AazonInt b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the integer addition.
	 * @param _b Second argument of the integer addition.
	 */
	private AazonMutableAdditiveInt( AazonInt _a , AazonInt _b )
	{
		a = _a;
		b = _b;
		
		if( a instanceof AazonMutableInt )
		{
			( (AazonMutableInt) a ).add( this );
		}
		
		if( b instanceof AazonMutableInt )
		{
			( (AazonMutableInt) b ).add( this );
		}
	}
	
	/**
	 * Constructs an Aazon integer representing the integer addition of two input integers.
	 * @param _a First argument of the integer addition.
	 * @param _b Second argument of the integer addition.
	 * @return The constructed integer.
	 */
	public static AazonInt construct( AazonInt _a , AazonInt _b )
	{
		if( ( _a instanceof AazonImmutableInt ) && ( _b instanceof AazonImmutableInt ) )
		{
			return( AazonImmutableAdditiveInt.construct( (AazonImmutableInt) _a , (AazonImmutableInt) _b ) );
		}
		return( new AazonMutableAdditiveInt( _a , _b ) );
	}

	@Override
	public int getX() {
		return( a.getX() + b.getX() );
	}
	
	/**
	 * Handles a change to one of the arguments by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

