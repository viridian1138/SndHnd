




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
 * A base class for an Aazon mutable vector.
 * 
 * @author tgreen
 *
 */
public class AazonBaseMutableVect extends AazonMutableVect implements AazonCoordSetMutableVect {
	
	/**
	 * The X-coordinate value of the vector.
	 */
	protected double x;
	
	/**
	 * The Y-coordinate value of the vector.
	 */
	protected double y;
	
	/**
	 * Constructs the mutable vector.
	 * @param _x The X-coordinate value of the vector.
	 * @param _y The Y-coordinate value of the vector.
	 */
	public AazonBaseMutableVect( double _x , double _y )
	{
		x = _x;
		y = _y;
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
	 * Sets the current state of the mutable vector.
	 * @param _x The X-coordinate value of the vector.
	 * @param _y The Y-coordinate value of the vector.
	 */
	public void setCoords( double _x , double _y )
	{
		x = _x;
		y = _y;
		fire();
	}

	
}

