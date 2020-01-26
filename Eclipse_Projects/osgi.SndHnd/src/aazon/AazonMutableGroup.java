




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
import javax.media.j3d.Group;


/**
 * Mutable AazonEnt containing a group of enclosed AazonEnts that can be displayed in any order that improves display times.
 * 
 * @author tgreen
 *
 */
public class AazonMutableGroup extends AazonMutableGroupBase implements AazonMutableEnt {
	
	/**
	 * The current state of the group.
	 */
	AazonImmutableGroup grp;
	
	/**
	 * Constructs the mutable group given an input state.
	 * @param in The current state of the group.
	 */
	public AazonMutableGroup( AazonImmutableGroup in )
	{	
		setGrp( in );
	}
	
	/**
	 * Constructs the group as empty.
	 */
	public AazonMutableGroup()
	{
		this( new AazonImmutableGroup() );
	}
	
	/**
	 * Constructs the group containing a single AazonEnt.
	 * @param in The AazonEnt to be contained.
	 */
	public AazonMutableGroup( AazonEnt in )
	{
		this( new AazonImmutableGroup( in ) );
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
	public void setGrp( AazonImmutableGroup ord )
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
			
			Group gp = grp.getGroup();
		
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
			clrGrp();
			
			init = true;
		}
	}
	
	
	/**
	 * Clears the current state of the group.
	 */
	protected void clrGrp()
	{
		grp = null;
	}

	
}

