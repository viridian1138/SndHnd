




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
 * Aazon class for an immutable BuilderNode returning a buffered version of the current state captured from a mutable BuilderNode.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedImmutableBuilderNode extends AazonImmutableBuilderNode {

	/**
	 * The mutable BuilderNode.
	 */
	protected AazonBuilderNode a;
	
	/**
	 * The buffered BuilderNode state.
	 */
	protected BuilderNode x;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The mutable BuilderNode.
	 */
	private AazonBufferedImmutableBuilderNode( AazonBuilderNode _a )
	{
		a = _a;
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		if( !enabled )
		{
			x = a.getX(univ, fact);
			a = null;
			enabled = true;
		}
		
		return( x );
	}
	
	/**
	 * Constructs an immutable BuilderNode from the input.
	 * @param _a The input BuilderNode.
	 * @return An immutable BuilderNode instance.
	 */
	public static AazonImmutableBuilderNode construct( AazonBuilderNode _a )
	{
		if( _a instanceof AazonBaseImmutableBuilderNode )
		{
			return( (AazonBaseImmutableBuilderNode) _a );
		}
		
		if( _a instanceof AazonBufferedImmutableBuilderNode )
		{
			return( (AazonBufferedImmutableBuilderNode) _a );
		}
		
		return( new AazonBufferedImmutableBuilderNode( _a ) );
	}

	
}

