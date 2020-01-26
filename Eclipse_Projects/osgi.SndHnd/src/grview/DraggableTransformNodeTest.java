




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







package grview;

import gredit.GNode;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import palettes.IPalette;
import verdantium.ProgramDirector;
import verdantium.undo.UndoManager;
import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonListener;
import aazon.AazonMutableGroup;
import aazon.AazonSmartLine;
import aazon.AazonSmartOutlineRectangle;
import aazon.AazonSmartToggle;
import aazon.AazonSmartTranslation;
import aazon.bool.AazonBool;
import aazon.builderNode.AazonBaseImmutableBuilderNode;
import aazon.builderNode.AazonBuilderNode;
import aazon.builderNode.AazonCenterVectLocationFactory;
import aazon.builderNode.AazonDefaultCenterVectLocationFactory;
import aazon.builderNode.AazonImmutableChkBuilderNode;
import aazon.builderNode.AazonMutableBuilderBool;
import aazon.builderNode.AazonMutableChkBuilderNode;
import aazon.builderNode.AazonMutableDchkBuilderNode;
import aazon.builderNode.AazonTransChld;
import aazon.builderNode.BuilderNode;
import aazon.intg.AazonBaseMutableInt;
import aazon.intg.AazonInt;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableSubtractVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonMutableSubtractVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;
import abzon.AbzonCoordSetEvent;
import aczon.AczonColor;
import aczon.AczonCoordinateConvert;
import aczon.AczonMouseDragVect;
import aczon.AczonResizeVect;
import aczon.AczonResizeVectFactory;
import aczon.AczonRootFactory;
import aczon.AczonSmartConnectionLine;
import core.InstrumentTrack;

/**
 * Editor for the graphical manipulation of BuilderNodes.
 * 
 * This class was influenced by several previous systems with similar capabilities.
 * One of them was LabView (see https://en.wikipedia.org/wiki/LabVIEW).  Another
 * was former Arizona State University professor Tony Faustini's Visual Java
 * system from the mid-1990s, which was a visual editor for elements in the Lucid
 * programming language inspired by Edward Ashcroft's work on operator nets.  Another 
 * influence was PureData (see https://en.wikipedia.org/wiki/Pure_Data) and Reaktor
 * (see https://en.wikipedia.org/wiki/Reaktor).  I've also been told there's
 * a program called Studio Max that is like a commercial version of PureData, and a
 * program by Autodesk called Hyperwire with similarities to Faustini's Visual Java.
 * And of course, there was a program called Prograph CPX 
 * (see https://en.wikipedia.org/wiki/Prograph). And SPSS Clementine
 * (see https://en.wikipedia.org/wiki/SPSS_Clementine).
 * 
 * PureData and Reaktor were influences in the field of music, although
 * they seems more like real-time effects processors rather than a 
 * music composition system like SndHnd.
 * 
 * Both SndHnd and PureData use minimalist visual interfaces that allow large numbers
 * of nodes can be displayed.
 * 
 * @author tgreen
 *
 */
