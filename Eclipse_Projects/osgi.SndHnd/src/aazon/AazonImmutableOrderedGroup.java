




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

import javax.media.j3d.Node;
import javax.media.j3d.OrderedGroup;


/**
 * Immutable AazonEnt containing a group of enclosed AazonEnts that are displayed in order.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableOrderedGroup implements AazonImmutableEnt {
	
	/**
	 * Ordered group node.
	 */
	OrderedGroup group;
	
	/**
	 * The array of enclosed AazonEnts.
	 */
	AazonEnt[] ents;
	
	/**
	 * List of self-active objects.
	 */
	ArrayList ar = null;
	
	/**
	 * Whether the group has been initialized.
	 */
	boolean init = false;
	
	/**
	 * Constructs the group as containing an array of AazonEnts.
	 * @param _ents The array of AazonEnts to be contained.
	 */
	public AazonImmutableOrderedGroup( AazonEnt[] _ents )
	{
		ents = _ents;
	}
	
	/**
	 * Constructs the group as empty.
	 */
	public AazonImmutableOrderedGroup()
	{
		this( AazonMutableGroupBase.getEmpty() );
	}
	
	/**
	 * Constructs the group containing a single AazonEnt.
	 * @param in The AazonEnt to be contained.
	 */
	public AazonImmutableOrderedGroup( AazonEnt in )
	{
		this( AazonMutableGroupBase.getSingle( in ) );
	}
	
	/**
	 * Initializes the group.
	 */
	protected void init()
	{
		if( !init )
		{
			group = new OrderedGroup();
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
					group.addChild( ( (AazonMutableEnt) ent ).getBranch() );
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
	 * Gets the ordered group node.
	 * @return The ordered group node.
	 */
	public OrderedGroup getOrderedGroup()
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

