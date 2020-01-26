




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
 * Aazon immutable integer representing the addition of two input immutable integers.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableAdditiveInt extends AazonImmutableInt {
	
	/**
	 * First argument of the integer addition.
	 */
	protected AazonImmutableInt a;
	
	/**
	 * Second argument of the integer addition.
	 */
	protected AazonImmutableInt b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the integer addition.
	 * @param _b Second argument of the integer addition.
	 */
	private AazonImmutableAdditiveInt( AazonImmutableInt _a , AazonImmutableInt _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Constructs an Aazon immutable integer representing the addition of two input immutable integers.
	 * @param _a First argument of the integer addition.
	 * @param _b Second argument of the integer addition.
	 * @return The constructed integer.
	 */
	public static AazonImmutableInt construct( AazonImmutableInt _a , AazonImmutableInt _b )
	{
		if( ( _a instanceof AazonBaseImmutableInt ) && ( _b instanceof AazonBaseImmutableInt ) )
		{
			return( new AazonBaseImmutableInt( _a.getX() + _b.getX() ) );
		}
		return( new AazonImmutableAdditiveInt( _a , _b ) );
	}

	@Override
	public int getX() {
		return( a.getX() + b.getX() );
	}

	
}

