




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







package aczon.node;

import aazon.AazonEnt;
import aazon.vect.AazonVect;

/**
 * Node representing a button on the palette.
 * 
 * @author tgreen
 *
 */
public interface PaletteNode {
	
	/**
	 * Gets the AazonEnt to be displayed for the palette button.
	 * @return The AazonEnt to be displayed for the palette button.
	 */
	public abstract AazonEnt getEnt();
	
	/**
	 * Gets the location of the lower-left corner of the palette button.
	 * @return The location of the lower-left corner of the palette button.
	 */
	public abstract AazonVect getBox0();
	
	/**
	 * Gets the location of the upper-right corner of the palette button.
	 * @return The location of the upper-right corner of the palette button.
	 */
	public abstract AazonVect getBox1();
	
	/**
	 * Gets the editing mode associated with the selection of the palette button.
	 * @return The editing mode associated with the selection of the palette button.
	 */
	public abstract int getMode();

}

