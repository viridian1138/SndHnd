




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
 * Aazon immutable double representing the addition of two input immutable doubles.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableAdditiveDbl extends AazonImmutableDbl {
	
	/**
	 * First argument of the double addition.
	 */
	protected AazonImmutableDbl a;
	
	/**
	 * Second argument of the double addition.
	 */
	protected AazonImmutableDbl b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the double addition.
	 * @param _b Second argument of the double addition.
	 */
	private AazonImmutableAdditiveDbl( AazonImmutableDbl _a , AazonImmutableDbl _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Constructs an Aazon immutable double representing the addition of two input immutable doubles.
	 * @param _a First argument of the double addition.
	 * @param _b Second argument of the double addition.
	 * @return The constructed double.
	 */
	public static AazonImmutableDbl construct( AazonImmutableDbl _a , AazonImmutableDbl _b )
	{
		if( ( _a instanceof AazonBaseImmutableDbl ) && ( _b instanceof AazonBaseImmutableDbl ) )
		{
			return( new AazonBaseImmutableDbl( _a.getX() + _b.getX() ) );
		}
		return( new AazonImmutableAdditiveDbl( _a , _b ) );
	}

	@Override
	public double getX() {
		return( a.getX() + b.getX() );
	}

	
}

