




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

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableSubtractVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonVect;
import aczon.AczonBoxCheck;


/**
 * Immutable node returning a BuilderNode for which the vector representing the mouse location is within the bounding box of the BuilderNode, or null if none found.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableChkBuilderNode extends AazonImmutableBuilderNode {
	
	/**
	 *  The set of BuilderNodes to check.  The BuilderNodes are presumed to be immutable.
	 */
	protected ArrayList<BuilderNode> a;
	
	/**
	 * The immutable vector representing the current mouse location.
	 */
	protected AazonImmutableVect b;
	
	/**
	 * Private constructor.
	 * @param _a The set of BuilderNodes to check.  The BuilderNodes are presumed to be immutable.
	 * @param _b The immutable vector representing the current mouse location.
	 */
	private AazonImmutableChkBuilderNode( ArrayList<BuilderNode> _a , AazonImmutableVect _b )
	{
		a = _a;
		b = _b;
	}
	
	/**
	 * Constructs an immutable node returning a BuilderNode for which the vector representing the mouse location is within the bounding box of the BuilderNode, or null if none found.
	 * @param _a The set of BuilderNodes to check.  The BuilderNodes are presumed to be immutable.
	 * @param _b The immutable vector representing the current mouse location.
	 * @return
	 */
	public static AazonImmutableBuilderNode construct( ArrayList<BuilderNode> _a , AazonImmutableVect _b )
	{
		return( new AazonImmutableChkBuilderNode( _a , _b ) );
	}

	@Override
	public BuilderNode getX(Object univ, AazonCenterVectLocationFactory fact) {
		return( performChk( univ , b , a , fact) );
	}

	/**
	 * Returns a BuilderNode for which the immutable vector representing the mouse location is within the bounding box of the BuilderNode, or null if none found.
	 * @param univ The universe ID for the nodes.
	 * @param ivect The immutable vector representing the current mouse location.
	 * @param builderNodes The set of BuilderNodes to check.  The BuilderNodes are presumed to be immutable.
	 * @param fact Factory for producing the a vector to the center location of a node.
	 * @return BuilderNode for which the vector representing the mouse location is within the bounding box of the BuilderNode, or null if none found.
	 */
	public static BuilderNode performChk( Object univ , AazonImmutableVect ivect , ArrayList<BuilderNode> builderNodes , AazonCenterVectLocationFactory fact )
	{
		final int sz = builderNodes.size();
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			BuilderNode bn = builderNodes.get( count );
			if( AczonBoxCheck.isInside( ivect , bn.getBox0(univ, fact) , bn.getBox1(univ, fact) ) )
			{
				return( bn );
			}
		}
		return( null );
	}

	
}

