




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

import java.util.HashMap;

import aazon.AazonEnt;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonVect;

/**
 * An Aazon builder node.
 * 
 * @author tgreen
 *
 */
public interface BuilderNode {
	
	/**
	 * Gets the AazonEnt for displaying the node.
	 * @param univ The universe ID.
	 * @param fact Factory for generating vectors to the center point of a node.
	 * @return The AazonEnt for displaying the node.
	 */
	public abstract AazonEnt getEnt(Object univ, AazonCenterVectLocationFactory fact );
	
	/**
	 * Gets the set of child nodes.
	 * @return The set of child nodes.
	 */
	public abstract Object getChldNodes();
	
	/**
	 * Gets the vector to the center of the node in the coordinate system of the node graph.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 * @return The vector to the center of the node in the coordinate system of the node graph.
	 */
	public abstract AazonBaseMutableVect getCenterVect(Object univ, AazonCenterVectLocationFactory fact );
	
	/**
	 * Gets the vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 * @return The vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 */
	public abstract AazonVect getTransformedCenterVect(Object univ, AazonCenterVectLocationFactory fact );
	
	/**
	 * Gets the vector to the lower-left corner of the node.
	 * @param univ The universe ID.
	 * @param fact Factory for generating vectors to the center point of a node.
	 * @return The vector to the lower-left corner of the node.
	 */
	public abstract AazonVect getBox0(Object univ, AazonCenterVectLocationFactory fact );
	
	/**
	 * Gets the vector to the upper-right corner of the node.
	 * @param univ The universe ID.
	 * @param fact Factory for generating vectors to the center point of a node.
	 * @return The vector to the upper-right corner of the node.
	 */
	public abstract AazonVect getBox1(Object univ, AazonCenterVectLocationFactory fact );
	
	/**
	 * Attempts to accept the input node as a child.
	 * @param chld The potential child.
	 */
	public abstract void chunkChld( BuilderNode chld );
	
	/**
	 * Returns whether the node will accept the input node as its child.
	 * @param chld The potential child.
	 * @return Whether the node will accept the input node as its child.
	 */
	public abstract boolean willChunkChld( BuilderNode chld );
	
	/**
	 * Displays the property editor for the node.
	 * @param context The context for displaying the property editor.
	 * @throws Throwable
	 */
	public abstract void editProperties( HashMap<String,Object> context ) throws Throwable;
	
	/**
	 * Removes the children of the node.
	 */
	public abstract void removeChld();

	
}

