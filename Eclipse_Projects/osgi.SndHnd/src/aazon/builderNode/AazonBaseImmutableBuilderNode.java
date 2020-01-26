




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
 * A base class for an Aazon immutable BuilderNode.
 * 
 * @author tgreen
 *
 */
public class AazonBaseImmutableBuilderNode extends AazonImmutableBuilderNode {
	
	/**
	 * The BuilderNode state.  It is assumed that the internals of this state will not change.
	 */
	BuilderNode x;
	
	/**
	 * Constructs the Aazon immutable BuilderNode.  It is assumed that the internals of this state will not change.
	 * @param _x The BuilderNode state.
	 */
	public AazonBaseImmutableBuilderNode( BuilderNode _x )
	{
		x = _x;
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		return( x );
	}

	
}

