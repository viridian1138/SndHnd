




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







package aazon.builderNode;

import java.util.ArrayList;

import aazon.AazonListener;
import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;


/**
 * Mutable node returning a BuilderNode for which the vector representing the mouse location is within the bounding box of the BuilderNode, or null if none found.
 * 
 * @author tgreen
 *
 */
public class AazonMutableChkBuilderNode extends AazonMutableBuilderNode implements AazonListener {
	
	/**
	 *  The set of BuilderNodes to check.  The BuilderNodes are presumed to be immutable.
	 */
	protected ArrayList<BuilderNode> a;
	
	/**
	 * The mutable vector representing the current mouse location.
	 */
	protected AazonMutableVect b;
	
	/**
	 * Private constructor.
	 * @param _a The set of BuilderNodes to check.  The BuilderNodes are presumed to be immutable.
	 * @param _b The immutable vector representing the current mouse location.
	 */
	private AazonMutableChkBuilderNode( ArrayList<BuilderNode> _a , AazonMutableVect _b )
	{
		a = _a;
		b = _b;
		
		b.add( this );
	}
	
	/**
	 * Returns a BuilderNode for which the vector representing the mouse location is within the bounding box of the BuilderNode, or null if none found.
	 * @param _a The set of BuilderNodes to check.  The BuilderNodes are presumed to be immutable.
	 * @param _b Vector representing the current mouse location.
	 * @return A BuilderNode for which the vector representing the mouse location is within the bounding box of the BuilderNode, or null if none found.
	 */
	public static AazonBuilderNode construct( ArrayList<BuilderNode> _a , AazonVect _b )
	{
		if( _b instanceof AazonImmutableVect )
		{
			return( AazonImmutableChkBuilderNode.construct( _a , (AazonImmutableVect) _b ) );
		}
		return( new AazonMutableChkBuilderNode( _a , (AazonMutableVect) _b ) );
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		return( AazonImmutableChkBuilderNode.performChk( univ , AazonBufferedImmutableVect.construct( b ) , a , fact) );
	}
	
	/**
	 * Handles a change in the vector by firing listeners.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

