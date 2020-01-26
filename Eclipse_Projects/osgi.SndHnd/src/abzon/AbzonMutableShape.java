




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







package abzon;


import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Node;

import aazon.AazonImmutableEnt;
import aazon.AazonMutableEnt;
import aazon.AazonMutableGroupBase;

/**
 * AazonEnt for a mutable shape.
 * 
 * @author tgreen
 *
 */
public class AbzonMutableShape extends AazonMutableGroupBase implements
		AazonMutableEnt {
	
	/**
	 * The current state of the mutable shape.
	 */
	AbzonImmutableShape shape;
	
	/**
	 * Constructs the shape.
	 * @param _line The initial state of the mutable shape.
	 */
	public AbzonMutableShape( AbzonImmutableShape _line )
	{	
		setShape( _line );
	}
	
	/**
	 * Sets the current state of the mutable shape.
	 * @param ord The current state of the mutable shape.
	 */
	public void setShape( AbzonImmutableShape ord )
	{
		shape = ord;
		if( init )
		{
			init = false;
			init();
		}
	}
	
	/**
	 * Gets the current state of the mutable shape.
	 * @return The current state of the mutable shape.
	 */
	public AazonImmutableEnt genImmutable()
	{
		return( shape );
	}
	
	@Override
	protected void init()
	{
		if( !init )
		{
			getSharedGroup().removeAllChildren();
			
			Node node = shape.getNode();
		
			BranchGroup br = createBranchGroup( node );
		
			getSharedGroup().addChild( br );
			
			if( ar == null )
			{
				ar = new ArrayList();
			}
			else
			{
				ar.clear();
			}
			shape.genSelfActive( ar );
			shape = null;
			
			init = true;
		}
	}

	
}

