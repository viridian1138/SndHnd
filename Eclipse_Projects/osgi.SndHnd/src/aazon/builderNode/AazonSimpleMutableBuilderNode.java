




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
 * Aazon mutable BuilderNode representing a simple state with a "get" method and a "set" method.
 * 
 * @author tgreen
 *
 */
public class AazonSimpleMutableBuilderNode extends AazonMutableBuilderNode implements AazonImmutSetMutableBuilderNode {
	
	/**
	 * The current state of the mutable BuilderNode.
	 */
	AazonImmutableBuilderNode a;
	
	/**
	 * Constructs the mutable BuilderNode.
	 * @param _a The current state of the mutable BuilderNode.
	 */
	public AazonSimpleMutableBuilderNode( AazonImmutableBuilderNode _a )
	{
		a = _a;
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		return( a.getX(univ, fact) );
	}
	
	/**
	 * Sets the current state of the mutable BuilderNode.
	 * @param _a The current state of the mutable BuilderNode.
	 */
	public void setCoords( AazonImmutableBuilderNode _a )
	{
		a = _a;
		fire();
	}

	
}

