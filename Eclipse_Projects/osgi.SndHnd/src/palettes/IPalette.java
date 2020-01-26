




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







package palettes;

import gredit.GNode;

import java.util.ArrayList;


/**
 * Interface defining accessors to state information for a GUI palette.
 * 
 * @author tgreen
 *
 */
public interface IPalette {
	
	/**
	 * Gets the set of classes defined for the creational tool of the palette.
	 * 
	 * @return The set of classes defined for the creational tool of the palette.
	 */
	public ArrayList<Class<? extends GNode>> getPaletteClasses();
	
	/**
	 * Adds a listener to the palette state that is called whenever a palette class is added or deleted from the creation tool.
	 * 
	 * @param r The listener to be added.
	 */
	public void addPaletteListener( Runnable r );
	
	/**
	 * Removes a listener from the palette state.
	 * 
	 * @param r The listener to be removed.
	 */
	public void removePaletteListener( Runnable r );

}

