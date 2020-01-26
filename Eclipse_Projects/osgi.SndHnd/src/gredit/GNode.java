




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







package gredit;

import java.awt.Font;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;

import aazon.AazonEnt;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonImmutableSharedEnt;
import aazon.AazonSmartBlockText;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartLine;
import aazon.AazonSmartOutlineRectangle;
import aazon.AazonSmartScale;
import aazon.AazonSmartTranslation;
import aazon.builderNode.AazonCenterVectLocationFactory;
import aazon.builderNode.BuilderNode;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonVect;
import aczon.AczonColor;

/**
 * A node representing an object.
 * 
 * @author tgreen
 *
 */
public abstract class GNode implements BuilderNode , Externalizable {

	/**
	 * Map from the universe ID to the AazonEnt for the node translated into the coordinate system of the graph.
	 */
	protected WeakHashMap<Object,AazonEnt> rectTrans = new WeakHashMap<Object,AazonEnt>();

	/**
	 * The vector to the center of the node in the coordinate system of the node graph.
	 */
	protected AazonBaseMutableVect centerVect;
	
	/**
	 * Map from the universe ID to the vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 */
	protected WeakHashMap<Object,AazonVect> transformedCenterVectMap = new WeakHashMap<Object,AazonVect>();

	/**
	 * Map from the universe ID to the location of the lower-left corner of the palette button.
	 */
	protected WeakHashMap<Object,AazonVect> box0Map = new WeakHashMap<Object,AazonVect>();
	
	/**
	 * Map from the universe ID to the location of the upper-right corner of the palette button.
	 */
	protected WeakHashMap<Object,AazonVect> box1Map = new WeakHashMap<Object,AazonVect>();
	
	/**
	 * Map from the universe ID to a factory for producing a vector to the center of the node.
	 */
	protected WeakHashMap<Object,AazonCenterVectLocationFactory> scaleValMap = new WeakHashMap<Object,AazonCenterVectLocationFactory>();

	/**
	 * Indicates whether the node has been initialized.
	 */
	protected boolean init = false;

	
	/**
	 * Initializes the node.
	 * @param univ The Universe ID.
	 * @param fact Factory for producing a vector to the center of the node.
	 */
	protected void init(Object univ, AazonCenterVectLocationFactory fact ) {
		if (!init) {
			if( centerVect == null )
			{
				centerVect = new AazonBaseMutableVect(0.25, 0.25);
			}
		}
		if( rectTrans.get(univ) == null )
		{
			initShape(univ, fact );
		}
		AazonCenterVectLocationFactory sv = scaleValMap.get( univ );
		if( fact != sv )
		{
			initShape(univ, fact);
		}
		init = true;
	}

	/**
	 * Initializes the shape.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 */
	protected void initShape(Object univ, AazonCenterVectLocationFactory fact ) {
		
		final AazonEnt e1 = getRectGrpOff(univ);
		
		final AazonEnt txt0 = AazonSmartBlockText.construct( new AazonBaseImmutableVect( 0.0 , 0.1 ) , 
				getName(), AczonColor.getTextGreen(), "Helvetica", 8, Font.PLAIN, false);
		
		final AazonEnt[] entsRect = { e1 , txt0 };

		final AazonImmutableOrderedGroup rectGa = new AazonImmutableOrderedGroup(
				entsRect);
		
		final double scaleVal = fact.getScaleVal();
		
		final AazonEnt rectGa2 = AazonSmartScale.construct( rectGa , scaleVal );
		
		final AazonVect cvect = fact.genVect( centerVect );
		
		transformedCenterVectMap.put( univ , cvect );

		rectTrans.put( univ ,  new AazonImmutableSharedEnt(AazonSmartTranslation
				.construct(rectGa2, cvect)) );
		
		box0Map.put( univ , AazonMutableAdditiveVect.construct(cvect,
				new AazonBaseImmutableVect(-0.1 * scaleVal, -0.1 * scaleVal) ) );
		
		box1Map.put( univ , AazonMutableAdditiveVect.construct(cvect,
				new AazonBaseImmutableVect(0.1 * scaleVal, 0.1 * scaleVal) ) );
		
		scaleValMap.put( univ , fact );
		
	}

	/**
	 * Gets the AazonEnt for the node.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 */
	public AazonEnt getEnt(Object univ, AazonCenterVectLocationFactory fact ) {
		init(univ, fact );
		return ( rectTrans.get(univ) );
	}

	/**
	 * Attempts to assign a child to a node.
	 * @param in The child to assign.
	 */
	public abstract void performAssign(GNode in);

	/**
	 * Returns whether the a child is assignment-compatible for a node.
	 * @param in The potential child.
	 * @return Whether the a child is assignment-compatible for a node.
	 */
	public abstract boolean isAssignCompatible(GNode in);
	
	/**
	 * Removes the children of the node.
	 */
	public abstract void removeChld();

	/**
	 * Gets the name of the node.
	 * @return The name of the node.
	 */
	public abstract String getName();

	/**
	 * Generates an instance of the object represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the object represented by this node.
	 */
	public abstract Object genObj(HashMap<GNode,Object> s);

	/**
	 * Attempts to accept the input node as a child.
	 * @param chld The potential child.
	 */
	public void chunkChld(BuilderNode chld) {
		performAssign((GNode) chld);
	}

	/**
	 * Returns whether the node will accept the input node as its child.
	 * @param chld The potential child.
	 * @return Whether the node will accept the input node as its child.
	 */
	public boolean willChunkChld(BuilderNode chld) {
		return (isAssignCompatible((GNode) chld));
	}
	
