




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


/**
 * A mutable AazonEnt displaying a rasterized version (the sprite) of an input immutable AazonEnt contained in an AazonImmutableSprite.  This provides performance enhancements in cases where the raster can be displayed more quickly that the original AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonMutableSprite extends AazonMutableGroupBase implements
		AazonMutableEnt {
	
	/**
	 * The current state of the mutable AazonEnt.
	 */
	AazonImmutableSprite rect;
	
	/**
	 * Constructs the mutable AazonEnt.
	 * @param _rect The current state of the mutable AazonEnt.
	 */
	public AazonMutableSprite( AazonImmutableSprite _rect )
	{	
		setSprite( _rect );
	}
	
	/**
	 * Sets the current state of the mutable AazonEnt.
	 * @param ord The current state of the mutable AazonEnt.
	 */
	public void setSprite( AazonImmutableSprite ord )
	{
		rect = ord;
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
			
			Node node = rect.getNode();
		
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
			rect.genSelfActive( ar );
			rect = null;
			
			init = true;
		}
	}
	
	/**
	 * Returns the current state of the mutable AazonEnt.
	 * @return The current state of the mutable AazonEnt.
	 */
	public AazonImmutableEnt genImmutable()
	{
		return( rect );
	}

	
}

