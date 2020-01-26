




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


import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Link;


/**
 * A mutable AazonEnt that is updated to reflect the state of an input mutable AazonEnt upon receipt of an input change event.
 * 
 * @author tgreen
 *
 */
public class AazonStateFiredMutableGroup implements AazonMutableEnt , AazonListener {

	/**
	 * The current updates state of the mutable AazonEnt.
	 */
	final protected AazonMutableGroup grp = new AazonMutableGroup();
	
	/**
	 * The input mutable AazonEnt.
	 */
	protected AazonMutableEnt gp;
	
	/**
	 * Constructs the mutable AazonEnt.
	 * @param _gp The input mutable AAzonEnt.
	 */
	public AazonStateFiredMutableGroup( AazonMutableEnt _gp )
	{
		gp = _gp;
		handleListen();
	}

	/**
	 * Gets the branch group for the AazonEnt.
	 * @return The branch group for the AazonEnt.
	 */
	public BranchGroup getBranch() {
		return( grp.getBranch() );
	}

	/**
	 * Gets the link for the AazonEnt.
	 * @return The link for the AazonEnt.
	 */
	public Link getLink() {
		return( grp.getLink() );
	}
	
	/**
	 * Returns the current state of the mutable AazonEnt.
	 * @return The current state of the mutable AazonEnt.
	 */
	public AazonImmutableEnt genImmutable()
	{
		return( gp.genImmutable() );
	}
	
	/**
	 * Handles a change by updating the current state.
	 */
	public void handleListen()
	{
		final AazonImmutableEnt ent = gp.genImmutable();
		final AazonImmutableGroup ord = new AazonImmutableGroup( ent );
		grp.setGrp( ord );
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		grp.genSelfActive( in );
	}

	
}

