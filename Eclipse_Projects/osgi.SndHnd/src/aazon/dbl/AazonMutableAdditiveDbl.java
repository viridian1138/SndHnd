




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

import aazon.AazonListener;


/**
 * Aazon mutable double representing the double addition of two input doubles.
 * 
 * @author tgreen
 *
 */
public class AazonMutableAdditiveDbl extends AazonMutableDbl implements AazonListener {
	
	/**
	 * First argument of the double addition.
	 */
	protected AazonDbl a;
	
	/**
	 * Second argument of the double addition.
	 */
	protected AazonDbl b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the double addition.
	 * @param _b Second argument of the double addition.
	 */
	private AazonMutableAdditiveDbl( AazonDbl _a , AazonDbl _b )
	{
		a = _a;
		b = _b;
		
		if( a instanceof AazonMutableDbl )
		{
			( (AazonMutableDbl) a ).add( this );
		}
		
		if( b instanceof AazonMutableDbl )
		{
			( (AazonMutableDbl) b ).add( this );
		}
	}
	
	/**
	 * Constructs an Aazon double representing the double addition of two input doubles.
	 * @param _a First argument of the double addition.
	 * @param _b Second argument of the double addition.
	 * @return The constructed double.
	 */
	public static AazonDbl construct( AazonDbl _a , AazonDbl _b )
	{
		if( ( _a instanceof AazonImmutableDbl ) && ( _b instanceof AazonImmutableDbl ) )
		{
			return( AazonImmutableAdditiveDbl.construct( (AazonImmutableDbl) _a , (AazonImmutableDbl) _b ) );
		}
		return( new AazonMutableAdditiveDbl( _a , _b ) );
	}

	@Override
	public double getX() {
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

