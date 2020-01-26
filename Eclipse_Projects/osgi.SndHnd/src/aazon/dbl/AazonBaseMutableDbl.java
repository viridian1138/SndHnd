




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
 * A base class for an Aazon mutable double.
 * 
 * @author tgreen
 *
 */
public class AazonBaseMutableDbl extends AazonMutableDbl implements AazonCoordSetMutableDbl {
	
	/**
	 * The current value of the mutable double.
	 */
	protected double x;
	
	/**
	 * Constructs the mutable double.
	 * @param _x The current value of the mutable double.
	 */
	public AazonBaseMutableDbl( double _x )
	{
		x = _x;
	}

	@Override
	public double getX() {
		return( x );
	}
	
	/**
	 * Sets the current value of the mutable double.
	 * @param _x The current value of the mutable double.
	 */
	public void setCoords( double _x )
	{
		x = _x;
		fire();
	}

	
}