public class DraggableTransformNodeTest implements MouseListener,
		MouseMotionListener {
	
	/**
	 * The universe ID.
	 */
	protected Object univ;
	
	/**
	 * Set of BuilderNodes to be edited.
	 */
	protected HashSet<BuilderNode> nodeSet;

	/**
	 * The root factory.
	 */
	protected AczonRootFactory af;

	/**
	 * Vector indicating the mouse position of the current mouse-drag operation.
	 */
	protected AczonMouseDragVect dragVect = null;
	
	/**
	 * Indicates whether a mouse-drag operation is ongoing.
	 */
	protected boolean dragging = false;

	/**
	 * Aczon vector representing the resizing of the window.
	 */
	AazonMutableVect resizeVect;
	
	/**
	 * Vector indicating the global coordinate transform.
	 */
	AazonBaseMutableVect gloTransform;
	
	/**
	 * The group of AazonEnts containing the frontal pane for controls.
	 */
	AazonMutableGroup frontPaneGroup;
	
	/**
	 * The group of AazonEnts containing connection lines.
	 */
	AazonMutableGroup lineGroup;
	
	/**
	 * The group of AazonEnts containing the rubber-band line for the zoom operation.
	 */
	AazonMutableGroup zoomGroup;

	/**
	 * Separate ArrayList of BuilderNodes to be edited.
	 */
	ArrayList<BuilderNode> builderNodes;
	
	/**
	 * Group of AazonEnts containing the nodes.
	 */
	AazonMutableGroup nodeGroup;

	/**
	 * The current "system mode" for editing.
	 */
	AazonInt sysMode;
	
	/**
	 * The instrument track in which the nodes exist, or null if no instrument track.
	 */
	InstrumentTrack track;
	
	/**
	 * The undo manager for performing undo.
	 */
	UndoManager undoMgr;
	
	/**
	 * Interface defining accessors to state information for a GUI palette.
	 */
	IPalette palette;
	

	/**
	 * The allocated singleton of the DraggableTransformNodeTest.
	 */
	protected static DraggableTransformNodeTest dr;

	/**
	 * Allocated singleton for the AazonEnt to be displayed.
	 */
	protected static AazonEnt drEnt;
	

	/**
	 * Handles the rebuilding of groups.
	 * Created to work around a J3D bug.
	 */
	protected void handleRebuildGroups() {
		// modifyLineGroup( builderNodes , lineGroup );
		modifyGroup(univ,builderNodes, nodeGroup, getFact());
	}

	/**
	 * Handles the presence of the mouse inside the rectangle of one of the nodes for dragging.
	 * @param x The X-coordinate of the mouse event.
	 * @param y The Y-coordinate of the mouse event.
	 * @param centerVect The vector to the center of the selected node.
	 * @param offsetVect The offset vector from the center defined by the original mouse click.
	 */
	protected void handleMouseInsideBox(final int x, final int y,
			final AazonBaseMutableVect centerVect,
			final AazonImmutableVect offsetVect) {
		dragVect = new AczonMouseDragVect() {
			
			@Override
			protected double getInnerX() {
				return (x);
			}

			@Override
			protected double getInnerY() {
				return (y);
			}

			@Override
			protected void handleListenerEnd() {
				dragVect = null;
				((AbzonCoordSetEvent) (setEvents[0])).disable();
			}
		};

		final AazonVect cvect = AczonCoordinateConvert.construct(dragVect,
				resizeVect, af);

		final AazonVect svect = AazonMutableSubtractVect.construct(cvect,
				gloTransform);

		final AazonVect ovect = AazonMutableAdditiveVect.construct(svect,
				offsetVect);
		
		final AazonVect sclVect = getMutableInvScalingVect( ovect );

		if (!(sclVect instanceof AazonMutableVect)) {
			throw (new RuntimeException("Inconsistent"));
		}

		final AbzonCoordSetEvent acse = new AbzonCoordSetEvent(
				(AazonMutableVect) sclVect, centerVect);

		final Object[] asetEvents = { acse };

		dragVect.setSetEvents(asetEvents);
	}

	
	/**
	 * Handles the start of a mouse-drag operation in hand-mode.
	 * @param x The X-coordinate of the mouse event.
	 * @param y The Y-coordinate of the mouse event.
	 * @param offsetVect The coordinate offset for the mouse-drag operation.
	 */
	protected void handleMouseHand(final int x, final int y,
			final AazonImmutableVect offsetVect) {
		dragVect = new AczonMouseDragVect() {
			
			@Override
			protected double getInnerX() {
				return (x);
			}

			@Override
			protected double getInnerY() {
				return (y);
			}

			@Override
			protected void handleListenerEnd() {
				dragVect = null;
				((AbzonCoordSetEvent) (setEvents[0])).disable();
				handleRebuildGroups();
			}
			
		};

		final AazonVect cvect = AczonCoordinateConvert.construct(dragVect,
				resizeVect, af);

		final AazonVect ovect = AazonMutableAdditiveVect.construct(cvect,
				offsetVect);

		if (!(ovect instanceof AazonMutableVect)) {
			throw (new RuntimeException("Inconsistent"));
		}

		final AbzonCoordSetEvent acse = new AbzonCoordSetEvent(
				(AazonMutableVect) ovect, gloTransform);

		final Object[] asetEvents = { acse };

		dragVect.setSetEvents(asetEvents);
	}

	
	/**
	 * Handles the presence of the mouse inside the rectangle of one of the nodes for connect-mode.
	 * @param x The X-coordinate of the mouse event.
	 * @param y The Y-coordinate of the mouse event.
	 * @param strtVect The starting point of the connection line in the coordinate system of the frontPaneGroup.
	 * @param strtNode The node from which the connect operation was initiated.
	 */
	protected void handleMouseConnect(final int x, final int y,
			final AazonVect strtVect, final BuilderNode strtNode) {
		dragVect = new AczonMouseDragVect() {
			
			@Override
			protected double getInnerX() {
				return (x);
			}

			@Override
			protected double getInnerY() {
				return (y);
			}

			@Override
			protected void handleListenerEnd() {
				dragVect = null;
				AazonImmutableGroup gp = new AazonImmutableGroup();
				((AazonMutableGroup) (setEvents[0])).setGrp(gp);
				BuilderNode strt = (BuilderNode) (setEvents[1]);
				AazonBuilderNode endA = (AazonBuilderNode) (setEvents[2]);
				BuilderNode end = endA.getX(univ,getFact());
				if (end != null) {
					end.chunkChld(strt);
					ArrayList<BuilderNode> nodes = (ArrayList<BuilderNode>) (setEvents[3]);
					AazonMutableGroup lineGroup = (AazonMutableGroup) (setEvents[4]);
					DraggableTransformNodeTest
							.modifyLineGroup(univ, nodes, lineGroup, getFact());
					final int core = 0;
					System.out.println( track );
					if( track != null )
					{
						try
						{
							track.updateTrackFrames(core);
						}
						catch( Throwable ex )
						{
							ex.printStackTrace( System.out );
						}
					}
				}
			}
		};

		final AazonVect cvect = AczonCoordinateConvert.construct(dragVect,
				resizeVect, af);

		final AazonVect svect = AazonMutableSubtractVect.construct(cvect,
				gloTransform);

		if (!(svect instanceof AazonMutableVect)) {
			throw (new RuntimeException("Inconsistent"));
		}

		final AazonEnt lnOff = AazonSmartLine.construct(strtVect, svect,
				AczonColor.getLineRed(), false);

		final AazonEnt lnOn = AazonSmartLine.construct(strtVect, svect,
				AczonColor.getLineYellow(), false);

		final AazonBuilderNode chk = AazonMutableChkBuilderNode.construct(
				builderNodes, svect);

		final AazonBuilderNode dchk = AazonMutableDchkBuilderNode.construct(
				univ,new AazonBaseImmutableBuilderNode(strtNode), chk, getFact());

		final AazonBool selectBool = AazonMutableBuilderBool.construct(univ,dchk,getFact());

		final AazonEnt ln = AazonSmartToggle.construct(lnOn, lnOff, selectBool);

		final AazonImmutableGroup gp = new AazonImmutableGroup(ln);

		frontPaneGroup.setGrp(gp);

		final Object[] asetEvents = { frontPaneGroup, strtNode, dchk,
				builderNodes, lineGroup };

		dragVect.setSetEvents(asetEvents);
	}
	
	
	/**
	 * Handles the beginning of a mouse-zoom operation.
	 * @param x The X-coordinate of the mouse event.
	 * @param y The Y-coordinate of the mouse event.
	 */
	protected void handleMouseZoom(final int x, final int y,
			final AazonVect strtVect, final boolean zoomX, final boolean zoomY) {
		dragVect = new AczonMouseDragVect() {
			
			@Override
			protected double getInnerX() {
				return (x);
			}

			@Override
			protected double getInnerY() {
				return (y);
			}

			@Override
			protected void handleListenerEnd() {
				dragVect = null;
				AazonImmutableGroup gp = new AazonImmutableGroup();
				((AazonMutableGroup) (setEvents[0])).setGrp(gp);
				AazonVect strt = (AazonVect) (setEvents[1]);
				AazonVect end = (AazonVect) (setEvents[2]);
				Boolean zmx = (Boolean) (setEvents[3]);
				Boolean zmy = (Boolean) (setEvents[4]);
				DraggableTransformNodeTest.this.performZoom(strt.getX(), strt.getY(), end.getX(), end.getY(), zmx, zmy);
			}
		};

		final AazonVect cvect = AczonCoordinateConvert.construct(dragVect,
				resizeVect, af);

		final AazonEnt controlPoint = AazonSmartOutlineRectangle.construct( strtVect , cvect , AczonColor.getLineOrange() , false );
		
		zoomGroup.setGrp( new AazonImmutableGroup( controlPoint ) );

		if (!(cvect instanceof AazonMutableVect)) {
			throw (new RuntimeException("Inconsistent"));
		}

		final Object[] asetEvents = { zoomGroup , strtVect , cvect , new Boolean( zoomX ) , new Boolean( zoomY ) };

		dragVect.setSetEvents(asetEvents);
	}
	

	/**
	 * Handles the beginning of a mouse-drag operation.
	 * @param x The X-coordinate of the mouse event.
	 * @param y The Y-coordinate of the mouse event.
	 */
	protected void handleMouseDragBegin(final int x, final int y) {
		final AazonImmutableVect bvect = AczonCoordinateConvert.convertCoords(
				x, y, resizeVect, af);
		final AazonImmutableVect ivect = AazonImmutableSubtractVect.construct(
				bvect, AazonBufferedImmutableVect.construct(gloTransform));
		final int mode = sysMode.getX();
		switch (mode) {
		case DraggableTestPalette.MOVE_MODE: {
			BuilderNode bn = AazonImmutableChkBuilderNode.performChk(univ,ivect,
					builderNodes,getFact());
			if (bn != null) {
				final AazonImmutableVect offsetVect = AazonImmutableSubtractVect
						.construct(new AazonBaseImmutableVect(bn
								.getTransformedCenterVect(univ,getFact()).getX(), bn.getTransformedCenterVect(univ,getFact())
								.getY()), ivect);
				handleMouseInsideBox(x, y, bn.getCenterVect(univ,getFact()), offsetVect);
				return;
			}
		}
			break;

		case DraggableTestPalette.CONNECT_MODE: {
			BuilderNode bn = AazonImmutableChkBuilderNode.performChk(univ,ivect,
					builderNodes,getFact());
			if (bn != null) {
				handleMouseConnect(x, y, ivect, bn);
				return;
			}
		}
			break;

		case DraggableTestPalette.HAND_MODE: {
			final AazonImmutableVect offsetVect = AazonImmutableSubtractVect
					.construct(new AazonBaseImmutableVect(gloTransform.getX(),
							gloTransform.getY()), bvect);
			handleMouseHand(x, y, offsetVect);
		}
			break;
			
			
		case DraggableTestPalette.ZOOM_MODE: {
			handleMouseZoom( x , y , bvect , true , true );
		}
			break;
			
			
		case DraggableTestPalette.ZOOM_X_MODE: {
			handleMouseZoom( x , y , bvect , true , false );
		}
			break;
			
			
		case DraggableTestPalette.ZOOM_Y_MODE: {
			handleMouseZoom( x , y , bvect , false , true );
		}
			break;
			
			
		case DraggableTestPalette.UNZOOM_MODE: {
			handleUnzoom();
		}
			break;
			
		case DraggableTestPalette.NEW_MODE: {
			NodeCreationEditor editor = new NodeCreationEditor( this , ivect , palette );
			ProgramDirector.showPropertyEditor(editor, null, "Node Creation");
		}
			break;
			
		case DraggableTestPalette.PROPERTIES_MODE: {
			BuilderNode bn = AazonImmutableChkBuilderNode.performChk(univ,ivect,
					builderNodes,getFact());
			if (bn != null) {
				HashMap<String,Object> context = new HashMap<String,Object>();
				context.put("InstrumentTrack", track);
				context.put("UndoMgr", undoMgr);
				try
				{
					bn.editProperties(context);
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
				return;
			}
		}
			break;
			
		case DraggableTestPalette.DEL_CHLD_MODE: {
			BuilderNode bn = AazonImmutableChkBuilderNode.performChk(univ,ivect,
					builderNodes,getFact());
			if (bn != null) {
				bn.removeChld();
				DraggableTransformNodeTest.modifyLineGroup(univ,builderNodes, lineGroup, getFact());
				final int core = 0;
				System.out.println( track );
				if( track != null )
				{
					try
					{
						track.updateTrackFrames(core);
					}
					catch( Throwable ex )
					{
						ex.printStackTrace( System.out );
					}
				}
				return;
			}
		}
			break;

		}

	}
	
	
	/**
	 * Handles the creation of a node.
	 * @param cl The class of the node to create.
	 * @param ivect The vector to the center of the node to be created.
	 */
	public void handleCreation( Class<? extends GNode> cl , AazonImmutableVect ivect )
	{
		try
		{
			GNode node = cl.newInstance();
			node.getCenterVect(univ,getFact()).setCoords(ivect.getX(), ivect.getY());
			builderNodes.add(node);
			modifyGroup(univ,builderNodes, nodeGroup, getFact());
			nodeSet.add( node );
		}
		catch( Throwable ex )
		{
			ex.printStackTrace(System.out);
		}
	}
	
	
	/**
	 * Handles a request to perform a zoom.
	 * @param xa1 The start X-axis location of the zoom.
	 * @param ya1 The start Y-axis location of the zoom.
	 * @param xa2 The end X-axis location of the zoom.
	 * @param ya2 The end Y-axis location of the zoom.
	 * @param zoomX Whether to zoom on the X-axis.
	 * @param zoomY Whether to zoom on the Y-axis.
	 */
	public void performZoom( final double xa1 , final double ya1 , final double xa2 , final double ya2 , final boolean zoomX , final boolean zoomY )
	{
		final double x1 = Math.min(xa1, xa2);
		final double x2 = Math.max(xa1, xa2);
		final double y1 = Math.min(ya1, ya2);
		final double y2 = Math.max(ya1, ya2);
		
		final double xb1 = ( x1 + 1.0 ) / 2.0;
		final double xb2 = ( x2 + 1.0 ) / 2.0;
		final double yb1 = ( y1 + 1.0 ) / 2.0;
		final double yb2 = ( y2 + 1.0 ) / 2.0;
		
		final double xc1 = ( 1.0 - xb1 ) * startX + xb1 * endX;
		final double xc2 = ( 1.0 - xb2 ) * startX + xb2 * endX;
		final double yc1 = ( 1.0 - yb1 ) * startY + yb1 * endY;
		final double yc2 = ( 1.0 - yb2 ) * startY + yb2 * endY;
		
		if( zoomX )
		{
			startX = xc1;
			endX = xc2;
		}
		
		if( zoomY )
		{
			startY = yc1;
			endY = yc2;
		}
		
		double ax = ( startX + endX ) / 2.0;
		double ay = ( startY + endY ) / 2.0;
		
		final double svx = ( endX - startX ) / 2.0;
		final double svy = ( endY - startY ) / 2.0;
		
		double bx = ax / svx;
		double by = ay / svy;
		gloTransform.setCoords( -bx , -by );
		
		fact = null;
		
		handleRebuildGroups();
		modifyLineGroup(univ, builderNodes, lineGroup, getFact());
	}
	
	/**
	 * Handles a request to un-zoom.
	 */
	public void handleUnzoom()
	{
		double stX = 1e+9;
		double edX = -1e+9;
		double stY = 1e+9;
		double edY = -1e+9;
		
		Iterator<BuilderNode> it = builderNodes.iterator();
		
		if( !( it.hasNext() ) )
		{
			return;
		}
		
		while( it.hasNext() )
		{
			BuilderNode bd = it.next();
			AazonVect box0 = bd.getBox0(bd, AazonDefaultCenterVectLocationFactory.getFact());
			AazonVect box1 = bd.getBox1(bd, AazonDefaultCenterVectLocationFactory.getFact());
			stX = Math.min( stX , box0.getX() );
			edX = Math.max( edX , box1.getX() );
			stY = Math.min( stY , box0.getY() );
			edY = Math.max( edY , box1.getY() );
		}
		
		startX = stX - 0.1;
		endX = edX - 0.1;
		startY = stY + 0.1;
		endY = edY + 0.1;
		
		double ax = ( startX + endX ) / 2.0;
		double ay = ( startY + endY ) / 2.0;
		
		final double svx = ( endX - startX ) / 2.0;
		final double svy = ( endY - startY ) / 2.0;
		
		double bx = ax / svx;
		double by = ay / svy;
		gloTransform.setCoords( -bx , -by );
		
		fact = null;
		
		handleRebuildGroups();
		modifyLineGroup(univ, builderNodes, lineGroup, getFact());
	}
	
	
	/**
	 * Gets the mutable scaling vect scaling from global coordinates to local coordinates.
	 * @param _a The global coordinate vector.
	 * @return The mutable scaled local coordinate vector.
	 */
	protected AazonVect getMutableScalingVect( final AazonVect _a )
	{
		final double svx = ( endX - startX ) / 2.0;
		final double svy = ( endY - startY ) / 2.0;
		final AazonBaseImmutableVect vct = new AazonBaseImmutableVect( 1.0 / svx , 1.0 / svy );
		return( aazon.vect.AazonMutableScalingVect.construct( _a , vct ) );
	}
	
	/**
	 * Gets the mutable scaling vect scaling from local coordinates to global coordinates.
	 * @param _a The local coordinate vector.
	 * @return The mutable scaled global coordinate vector.
	 */
	protected AazonVect getMutableInvScalingVect( final AazonVect _a )
	{
		final double svx = ( endX - startX ) / 2.0;
		final double svy = ( endY - startY ) / 2.0;
		final AazonBaseImmutableVect vct = new AazonBaseImmutableVect( svx , svy );
		return( aazon.vect.AazonMutableScalingVect.construct( _a , vct ) );
	}
	
	
	/**
	 * Factory for generating the center location of a node.
	 */
	AazonCenterVectLocationFactory fact = null;
	
	
	/**
	 * Gets the factory for generating the center location of a node.
	 * @return Factory for generating the center location of a node.
	 */
	public AazonCenterVectLocationFactory getFact()
	{
		if( fact == null )
		{	
			fact = new AazonCenterVectLocationFactory()
			{
				
				/**
				 * Returns a vector to the center location of a node.
				 * @return The vector to the center location of a node.
				 */
				public AazonVect genVect( final AazonVect in )
				{
					return( getMutableScalingVect( in ) );
				}
				
				/**
				 * Gets the scaling factor.
				 * @return The scaling factor.
				 */
				public double getScaleVal()
				{
					return( DraggableTransformNodeTest.this.getScaleVal() );
				}
				
			};
		}
		return( fact );
	}
	
	
	/**
	 * Gets the current scale factor of the view.
	 * @return The current scale factor of the view.
	 */
	public double getScaleVal()
	{
		final double sv1 = ( endX - startX ) / 2.0;
		final double sv2 = ( endY - startY ) / 2.0;
		return( Math.min( 1.0 / sv1 , 1.0 / sv2 ) );
	}
	
	
	/**
	 * The start X-coordinate of the zoom region.
	 */
	protected double startX = -1.0;
	
	/**
	 * The end X-coordinate of the zoom region.
	 */
	protected double endX = 1.0;
	
	/**
	 * The start Y-coordinate of the zoom region.
	 */
	protected double startY = -1.0;
	
	/**
	 * The end Y-coordinate of the zoom region.
	 */
	protected double endY = 1.0;

	
	/**
	 * Constructs the editor.
	 * @param _univ The universe ID for the editor.
	 * @param _nodeSet Set of BuilderNodes to be edited.
	 * @param _af The root factory.
	 * @param _builderNodes Separate ArrayList of BuilderNodes to be edited.
	 * @param _nodeGroup Group of AazonEnts containing the nodes.
	 * @param _resizeVect Aczon vector representing the resizing of the window.
	 * @param _sysMode The current "system mode" for editing.
	 * @param _gloTransform Vector indicating the global coordinate transform.
	 * @param _frontPaneGroup The group of AazonEnts containing the frontal pane for controls.
	 * @param _lineGroup The group of AazonEnts containing connection lines.
	 * @param _zoomGroup The group of AazonEnts containing the rubber-band line for the zoom operation.
	 * @param _track The instrument track in which the nodes exist, or null if no instrument track.
	 * @param _undoMgr The undo manager for performing undo.
	 * @param _palette Interface defining accessors to state information for a GUI palette.
	 */
	public DraggableTransformNodeTest(Object _univ, HashSet<BuilderNode> _nodeSet, AczonRootFactory _af,
			ArrayList<BuilderNode> _builderNodes, AazonMutableGroup _nodeGroup,
			AazonMutableVect _resizeVect, AazonInt _sysMode,
			AazonBaseMutableVect _gloTransform,
			AazonMutableGroup _frontPaneGroup, AazonMutableGroup _lineGroup, AazonMutableGroup _zoomGroup,
			InstrumentTrack _track, UndoManager _undoMgr, IPalette _palette) {
		univ = _univ;
		nodeSet = _nodeSet;
		af = _af;
		builderNodes = _builderNodes;
		nodeGroup = _nodeGroup;
		resizeVect = _resizeVect;
		sysMode = _sysMode;
		gloTransform = _gloTransform;
		frontPaneGroup = _frontPaneGroup;
		lineGroup = _lineGroup;
		zoomGroup = _zoomGroup;
		track = _track;
		undoMgr = _undoMgr;
		palette = _palette;
		af.getCanvas().addMouseListener(this);
		af.getCanvas().addMouseMotionListener(this);
	}

	/**
	 * Handles a mouse-click event.
	 * @param arg0 The input event.
	 */
	public void mouseClicked(MouseEvent arg0) {
		if (dragVect != null) {
			dragVect.mouseClicked(arg0);
		} else {

		}
	}

	/**
	 * Handles a mouse-enter event.
	 * @param arg0 The input event.
	 */
	public void mouseEntered(MouseEvent arg0) {
		if (dragVect != null) {
			dragVect.mouseEntered(arg0);
		} else {

		}
	}

	/**
	 * Handles a mouse-exit event.
	 * @param arg0 The input event.
	 */
	public void mouseExited(MouseEvent arg0) {
		if (dragVect != null) {
			dragVect.mouseExited(arg0);
		} else {

		}
		dragging = false;
	}

	/**
	 * Handles a mouse-pressed event.
	 * @param arg0 The input event.
	 */
	public void mousePressed(MouseEvent arg0) {
		if (dragVect != null) {
			dragVect.mousePressed(arg0);
		} else {
			if (!dragging) {
				handleMouseDragBegin(arg0.getX(), arg0.getY());
			}
		}
		dragging = true;
	}

	/**
	 * Handles a mouse-released event.
	 * @param arg0 The input event.
	 */
	public void mouseReleased(MouseEvent arg0) {
		if (dragVect != null) {
			dragVect.mouseReleased(arg0);
		} else {

		}
		dragging = false;
	}

	/**
	 * Handles a mouse-drag event.
	 * @param arg0 The input event.
	 */
	public void mouseDragged(MouseEvent arg0) {
		if (dragVect != null) {
			dragVect.mouseDragged(arg0);
		} else {
			if (!dragging) {
				handleMouseDragBegin(arg0.getX(), arg0.getY());
			}
		}
		dragging = true;
	}

	/**
	 * Handles a mouse-moved event.
	 * @param arg0 The input event.
	 */
	public void mouseMoved(MouseEvent arg0) {
		if (dragVect != null) {
			dragVect.mouseMoved(arg0);
		} else {

		}
	}

	

	/**
	 * Creates the contents of the line group from the list of nodes.
	 * @param univ The universe ID.
	 * @param nodes The list of nodes.
	 * @param fact Factory for generating the center location of a node.
	 * @return The line group.
	 */
	protected static AazonImmutableGroup createLineGroup(Object univ, ArrayList<BuilderNode> nodes, AazonCenterVectLocationFactory fact) {
		ArrayList<AazonEnt> lines = new ArrayList<AazonEnt>();
		final int sz = nodes.size();
		int count;
		for (count = 0; count < sz; count++) {
			final BuilderNode nd = nodes.get(count);
			final Object chld = nd.getChldNodes();
			BuilderNode[] cc = AazonTransChld.getTransChld(chld);

			final int ccsz = cc.length;
			int cnt;
			for (cnt = 0; cnt < ccsz; cnt++) {
				final BuilderNode cn = cc[cnt];
				if( cn != null )
				{
					final AazonEnt ln0 = AczonSmartConnectionLine.construct(cn
							.getTransformedCenterVect(univ, fact), AczonColor.getLineOrange(), nd
							.getTransformedCenterVect(univ, fact), AczonColor.getLineWhite());
					lines.add(ln0);
				}
			}
		}

		final int nsz = lines.size();
		AazonEnt[] ents = new AazonEnt[nsz];
		for (count = 0; count < nsz; count++) {
			ents[count] = lines.get(count);
		}
		return (new AazonImmutableGroup(ents));
	}

	/**
	 * Handles the modification of the line group due to node changes.
	 * @param univ The universe ID.
	 * @param nodes The list of nodes.
	 * @param grp The output group receiving the modifications.
	 * @param fact Factory for generating the center location of a node.
	 */
	protected static void modifyLineGroup(Object univ, ArrayList<BuilderNode> nodes, AazonMutableGroup grp, AazonCenterVectLocationFactory fact) {
		grp.setGrp(createLineGroup(univ,nodes,fact));
	}

	/**
	 * Creates the contents of the node group from the list of nodes.
	 * @param univ The universe ID.
	 * @param nodes The list of nodes.
	 * @return The node group.
	 * @param fact Factory for generating the center location of a node.
	 */
	protected static AazonImmutableGroup buildGroup(Object univ, ArrayList<BuilderNode> nodes, AazonCenterVectLocationFactory fact) {
		final int sz = nodes.size();
		final AazonEnt[] ents = new AazonEnt[sz];
		int count;
		for (count = 0; count < sz; count++) {
			BuilderNode node = nodes.get(count);
			ents[count] = node.getEnt(univ,fact);
		}
		return (new AazonImmutableGroup(ents));
	}
	
	/**
	 * Handles the modification of the node group due to node changes.
	 * @param univ The universe ID.
	 * @param nodes The list of nodes.
	 * @param grp The output group receiving the modifications.
	 * @param fact Factory for generating the center location of a node.
	 */
	protected static void modifyGroup(Object univ, ArrayList<BuilderNode> nodes, AazonMutableGroup grp, AazonCenterVectLocationFactory fact) {
		grp.setGrp(buildGroup(univ, nodes, fact));
	}
	

	
	/**
	 * Creates an starts an editor for graphically manipulating BuilderNodes.
	 * @param univ The universe ID to be used for the main editing window.
	 * @param univ2 The universe ID to be used for adjoining palette window.
	 * @param s The set of BuilderNodes to be edited.
	 * @param trk The instrument track in which the nodes exist, or null if no instrument track.
	 * @param undoMgr The undo manager for performing undo.
	 * @param palette Interface defining accessors to state information for a GUI palette.
	 */
	public static void start(final Object univ, final Object univ2, HashSet<BuilderNode> s, InstrumentTrack trk, UndoManager undoMgr, IPalette palette) {

		int count;

		final AczonResizeVectFactory mf = new AczonResizeVectFactory() {
			public AczonResizeVect createVect(final AczonRootFactory rf) {
				final AczonResizeVect vct = new AczonResizeVect() {
					
					@Override
					protected double getInnerX() {
						return (700);
					}

					@Override
					protected double getInnerY() {
						return (700);
					}

				};
				vct.setFactory(rf);
				return (vct);
			}
		};

		final AazonBaseMutableInt sysMode = new AazonBaseMutableInt(
				DraggableTestPalette.MOVE_MODE);

		ArrayList<BuilderNode> builderNodes = new ArrayList<BuilderNode>();
		
		for( final BuilderNode bl : s )
		{
			builderNodes.add( bl );
		}

		final AazonMutableGroup nodeGroup = new AazonMutableGroup();

		modifyGroup(univ,builderNodes, nodeGroup, AazonDefaultCenterVectLocationFactory.getFact() );

		final AazonMutableGroup frontPaneGroup = new AazonMutableGroup();

		final AazonMutableGroup lineGroup = new AazonMutableGroup();
		
		final AazonMutableGroup zoomGroup = new AazonMutableGroup();

		modifyLineGroup(univ,builderNodes, lineGroup, AazonDefaultCenterVectLocationFactory.getFact() );

		final AazonEnt[] ents1 = { lineGroup, nodeGroup, frontPaneGroup };

		final AazonImmutableOrderedGroup gp1 = new AazonImmutableOrderedGroup(
				ents1);

		final AazonBaseMutableVect gloTransform = new AazonBaseMutableVect(0.0,
				0.0);

		final AazonEnt gp2 = AazonSmartTranslation.construct(gp1, gloTransform);

		final AazonEnt[] ents2 = { zoomGroup , gp2 };

		final AazonImmutableGroup gp3 = new AazonImmutableGroup(ents2);

		final AczonRootFactory af = new AczonRootFactory(gp3);

		drEnt = gp3;

		final JFrame fr = new JFrame();

		final JPanel pn = new JPanel();

		fr.getContentPane().setLayout(new BorderLayout(0, 0));

		pn.setLayout(new BorderLayout(0, 0));

		fr.getContentPane().add(BorderLayout.CENTER, pn);

		pn.add(BorderLayout.CENTER, af.getCanvas());

		pn.setMinimumSize(new Dimension(700, 700));

		pn.setPreferredSize(new Dimension(700, 700));

		fr.pack();

		fr.show();

		final AazonMutableVect resizeVect = af.constructResizeVect(mf);

		dr = new DraggableTransformNodeTest(univ, s, af, builderNodes, nodeGroup,
				resizeVect, sysMode, gloTransform, frontPaneGroup, lineGroup, zoomGroup , trk, undoMgr, palette);

		final JFrame fr2 = DraggableTestPalette.start(univ2,sysMode);
		
		WindowListener wl = new WindowAdapter()
		{
			boolean closed = false;
			
			@Override
			public void windowClosing( WindowEvent e )
			{
				if( !closed )
				{
					closed = true;
					fr.setVisible( false );
					fr.dispose();
					fr2.setVisible( false );
					fr2.dispose();
				}
			}
			
			@Override
			public void windowClosed( WindowEvent e )
			{
				if( !closed )
				{
					closed = true;
					fr.setVisible( false );
					fr.dispose();
					fr2.setVisible( false );
					fr2.dispose();
				}
			}
		};
		
		fr.addWindowListener( wl );
		fr2.addWindowListener( wl );

	}

}