	/**
	 * Gets the vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 * @return The vector to the center of the node in the coordinate system in which the node graph has been translated in the window using GUI tools.
	 */
	public AazonVect getTransformedCenterVect(Object univ, AazonCenterVectLocationFactory fact ) {
		init(univ, fact);
		return( transformedCenterVectMap.get( univ ) );
	}

	/**
	 * Gets the location of the lower-left corner of the palette button.
	 * @param univ The universe ID.
	 * @param Factory for producing a vector to the center of the node.
	 * @return The location of the lower-left corner of the palette button.
	 */
	public AazonVect getBox0(Object univ, AazonCenterVectLocationFactory fact ) {
		init(univ, fact);
		return( box0Map.get( univ ) );
	}

	/**
	 * Gets the location of the upper-right corner of the palette button.
	 * @param univ The universe ID.
	 * @param Factory for producing a vector to the center of the node.
	 * @return The location of the upper-right corner of the palette button.
	 */
	public AazonVect getBox1(Object univ, AazonCenterVectLocationFactory fact ) {
		init(univ, fact);
		return( box1Map.get( univ ) );
	}

	/**
	 * Gets the vector to the center of the node in the coordinate system of the node graph.
	 * @param univ The universe ID.
	 * @param Factory for producing a vector to the center of the node.
	 * @return The vector to the center of the node in the coordinate system of the node graph.
	 */
	public AazonBaseMutableVect getCenterVect(Object univ, AazonCenterVectLocationFactory fact ) {
		init(univ, fact);
		return (centerVect);
	}
	
	/**
	 * Displays the property editor for the node.
	 * @param context The context for displaying the property editor.
	 * @throws Throwable
	 */
	public void editProperties( HashMap<String,Object> context ) throws Throwable
	{
		System.out.println( "Editing Not Supported For This Node." );
	}
	
	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		myv.setDouble("CenterVectX", centerVect.getX());
		myv.setDouble("CenterVectY", centerVect.getY());

		out.writeObject(myv);
	}

	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			final double x = myv.getDouble( "CenterVectX" );
			final double y = myv.getDouble( "CenterVectY" );
			if( centerVect == null )
			{
				centerVect = new AazonBaseMutableVect(0.25, 0.25);
			}
			centerVect.setCoords(x, y);

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Returns an AazonEnt for a palette button in the "on" state.
	 * @param univ The universe ID for the button.
	 * @return The AazonEnt.
	 */
	public static AazonImmutableSharedEnt getRectGrpOn(Object univ) {
		if (rectGpOn.get(univ) == null) {
			final AazonEnt rect = AazonSmartFilledRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getFillWhite(), false);

			final AazonEnt rectO = AazonSmartOutlineRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getLineBlack(), false);

			final AazonEnt liner1 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.0, 0.0),
					new AazonBaseImmutableVect(0.05, 0.0), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner2 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.05, 0.0),
					new AazonBaseImmutableVect(0.05, 0.05), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner3 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.05, 0.05),
					new AazonBaseImmutableVect(0.0, 0.0), AczonColor
							.getLineMagenta(), false);

			final AazonEnt[] entsRect = { rect, rectO, liner1, liner2, liner3 };

			final AazonImmutableOrderedGroup rectGa = new AazonImmutableOrderedGroup(
					entsRect);

			rectGpOn.put(univ, new AazonImmutableSharedEnt(rectGa));
		}

		return ( rectGpOn.get(univ) );
	}

	
	/**
	 * Returns an AazonEnt for a palette button in the "off" state.
	 * @param univ The universe ID for the button.
	 * @return The AazonEnt.
	 */
	public static AazonImmutableSharedEnt getRectGrpOff(Object univ) {
		if (rectGpOff.get(univ) == null) {
			final AazonEnt rect = AazonSmartFilledRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getFillCyan(), false);

			final AazonEnt rectO = AazonSmartOutlineRectangle.construct(
					new AazonBaseImmutableVect(-0.1, -0.1),
					new AazonBaseImmutableVect(0.1, 0.1), AczonColor
							.getLineBlack(), false);

			final AazonEnt liner1 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.0, 0.0),
					new AazonBaseImmutableVect(0.05, 0.0), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner2 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.05, 0.0),
					new AazonBaseImmutableVect(0.05, 0.05), AczonColor
							.getLineMagenta(), false);

			final AazonEnt liner3 = AazonSmartLine.construct(
					new AazonBaseImmutableVect(0.05, 0.05),
					new AazonBaseImmutableVect(0.0, 0.0), AczonColor
							.getLineMagenta(), false);

			final AazonEnt[] entsRect = { rect, rectO, liner1, liner2, liner3 };

			final AazonImmutableOrderedGroup rectGa = new AazonImmutableOrderedGroup(
					entsRect);

			rectGpOff.put(univ, new AazonImmutableSharedEnt(rectGa));
		}

		return ( rectGpOff.get(univ) );
	}

	/**
	 * Map of universe IDs to AazonEnts for palette buttons in the "on" state.
	 */
	protected static WeakHashMap<Object,AazonImmutableSharedEnt> rectGpOn = new WeakHashMap<Object,AazonImmutableSharedEnt>();

	/**
	 * Map of universe IDs to AazonEnts for palette buttons in the "off" state.
	 */
	protected static WeakHashMap<Object,AazonImmutableSharedEnt> rectGpOff = new WeakHashMap<Object,AazonImmutableSharedEnt>();

	
}

