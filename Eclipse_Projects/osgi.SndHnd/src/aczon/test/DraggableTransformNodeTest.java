




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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonMutableGroup;
import aazon.AazonSmartLine;
import aazon.AazonSmartToggle;
import aazon.AazonSmartTranslation;
import aazon.bool.AazonBool;
import aazon.builderNode.AazonBaseImmutableBuilderNode;
import aazon.builderNode.AazonBuilderNode;
import aazon.builderNode.AazonCenterVectLocationFactory;
import aazon.builderNode.AazonImmutableChkBuilderNode;
import aazon.builderNode.AazonMutableBuilderBool;
import aazon.builderNode.AazonMutableChkBuilderNode;
import aazon.builderNode.AazonMutableDchkBuilderNode;
import aazon.builderNode.AazonTransChld;
import aazon.builderNode.BuilderNode;
import aazon.builderNode.AazonDefaultCenterVectLocationFactory;
import aazon.dbl.AazonDbl;
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
import abzon.AbzonPathIteratorFactory;
import abzon.AbzonSmartCubicCurveFactory;
import abzon.AbzonSmartShape;
import aczon.AczonColor;
import aczon.AczonCoordinateConvert;
import aczon.AczonMouseDragVect;
import aczon.AczonResizeVect;
import aczon.AczonResizeVectFactory;
import aczon.AczonRootFactory;
import aczon.AczonScaleConvert;
import aczon.AczonSmartConnectionLine;

/**
 * Tests the manipulation of BuilderNodes.
 * 
 * @author tgreen
 *
 */
