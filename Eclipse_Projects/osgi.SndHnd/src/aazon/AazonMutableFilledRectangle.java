




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
 * AazonEnt for a mutable filled rectangle.
 * 
 * @author tgreen
 *
 */
public class AazonMutableFilledRectangle extends AazonMutableGroupBase implements
		AazonMutableEnt {
	
	/**
	 * The current state of the filled rectangle.
	 */
	AazonImmutableFilledRectangle rect;
	
	/**
	 * Constructs the mutable filled rectangle.
	 * @param _rect The current state of the filled rectangle.
	 */
	public AazonMutableFilledRectangle( AazonImmutableFilledRectangle _rect )
	{	
		setRect( _rect );
	}
	
	/**
	 * Sets the current state of the filled rectangle.
	 * @param ord The current state of the filled rectangle.
	 */
	public void setRect( AazonImmutableFilledRectangle ord )
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

