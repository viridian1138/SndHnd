




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

import aazon.dbl.AazonBaseImmutableDbl;
import aazon.dbl.AazonImmutableDbl;


/**
 * Aazon immutable vector representing a vector divided by a scalar.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableScalarDivideVect extends AazonImmutableVect {
	
	/**
	 * The vector to be divided.
	 */
	protected AazonImmutableVect a;
	
	/**
	 * The scalar divisor.
	 */
	protected AazonImmutableDbl b;
	
	/**
	 * Private constructor.
	 * @param _a The vector to be divided.
	 * @param _b The scalar divisor.
	 */
	private AazonImmutableScalarDivideVect( AazonImmutableVect _a , AazonImmutableDbl _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Constructs an Aazon immutable vector representing a vector divided by a scalar.
	 * @param _a The vector to be divided.
	 * @param _b The scalar divisor.
	 * @return The constructed vector.
	 */
	public static AazonImmutableVect construct( AazonImmutableVect _a , AazonImmutableDbl _b )
	{
		if( ( _a instanceof AazonBaseImmutableVect ) && ( _b instanceof AazonBaseImmutableDbl ) )
		{
			return( new AazonBaseImmutableVect( _a.getX() / _b.getX() , _a.getY() / _b.getX() ) );
		}
		return( new AazonImmutableScalarDivideVect( _a , _b ) );
	}

	@Override
	public double getX() {
		return( a.getX() / b.getX() );
	}

	@Override
	public double getY() {
		return( a.getY() / b.getX() );
	}

	
}

