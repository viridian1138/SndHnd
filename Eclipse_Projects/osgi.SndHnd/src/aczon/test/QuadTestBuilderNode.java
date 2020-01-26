




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

import java.util.ArrayList;
import java.util.HashMap;

import aazon.AazonEnt;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonImmutableSharedEnt;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartLine;
import aazon.AazonSmartOutlineRectangle;
import aazon.AazonSmartScale;
import aazon.AazonSmartTranslation;
import aazon.builderNode.AazonCenterVectLocationFactory;
import aazon.builderNode.BuilderNode;
import aazon.vect.AazonBaseImmutableVect;
import aczon.AczonColor;

/**
 * Test BuilderNode for a test quadrilateral used by DraggableTransformNodeTest.
 * 
 * @author tgreen
 *
 */
public class QuadTestBuilderNode extends AbstractTestBuilderNode {
	
	/**
	 * The AazonEnt for the node translated into the coordinate system of the graph.
	 */
	protected AazonEnt rectTrans;
	
	/**
	 * List of chld nodes.
	 */
	protected final ArrayList<BuilderNode> chldNodes = new ArrayList<BuilderNode>();
	
	/**
	 * Constructs the node.
	 * @param univ The universe ID of the node.
	 */
	public QuadTestBuilderNode( Object univ )
	{
		super( univ );
	}

	/**
	 * Initializes the shape.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 */
	protected void initShape( Object univ , AazonCenterVectLocationFactory fact ) {
		
		rectTrans = new AazonImmutableSharedEnt( AazonSmartTranslation.construct( 
				AazonSmartScale.construct( getRectGrpOff( univ ) , fact.getScaleVal() ) , fact.genVect( centerVect ) ) );
	}

	/**
	 * Gets the AazonEnt for the node.
	 * @param univ The universe ID for the node.
	 * @param fact Factory for producing a vector to the center of the node.
	 */
	public AazonEnt getEnt( Object univ , AazonCenterVectLocationFactory fact ) {
		init( fact );
		return( rectTrans );
	}
	
	public Object getChldNodes() {
		return( chldNodes );
	}
	
	public boolean willChunkChld( BuilderNode chld )
	{
		return( true );
	}
	
	public void chunkChld( BuilderNode chld )
	{
		chldNodes.add( chld );
	}
	
	public void removeChld()
	{
		chldNodes.clear();
	}
	
	
	/**
	 * Returns an AazonEnt for a palette button in the "on" state.
	 * @param univ The universe ID for the button.
	 * @return The AazonEnt.
	 */
	protected static AazonImmutableSharedEnt getRectGrpOn( Object univ )
	{
		if( rectGpOn.get( univ ) == null )
		{
			final AazonEnt rect = AazonSmartFilledRectangle.construct( new AazonBaseImmutableVect( -0.1 , -0.1 ) , 
					new AazonBaseImmutableVect( 0.1 , 0.1 ) , AczonColor.getFillWhite() , false );
			
			final AazonEnt rectO = AazonSmartOutlineRectangle.construct( new AazonBaseImmutableVect( -0.1 , -0.1 ) , 
					new AazonBaseImmutableVect( 0.1 , 0.1 ) , AczonColor.getLineBlack() , false );
			
			final AazonEnt liner1 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , 0.05 ) , new AazonBaseImmutableVect( 0.05 , 0.0 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt liner2 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.05 , 0.0 ) , new AazonBaseImmutableVect( 0.0 , -0.05 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt liner3 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , -0.05 ) , new AazonBaseImmutableVect( -0.05 , 0.0 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt liner4 = AazonSmartLine.construct( new AazonBaseImmutableVect( -0.05 , 0.0 ) , new AazonBaseImmutableVect( 0.0 , 0.05 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt[] entsRect = { rect , rectO , liner1 , liner2 , liner3 , liner4 };
			
			final AazonImmutableOrderedGroup rectGa = new AazonImmutableOrderedGroup( entsRect );
			
			rectGpOn.put( univ , new AazonImmutableSharedEnt( rectGa ) );
		}
		
		return( (AazonImmutableSharedEnt)( rectGpOn.get( univ ) ) );
	}
	

	/**
	 * Returns an AazonEnt for a palette button in the "off" state.
	 * @param univ The universe ID for the button.
	 * @return The AazonEnt.
	 */
	protected static AazonImmutableSharedEnt getRectGrpOff( Object univ )
	{
		if( rectGpOff.get( univ ) == null )
		{
			final AazonEnt rect = AazonSmartFilledRectangle.construct( new AazonBaseImmutableVect( -0.1 , -0.1 ) , 
					new AazonBaseImmutableVect( 0.1 , 0.1 ) , AczonColor.getFillCyan() , false );
			
			final AazonEnt rectO = AazonSmartOutlineRectangle.construct( new AazonBaseImmutableVect( -0.1 , -0.1 ) , 
					new AazonBaseImmutableVect( 0.1 , 0.1 ) , AczonColor.getLineBlack() , false );
			
			final AazonEnt liner1 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , 0.05 ) , new AazonBaseImmutableVect( 0.05 , 0.0 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt liner2 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.05 , 0.0 ) , new AazonBaseImmutableVect( 0.0 , -0.05 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt liner3 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , -0.05 ) , new AazonBaseImmutableVect( -0.05 , 0.0 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt liner4 = AazonSmartLine.construct( new AazonBaseImmutableVect( -0.05 , 0.0 ) , new AazonBaseImmutableVect( 0.0 , 0.05 ) , AczonColor.getLineMagenta() , false );
			
			final AazonEnt[] entsRect = { rect , rectO , liner1 , liner2 , liner3 , liner4 };
			
			final AazonImmutableOrderedGroup rectGa = new AazonImmutableOrderedGroup( entsRect );
			
			rectGpOff.put( univ , new AazonImmutableSharedEnt( rectGa ) );
		}
		
		return( (AazonImmutableSharedEnt)( rectGpOff.get( univ ) ) );
	}
	
	/**
	 * Map of universe IDs to AazonEnts for palette buttons in the "on" state.
	 */
	protected static HashMap<Object,AazonEnt> rectGpOn = new HashMap<Object,AazonEnt>();
	
	/**
	 * Map of universe IDs to AazonEnts for palette buttons in the "off" state.
	 */
	protected static HashMap<Object,AazonEnt> rectGpOff = new HashMap<Object,AazonEnt>();

}

