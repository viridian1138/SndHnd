




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
 * Aazon mutable double representing a simple state with a "get" method and a "set" method.
 * 
 * @author tgreen
 *
 */
public class AazonSimpleMutableDbl extends AazonMutableDbl implements AazonImmutSetMutableDbl {
	
	/**
	 * The current state of the mutable double.
	 */
	AazonImmutableDbl a;
	
	/**
	 * Constructs the mutable double.
	 * @param _a The current state of the mutable double.
	 */
	public AazonSimpleMutableDbl( AazonImmutableDbl _a )
	{
		a = _a;
	}

	@Override
	public double getX() {
		return( a.getX() );
	}
	
	/**
	 * Sets the current state of the mutable double.
	 * @param _a The current state of the mutable double.
	 */
	public void setCoords( AazonImmutableDbl _a )
	{
		a = _a;
		fire();
	}

	
}

