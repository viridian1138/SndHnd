




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
import javax.media.j3d.Node;
import javax.media.j3d.SharedGroup;


/**
 * Abstract base class for an AazonEnt for a mutable group of AazonEnts.
 * 
 * @author tgreen
 *
 */
public abstract class AazonMutableGroupBase {
	
	/**
	 * Shared group singleton used by the node.
	 */
	private SharedGroup sharedGroup = null;
	
	/**
	 * List of self-active objects.
	 */
	protected ArrayList ar;
	
	/**
	 * Whether the group has been initialized.
	 */
	protected boolean init = false;
	
	/**
	 * Constructs the group.
	 */
	public AazonMutableGroupBase()
	{
	}
	
	/**
	 * Gets the branch group for the AazonEnt.
	 * @return The branch group for the AazonEnt.
	 */
	public BranchGroup getBranch()
	{
		init();
		BranchGroup br = createParentBranchGroup();
		br.addChild( new Link( getSharedGroup() ) );
		return( br );
	}
	
	/**
	 * Gets the link for the AazonEnt.
	 * @return The link for the AazonEnt.
	 */
	public Link getLink()
	{
		init();
		return( new Link( getSharedGroup() ) );
	}
	
	/**
	 * Creates an instance of a shared group.
	 * @return The instance of the shared group.
	 */
	protected SharedGroup createSharedGroup()
	{
		final SharedGroup sharedGroup = new SharedGroup();
		sharedGroup.setCapability( SharedGroup.ALLOW_CHILDREN_EXTEND );
		sharedGroup.setCapability( SharedGroup.ALLOW_CHILDREN_READ );
		sharedGroup.setCapability( SharedGroup.ALLOW_CHILDREN_WRITE );
		return( sharedGroup );
	}
	
	/**
	 * Returns the singleton shared group of the AazonEnt.
	 * @return The singleton shared group of the AazonEnt.
	 */
	protected SharedGroup getSharedGroup()
	{
		if( sharedGroup == null )
		{
			sharedGroup = createSharedGroup();
		}
		
		return( sharedGroup );
	}

	/**
	 * Creates a branch group with permissions for manipulating children.
	 * @return The branch group with permissions for manipulating children.
	 */
	protected BranchGroup createParentBranchGroup()
	{
		BranchGroup ret = new BranchGroup();
		ret.setCapability( BranchGroup.ALLOW_DETACH );
		ret.setCapability( SharedGroup.ALLOW_CHILDREN_EXTEND );
		ret.setCapability( SharedGroup.ALLOW_CHILDREN_READ );
		ret.setCapability( SharedGroup.ALLOW_CHILDREN_WRITE );
		return( ret );
	}
	
	/**
	 * Creates an instance of a branch group containing a single node.
	 * @param nd The node to be placed in the BranchGroup.
	 * @return The instance of the branch group.
	 */
	protected BranchGroup createBranchGroup( Node nd )
	{
		BranchGroup ret = createBranchGroup();
		ret.addChild( nd );
		return( ret );
	}
	
	/**
	 * Creates an instance of a branch group.
	 * @return The instance of the branch group.
	 */
	protected BranchGroup createBranchGroup()
	{
		BranchGroup ret = new BranchGroup();
		ret.setCapability( BranchGroup.ALLOW_DETACH );
		return( ret );
	}
	
	/**
	 * Abstract method for initializing the group.
	 */
	protected abstract void init();
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		init();
		in.add( ar );
	}
	
	/**
	 * Returns an empty array of AazonEnts.
	 * @return The empty array of AAzonEnts.
	 */
	public static AazonEnt[] getEmpty()
	{
		AazonEnt[] ent = { };
		return( ent );
	}
	
	/**
	 * Convenience method for returning an array with a single AazonEnt.
	 * @param in Input AazonEnt to be put into the array.
	 * @return The generated array.
	 */
	public static AazonEnt[] getSingle( final AazonEnt in )
	{
		AazonEnt[] ent = { in };
		return( ent );
	}
	
	
}

