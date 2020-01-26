




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
 * Aazon abstract class for a mutable BuilderNode representing a state, with a "get" method and a "set" method, and an abstract method for defining the initial state.
 * 
 * @author tgreen
 *
 */
public abstract class AazonInnerMutableBuilderNode extends AazonMutableBuilderNode implements AazonCoordSetMutableBuilderNode {
	
	/**
	 * The current state of the BuilderNode.
	 */
	protected BuilderNode x;
	
	/**
	 * Constructs the class.
	 */
	public AazonInnerMutableBuilderNode( )
	{
		x = getInnerX();
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		return( x );
	}
	
	/**
	 * Sets the current value of the mutable state.
	 * @param _a The current value of the mutable state.
	 */
	public void setCoords( BuilderNode _x )
	{
		x = _x;
		fire();
	}
	
	/**
	 * Gets the initial state of the BuilderNode.
	 * @return The initial state of the BuilderNode.
	 */
	protected abstract BuilderNode getInnerX();

}

