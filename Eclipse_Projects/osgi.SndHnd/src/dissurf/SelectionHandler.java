




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







package dissurf;

/**
 * Handles a user selection on the where the user clicked on the dissonance surface.
 * 
 * @author tgreen
 *
 */
public interface SelectionHandler {

	/**
	 * Handles a user selection where the user clicked on on the dissonance surface.
	 * @param d1 The domain X-value on the surface.
	 * @param d2 The domain Y-value on the surface.
	 */
	public void handleSelection( double d1 , double d2 );

}

