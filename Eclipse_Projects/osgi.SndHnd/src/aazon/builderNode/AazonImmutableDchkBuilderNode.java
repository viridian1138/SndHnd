




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


/**
 * Aazon immutable builder node that returns a potential child node if one node will accept another node as its child, otherwise returns null.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableDchkBuilderNode extends AazonImmutableBuilderNode {
	
	/**
	 * The node that would be potentially accepting the child node.
	 */
	protected AazonImmutableBuilderNode a;
	
	/**
	 * The potential child node.
	 */
	protected AazonImmutableBuilderNode b;
	
	/**
	 * Private constructor.
	 * @param _a The node that would be potentially accepting the child node.
	 * @param _b The potential child node.
	 */
	private AazonImmutableDchkBuilderNode( AazonImmutableBuilderNode _a , AazonImmutableBuilderNode _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Returns an Aazon immutable builder node that returns a potential child node if one node will accept another node as its child, otherwise returns null.
	 * @param univ The universe ID of the nodes.
	 * @param _a The node that would be potentially accepting the child node.
	 * @param _b The potential child node.
	 * @param fact Factory for generating vectors to the center point of a node.
	 * @return Aazon immutable builder node that returns a potential child node if one node will accept another node as its child, otherwise returns null.
	 */
	public static AazonImmutableBuilderNode construct( Object univ , AazonImmutableBuilderNode _a , AazonImmutableBuilderNode _b , AazonCenterVectLocationFactory fact )
	{
		if( ( _a instanceof AazonBaseImmutableBuilderNode ) && ( _b instanceof AazonBaseImmutableBuilderNode ) )
		{
			return( new AazonBaseImmutableBuilderNode( performChk( univ , _a , _b , fact ) ) );
		}
		return( new AazonImmutableDchkBuilderNode( _a , _b ) );
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		return( performChk( univ , a , b , fact ) );
	}
	
	/**
	 * Returns a potential child node if one BuilderNode will accept another BuilderNode as its child.  It is presumed that the BuilderNodes are immutable, otherwise returns null.
	 * @param univ The universe ID of the nodes.
	 * @param _a The node that would be potentially accepting the child node.
	 * @param _b The potential child node.
	 * @param fact Factory for generating vectors to the center point of a node.
	 * @return The potential child node if one BuilderNode will accept another BuilderNode as its child, otherwise null.
	 */
	public static BuilderNode performChk( Object univ , AazonBuilderNode _a , AazonBuilderNode _b , AazonCenterVectLocationFactory fact )
	{
		if( ( _a == null ) || ( _b == null ) )
		{
			return( null );
		}
		
		BuilderNode ax = _a.getX(univ, fact);
		BuilderNode bx = _b.getX(univ, fact);
		
		if( ( ax == null ) || ( bx == null ) )
		{
			return( null );
		}
		
		return( ( bx.willChunkChld( ax ) ) && ( bx != ax ) ? bx : null );
	}

	
}

