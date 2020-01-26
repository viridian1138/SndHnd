




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







package aczon.test;

import java.util.HashMap;

import aazon.builderNode.AazonCenterVectLocationFactory;
import aazon.builderNode.BuilderNode;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonVect;

/**
 * Abstract test BuilderNode used in DraggableTransformNodeTest.
 * 
 * @author tgreen
 *
 */
public abstract class AbstractTestBuilderNode implements BuilderNode {
	
	/**
	 * The vector to the center of the node in the coordinate system of the node graph.
	 */
	protected AazonBaseMutableVect centerVect;
	
	/**
	 * The vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 */
	protected AazonVect transformedCenterVect;
	
	/**
	 * The location of the lower-left corner of the palette button.
	 */
	protected AazonVect box0;
	
	/**
	 * The location of the upper-right corner of the palette button.
	 */
	protected AazonVect box1;
	
	/**
	 * Indicates whether the node has been initialized.
	 */
	protected boolean init = false;
	
	/**
	 * Factory for producing a vector to the center of the node.
	 */
	protected AazonCenterVectLocationFactory initScale = null;
	
	/**
	 * The universe ID for the node.
	 */
	protected Object univ;
	
	/**
	 * Constructs the node.
	 * @param _univ The universe ID for the node.
	 */
	public AbstractTestBuilderNode( Object _univ )
	{
		univ = _univ;
	}
	
	/**
	 * Initializes the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 */
	protected void init( AazonCenterVectLocationFactory fact )
	{
		if( fact != initScale )
		{
			init = false;
		}
		if( !init )
		{
			centerVect = new AazonBaseMutableVect( 0.25 , 0.25 );
			transformedCenterVect = fact.genVect( centerVect );
			box0 = AazonMutableAdditiveVect.construct( transformedCenterVect , new AazonBaseImmutableVect( -0.1 * ( fact.getScaleVal() ) , -0.1 * ( fact.getScaleVal() ) ) );
			box1 = AazonMutableAdditiveVect.construct( transformedCenterVect , new AazonBaseImmutableVect( 0.1 * ( fact.getScaleVal() ) , 0.1 * ( fact.getScaleVal() ) ) );
			initShape( univ , fact );
		}
		init = true;
	}
	
	/**
	 * Abstract method to initialize the node.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 */
	protected abstract void initShape( Object univ , AazonCenterVectLocationFactory fact );

	/**
	 * Gets the location of the lower-left corner of the palette button.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 * @return The location of the lower-left corner of the palette button.
	 */
	public AazonVect getBox0(Object univ, AazonCenterVectLocationFactory fact) {
		init( fact );
		return( box0 );
	}

	/**
	 * Gets the location of the upper-right corner of the palette button.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 * @return The location of the upper-right corner of the palette button.
	 */
	public AazonVect getBox1(Object univ, AazonCenterVectLocationFactory fact) {
		init( fact );
		return( box1 );
	}

	/**
	 * Gets the vector to the center of the node in the coordinate system of the node graph.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 * @return The vector to the center of the node in the coordinate system of the node graph.
	 */
	public AazonBaseMutableVect getCenterVect(Object univ, AazonCenterVectLocationFactory fact ) {
		init( fact );
		return( centerVect );
	}
	
	/**
	 * Gets the vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 * @return The vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 */
	public AazonVect getTransformedCenterVect(Object univ, AazonCenterVectLocationFactory fact ) {
		init( fact );
		return( transformedCenterVect );
	}
	
	/**
	 * Not supported for the text.
	 * @param context Not supported for the test.
	 */
	public void editProperties( HashMap<String,Object> context )
	{
		System.out.println( "Editing Not Supported For This Node." );
	}

}

