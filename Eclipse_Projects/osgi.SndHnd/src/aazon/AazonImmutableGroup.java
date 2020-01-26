




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
import javax.media.j3d.Node;


/**
 * Immutable AazonEnt containing a group of enclosed AazonEnts that can be displayed in any order that improves display times.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableGroup implements AazonImmutableEnt {
	
	/**
	 * Group node.
	 */
	Group group;
	
	/**
	 * The array of enclosed AazonEnts.
	 */
	AazonEnt[] ents;
	
	/**
	 * List of self-active objects.
	 */
	ArrayList ar = null;
	
	/**
	 * Branch group node.
	 */
	BranchGroup tbranch = null;
	
	/**
	 * Whether the group has been initialized.
	 */
	boolean init = false;
	
	/**
	 * Constructs the group as containing an array of AazonEnts.
	 * @param _ents The array of AazonEnts to be contained.
	 */
	public AazonImmutableGroup( AazonEnt[] _ents )
	{
		ents = _ents;
	}
	
	/**
	 * Constructs the group as empty.
	 */
	public AazonImmutableGroup()
	{
		this( AazonMutableGroupBase.getEmpty() );
	}
	
	/**
	 * Constructs the group containing a single AazonEnt.
	 * @param in The AazonEnt to be contained.
	 */
	public AazonImmutableGroup( AazonEnt in )
	{
		this( AazonMutableGroupBase.getSingle( in ) );
	}
	
	/**
	 * Constructs the group as containing the contents of a list of AazonEnts.
	 * @param _ents The list of AazonEnts to be contained.
	 */
	public AazonImmutableGroup( ArrayList<AazonEnt> alst )
	{
		this( genArr( alst ) );
	}
	
	/**
	 * Returns an array of AazonEnts given a list of AazonEnts.
	 * @param alst The input list of AazonEnts.
	 * @return The generated array of AazonEnts.
	 */
	private static AazonEnt[] genArr( final ArrayList<AazonEnt> alst )
	{
		final int sz = alst.size();
		AazonEnt[] ents = new AazonEnt[ sz ];
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			ents[ count ] = alst.get( count );
		}
		return( ents );
	}
	
	/**
	 * Returns the immutable group that results from removing an AazonEnt from this immutable group.
	 * @param ent The AazonEnt to be removed.
	 * @return The resulting immutable group.
	 */
	public AazonImmutableGroup remmoveEnt( AazonEnt ent )
	{
		ArrayList<AazonEnt> lst = new ArrayList<AazonEnt>();
		int count;
		for( count = 0 ; count < ents.length ; count++ )
		{
			if( ents[ count ] != ent )
			{
				lst.add( ents[ count ] );
			}
		}
		return( new AazonImmutableGroup( lst ) );
	}
	
	/**
	 * Initializes the group.
	 */
	protected void init()
	{
		if( !init )
		{
			group = new Group();
			ar = new ArrayList();
			final int len = ents.length;
			for( int count = 0 ; count < len ; count++ )
			{
				final AazonEnt ent = ents[ count ];
				if( ent instanceof AazonImmutableEnt )
				{
					group.addChild( ( (AazonImmutableEnt) ent ).getNode() );
				}
				else
				{
					if( tbranch == null )
					{
						tbranch = ( (AazonMutableEnt) ent ).getBranch();
						group.addChild( tbranch );
					}
					else
					{
						tbranch.addChild( ( (AazonMutableEnt) ent ).getLink() );
					}
				}
				ent.genSelfActive( ar );
			}
			ents = null;
			init = true;
		}
	}

	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() 
	{
		init();
		return( group );
	}
	
	/**
	 * Gets the group node.
	 * @return The group node.
	 */
	public Group getGroup()
	{
		init();
		return( group );
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		if( ar != null )
		{
			in.add( ar );
			return;
		}
		
		int count;
		final int sz = ents.length;
		for( count = 0 ; count < sz ; count++ )
		{
			ents[ count ].genSelfActive( in );
		}
	}

	
}

