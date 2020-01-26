




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
import javax.media.j3d.Node;
import javax.media.j3d.SharedGroup;


/**
 * Immutable AazonEnt group that can be shared/reused among multiple parent AazonEnts.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableSharedEnt implements AazonImmutableEnt {
	
	/**
	 * Shared group node internal to the AazonImmutableSharedEnt.  This actually takes class AazonMutableGroup and removes a lot of the mutability permissions.
	 * 
	 * @author tgreen
	 *
	 */
	protected static class SharedEntGroup extends AazonMutableGroup
	{
		
		/**
		 * Constructs the mutable group given an input state.  Note: once constructed the aggregating AazonImmutableSharedEnt doesn't change the state of this class.
		 * @param in The current state of the group.
		 */
		public SharedEntGroup( AazonImmutableGroup in )
		{
			super( in );
		}
		
		@Override
		protected SharedGroup createSharedGroup()
		{
			final SharedGroup sharedGroup = new SharedGroup();
			return( sharedGroup );
		}
		
		@Override
		protected BranchGroup createParentBranchGroup()
		{
			BranchGroup ret = new BranchGroup();
			return( ret );
		}
		
		@Override
		protected BranchGroup createBranchGroup()
		{
			BranchGroup ret = new BranchGroup();
			return( ret );
		}
		
		
	}
	
	
	/**
	 * Shared group used to contain the children of the node.
	 */
	protected SharedEntGroup grp;
	
	/**
	 * Constructs the group as containing an array of AazonEnts.
	 * @param _ents The array of AazonEnts to be contained.
	 */
	public AazonImmutableSharedEnt( AazonEnt[] ents )
	{
		grp = new SharedEntGroup( new AazonImmutableGroup( ents ) );
	}
	
	/**
	 * Constructs the group containing a single AazonEnt.
	 * @param in The AazonEnt to be contained.
	 */
	public AazonImmutableSharedEnt( AazonEnt ent )
	{
		this( AazonMutableGroupBase.getSingle( ent ) );
	}

	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		return( grp.getLink() );
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

