




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
 * An Aazon mutable BuilderNode that buffers the state of an input mutable BuilderNode that requires more computational work to evaluate.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedMutableBuilderNode extends AazonMutableBuilderNode implements AazonListener {
	
	/**
	 * The input mutable BuilderNode.
	 */
	protected AazonMutableBuilderNode a;
	
	/**
	 * The current state of the mutable BuilderNode.
	 */
	protected BuilderNode x;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The input mutable BuilderNode.
	 */
	private AazonBufferedMutableBuilderNode( AazonMutableBuilderNode _a )
	{
		a = _a;
		a.add( this );
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		if( !enabled )
		{
			x = a.getX(univ, fact);
			enabled = true;
		}
		
		return( x );
	}
	
	/**
	 * Handles a change to the input BuilderNode by invalidating the buffer, and then firing events.
	 */
	public void handleListen()
	{
		enabled = false;
		fire();
	}
	
	/**
	 * Returns an Aazon BuilderNode that buffers the state of an input BuilderNode that requires more computational work to evaluate.
	 * @param _a The input BuilderNode.
	 * @return The Aazon BuilderNode that buffers the state of an input BuilderNode that requires more computational work to evaluate.
	 */
	public static AazonBuilderNode construct( AazonBuilderNode _a )
	{
		if( _a instanceof AazonImmutableBuilderNode )
		{
			return( AazonBufferedImmutableBuilderNode.construct( _a ) );
		}
		
		return( new AazonBufferedMutableBuilderNode( (AazonMutableBuilderNode) _a ) );
	}

	
}