public class DraggableTransformNodeTest implements MouseListener,
		MouseMotionListener {

	/**
	 * The universe ID for the test singleton.
	 */
	public static final Object univ = new Integer(5);

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
	 * The graph nodes to be displayed.
	 */
	ArrayList builderNodes;
	
	/**
	 * Group of AazonEnts containing the nodes.
	 */
	AazonMutableGroup nodeGroup;

	/**
	 * The current "system mode" for editing.
	 */
	AazonInt sysMode;

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
		modifyGroup(builderNodes, nodeGroup);
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

		if (!(ovect instanceof AazonMutableVect)) {
			throw (new RuntimeException("Inconsistent"));
		}

		final AbzonCoordSetEvent acse = new AbzonCoordSetEvent(
				(AazonMutableVect) ovect, centerVect);

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
					ArrayList nodes = (ArrayList) (setEvents[3]);
					AazonMutableGroup lineGroup = (AazonMutableGroup) (setEvents[4]);
					DraggableTransformNodeTest
							.modifyLineGroup(nodes, lineGroup);
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
				univ,new AazonBaseImmutableBuilderNode(strtNode), chk,getFact());

		final AazonBool selectBool = AazonMutableBuilderBool.construct(univ,dchk,getFact());

		final AazonEnt ln = AazonSmartToggle.construct(lnOn, lnOff, selectBool);

		final AazonImmutableGroup gp = new AazonImmutableGroup(ln);

		frontPaneGroup.setGrp(gp);

		final Object[] asetEvents = { frontPaneGroup, strtNode, dchk,
				builderNodes, lineGroup };

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
								.getCenterVect(univ,getFact()).getX(), bn.getCenterVect(univ,getFact())
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

		case DraggableTestPalette.TRI_MODE: {
			TriTestBuilderNode node = new TriTestBuilderNode(univ);
			node.getCenterVect(univ,getFact()).setCoords(ivect.getX(), ivect.getY());
			builderNodes.add(node);
			modifyGroup(builderNodes, nodeGroup);
		}
			break;

		case DraggableTestPalette.QUAD_MODE: {
			QuadTestBuilderNode node = new QuadTestBuilderNode(univ);
			node.getCenterVect(univ,getFact()).setCoords(ivect.getX(), ivect.getY());
			builderNodes.add(node);
			modifyGroup(builderNodes, nodeGroup);
		}
			break;

		}

	}
	
	
	/**
	 * Gets the AazonCenterVectLocationFactory for the test.
	 * @return The AazonCenterVectLocationFactory for the test.
	 */
	public static AazonCenterVectLocationFactory getFact()
	{
		return( AazonDefaultCenterVectLocationFactory.getFact() );
	}

	
	/**
	 * Constructs the test instance.
	 * @param _af The root factory.
	 * @param _builderNodes The set of graph nodes for the test.
	 * @param _nodeGroup Group of AazonEnts containing the nodes.
	 * @param _resizeVect Aczon vector representing the resizing of the window.
	 * @param _sysMode The current "system mode" for editing.
	 * @param _gloTransform Vector indicating the global coordinate transform.
	 * @param _frontPaneGroup The group of AazonEnts containing the frontal pane for controls.
	 * @param _lineGroup The group of AazonEnts containing connection lines.
	 */
	public DraggableTransformNodeTest(AczonRootFactory _af,
			ArrayList _builderNodes, AazonMutableGroup _nodeGroup,
			AazonMutableVect _resizeVect, AazonInt _sysMode,
			AazonBaseMutableVect _gloTransform,
			AazonMutableGroup _frontPaneGroup, AazonMutableGroup _lineGroup) {
		af = _af;
		builderNodes = _builderNodes;
		nodeGroup = _nodeGroup;
		resizeVect = _resizeVect;
		sysMode = _sysMode;
		gloTransform = _gloTransform;
		frontPaneGroup = _frontPaneGroup;
		lineGroup = _lineGroup;
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
	 * @param nodes The list of nodes.
	 * @return The line group.
	 */
	protected static AazonImmutableGroup createLineGroup(ArrayList<BuilderNode> nodes) {
		ArrayList<AazonEnt> lines = new ArrayList<AazonEnt>();
		int count;
		for ( final BuilderNode nd : nodes ) {
			final Object chld = nd.getChldNodes();
			BuilderNode[] cc = AazonTransChld.getTransChld(chld);

			final int ccsz = cc.length;
			int cnt;
			for (cnt = 0; cnt < ccsz; cnt++) {
				final BuilderNode cn = cc[cnt];
				if( cn != null )
				{
					final AazonEnt ln0 = AczonSmartConnectionLine.construct(cn
							.getTransformedCenterVect(univ,getFact()), AczonColor.getLineOrange(), nd
							.getTransformedCenterVect(univ,getFact()), AczonColor.getLineWhite());
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
	 * @param nodes The list of nodes.
	 * @param grp The output group receiving the modifications.
	 */
	protected static void modifyLineGroup(ArrayList<BuilderNode> nodes, AazonMutableGroup grp) {
		grp.setGrp(createLineGroup(nodes));
	}

	/**
	 * Creates the contents of the node group from the list of nodes.
	 * @param nodes The list of nodes.
	 * @return The node group.
	 */
	protected static AazonImmutableGroup buildGroup(ArrayList<BuilderNode> nodes) {
		final int sz = nodes.size();
		final AazonEnt[] ents = new AazonEnt[sz];
		int count = 0;
		for ( final BuilderNode node : nodes ) {
			ents[count] = node.getEnt(univ,getFact());
			count++;
		}
		return (new AazonImmutableGroup(ents));
	}

	/**
	 * Handles the modification of the node group due to node changes.
	 * @param nodes The list of nodes.
	 * @param grp The output group receiving the modifications.
	 */
	protected static void modifyGroup(ArrayList<BuilderNode> nodes, AazonMutableGroup grp) {
		grp.setGrp(buildGroup(nodes));
	}

	/**
	 * Tests the manipulation of BuilderNodes.
	 * @param args Args.
	 */
	public static void main(String[] args) {

		int count;

		final AczonResizeVectFactory mf = new AczonResizeVectFactory() {
			
			/**
			 * Creates a resize vector.
			 * @param rf The root factory defining coordinate transforms.
			 */
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

		final AazonEnt line1 = AazonSmartLine.construct(
				new AazonBaseImmutableVect(0.0, 0.0),
				new AazonBaseImmutableVect(1.0, 1.0), AczonColor
						.getLineMagenta(), false);

		final AazonEnt line2 = AazonSmartLine.construct(
				new AazonBaseImmutableVect(0.0, 0.0),
				new AazonBaseImmutableVect(-1.0, 1.0), AczonColor
						.getLineGreen(), false);

		final AazonEnt line3 = AazonSmartLine.construct(
				new AazonBaseImmutableVect(-0.2, -0.2),
				new AazonBaseImmutableVect(-0.4, -0.4), AczonColor
						.getLineGreen(), false);

		final AazonMutableGroup bezGroup = new AazonMutableGroup();

		final AazonEnt[] ents0 = { line1, line2, line3, bezGroup };

		final AazonImmutableGroup gp0 = new AazonImmutableGroup(ents0);

		TriTestBuilderNode tri1 = new TriTestBuilderNode(univ);

		TriTestBuilderNode tri2 = new TriTestBuilderNode(univ);

		QuadTestBuilderNode quad1 = new QuadTestBuilderNode(univ);

		QuadTestBuilderNode quad2 = new QuadTestBuilderNode(univ);

		ArrayList<BuilderNode> builderNodes = new ArrayList<BuilderNode>();
		builderNodes.add(tri1);
		builderNodes.add(tri2);
		builderNodes.add(quad1);
		builderNodes.add(quad2);

		tri2.getCenterVect(univ,getFact()).setCoords(-0.5, 0.75);

		quad1.getCenterVect(univ,getFact()).setCoords(0.75, -0.5);

		quad2.getCenterVect(univ,getFact()).setCoords(-0.75, -0.75);

		final AazonMutableGroup nodeGroup = new AazonMutableGroup();

		modifyGroup(builderNodes, nodeGroup);

		final AazonMutableGroup frontPaneGroup = new AazonMutableGroup();

		final AazonMutableGroup lineGroup = new AazonMutableGroup();

		modifyLineGroup(builderNodes, lineGroup);

		final AazonEnt[] ents1 = { gp0, lineGroup, nodeGroup, frontPaneGroup };

		final AazonImmutableOrderedGroup gp1 = new AazonImmutableOrderedGroup(
				ents1);

		final AazonBaseMutableVect gloTransform = new AazonBaseMutableVect(0.0,
				0.0);

		final AazonEnt gp2 = AazonSmartTranslation.construct(gp1, gloTransform);

		final AazonEnt[] ents2 = { gp2 };

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

		final AazonDbl scaleDbl = AczonScaleConvert.construct(resizeVect, af);

		final AbzonPathIteratorFactory curveFac = AbzonSmartCubicCurveFactory
				.construct(new AazonBaseImmutableVect(-0.9, -0.7),
						new AazonBaseImmutableVect(-0.5, 0.5),
						new AazonBaseImmutableVect(0.5, -0.7),
						new AazonBaseImmutableVect(0.9, 0.5));

		final AazonEnt cubicS = AbzonSmartShape.construct(curveFac, scaleDbl,
				new AffineTransform(), AczonColor.getLineMagenta(), false);

		final AazonEnt[] entsx = { cubicS };

		bezGroup.setGrp(new AazonImmutableGroup(entsx));

		dr = new DraggableTransformNodeTest(af, builderNodes, nodeGroup,
				resizeVect, sysMode, gloTransform, frontPaneGroup, lineGroup);

		DraggableTestPalette.start(sysMode);

	}

}
