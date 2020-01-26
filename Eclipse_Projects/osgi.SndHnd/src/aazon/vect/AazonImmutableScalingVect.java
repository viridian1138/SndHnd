




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


/**
 * Aazon immutable vector using one input vector as the affine scaling of another immutable vector.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableScalingVect extends AazonImmutableVect {
	
	/**
	 * First argument of the vector scaling.
	 */
	protected AazonImmutableVect a;
	
	/**
	 * Second argument of the vector scaling.
	 */
	protected AazonImmutableVect b;
	
	/**
	 * Private constructor.
	 * @param _a First argument of the vector scaling.
	 * @param _b Second argument of the vector scaling.
	 */
	private AazonImmutableScalingVect( AazonImmutableVect _a , AazonImmutableVect _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Constructs an Aazon immutable vector using one input vector as the affine scaling of another immutable vector.
	 * @param _a First argument of the vector scaling.
	 * @param _b Second argument of the vector scaling.
	 * @return The constructed vector.
	 */
	public static AazonImmutableVect construct( AazonImmutableVect _a , AazonImmutableVect _b )
	{
		if( ( _a instanceof AazonBaseImmutableVect ) && ( _b instanceof AazonBaseImmutableVect ) )
		{
			return( new AazonBaseImmutableVect( _a.getX() * _b.getX() , _a.getY() * _b.getY() ) );
		}
		return( new AazonImmutableScalingVect( _a , _b ) );
	}

	@Override
	public double getX() {
		return( a.getX() * b.getX() );
	}

	@Override
	public double getY() {
		return( a.getY() * b.getY() );
	}

	
}

