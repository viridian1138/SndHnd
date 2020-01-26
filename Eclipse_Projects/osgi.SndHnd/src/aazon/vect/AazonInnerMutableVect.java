




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
 * Aazon abstract class for a mutable vector representing a state, with a "get" method and a "set" method, and an abstract method for defining the initial state.
 * 
 * @author tgreen
 *
 */
public abstract class AazonInnerMutableVect extends AazonMutableVect implements AazonCoordSetMutableVect {
	
	/**
	 * The X-coordinate value of the vector.
	 */
	protected double x;
	
	/**
	 * The Y-coordinate value of the vector.
	 */
	protected double y;
	
	/**
	 * Constructs the class.
	 */
	public AazonInnerMutableVect( )
	{
		x = getInnerX();
		y = getInnerY();
	}

	@Override
	public double getX() {
		return( x );
	}

	@Override
	public double getY() {
		return( y );
	}
	
	/**
	 * Sets the current value of the mutable state.
	 * @param _a The current value of the mutable state.
	 */
	public void setCoords( double _x , double _y )
	{
		x = _x;
		y = _y;
		fire();
	}
	
	/**
	 * Gets the initial value for the X-coordinate of the vector.
	 * @return The initial value for the X-coordinate of the vector.
	 */
	protected abstract double getInnerX();
	
	/**
	 * Gets the initial value for the Y-coordinate of the vector.
	 * @return The initial value for the Y-coordinate of the vector.
	 */
	protected abstract double getInnerY();

	
}

