




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
import javax.media.j3d.OrderedGroup;
import javax.media.j3d.SharedGroup;


/**
 * Mutable AazonEnt containing a group of enclosed AazonEnts that are displayed in order.
 * 
 * @author tgreen
 *
 */
public class AazonMutableOrderedGroup extends AazonMutableGroupBase implements AazonMutableEnt {
	
	/**
	 * The current state of the group.
	 */
	AazonImmutableOrderedGroup grp;
	
	/**
	 * Constructs the group given an input state.
	 * @param in The current state of the group.
	 */
	public AazonMutableOrderedGroup( AazonImmutableOrderedGroup in )
	{	
		setGrp( in );
	}
	
	/**
	 * Constructs the group as empty.
	 */
	public AazonMutableOrderedGroup()
	{
		this( new AazonImmutableOrderedGroup() );
	}
	
	/**
	 * Constructs the group containing a single AazonEnt.
	 * @param in The AazonEnt to be contained.
	 */
	public AazonMutableOrderedGroup( AazonEnt in )
	{
		this( new AazonImmutableOrderedGroup( in ) );
	}
	
	/**
	 * Returns the current state of the mutable AazonEnt.
	 * @return The current state of the mutable AazonEnt.
	 */
	public AazonImmutableEnt genImmutable()
	{
		return( grp );
	}
	
	/**
	 * Sets the current state of the group.
	 * @param ord The current state of the group.
	 */
	public void setGrp( AazonImmutableOrderedGroup ord )
	{
		grp = ord;
		if( init )
		{
			init = false;
			init();
		}
	}
	
	@Override
	protected void init()
	{
		if( !init )
		{
			getSharedGroup().removeAllChildren();
			
			OrderedGroup gp = grp.getOrderedGroup();
		
			BranchGroup br = createBranchGroup( gp );
		
			getSharedGroup().addChild( br );
			
			if( ar == null )
			{
				ar = new ArrayList();
			}
			else
			{
				ar.clear();
			}
			grp.genSelfActive( ar );
			grp = null;
			
			init = true;
		}
	}

	
}

