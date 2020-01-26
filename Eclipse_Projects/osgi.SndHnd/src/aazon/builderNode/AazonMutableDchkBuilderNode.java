




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

import aazon.AazonListener;


/**
 * Aazon mutable builder node that returns a potential child node if one node will accept another node as its child, otherwise returns null.
 * 
 * @author tgreen
 *
 */
public class AazonMutableDchkBuilderNode extends AazonMutableBuilderNode implements AazonListener {
	
	/**
	 * The node that would be potentially accepting the child node.
	 */
	protected AazonBuilderNode a;
	
	/**
	 * The potential child node.
	 */
	protected AazonBuilderNode b;
	
	/**
	 * Private constructor.
	 * @param _a The node that would be potentially accepting the child node.
	 * @param _b The potential child node.
	 */
	private AazonMutableDchkBuilderNode( AazonBuilderNode _a , AazonBuilderNode _b )
	{
		a = _a;
		b = _b;
		
		if( a instanceof AazonMutableBuilderNode )
		{
			( (AazonMutableBuilderNode) a ).add( this );
		}
		
		if( b instanceof AazonMutableBuilderNode )
		{
			( (AazonMutableBuilderNode) b ).add( this );
		}
	}
	
	/**
	 * Returns an Aazon builder node that returns a potential child node if one node will accept another node as its child, otherwise returns null.
	 * @param univ The universe ID of the nodes.
	 * @param _a The node that would be potentially accepting the child node.
	 * @param _b The potential child node.
	 * @param fact Factory for generating vectors to the center point of a node.
	 * @return Aazon builder node that returns a potential child node if one node will accept another node as its child, otherwise returns null.
	 */
	public static AazonBuilderNode construct( Object univ , AazonBuilderNode _a , AazonBuilderNode _b , final AazonCenterVectLocationFactory fact )
	{
		if( ( _a instanceof AazonImmutableBuilderNode ) && ( _b instanceof AazonImmutableBuilderNode ) )
		{
			return( AazonImmutableDchkBuilderNode.construct( univ , (AazonImmutableBuilderNode) _a , (AazonImmutableBuilderNode) _b  , fact ) );
		}
		return( new AazonMutableDchkBuilderNode( _a , _b ) );
	}

	@Override
	public BuilderNode getX(Object univ, final AazonCenterVectLocationFactory fact ) {
		return( AazonImmutableDchkBuilderNode.performChk( univ, a,  b, fact) );
	}
	
	/**
	 * Handles a change to one of the builder nodes by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

