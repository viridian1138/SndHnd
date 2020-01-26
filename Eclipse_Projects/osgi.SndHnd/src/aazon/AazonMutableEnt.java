




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









package aazon;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Link;


/**
 * A mutable AazonEnt.
 * 
 * @author tgreen
 *
 */
public interface AazonMutableEnt extends AazonEnt {
	
	/**
	 * Gets the branch group for the AazonEnt.
	 * @return The branch group for the AazonEnt.
	 */
	public BranchGroup getBranch();
	
	/**
	 * Gets the link for the AazonEnt.
	 * @return The link for the AazonEnt.
	 */
	public Link getLink();
	
	/**
	 * Returns the current state of the mutable AazonEnt.
	 * @return The current state of the mutable AazonEnt.
	 */
	public AazonImmutableEnt genImmutable();

	
}

