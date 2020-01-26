




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

import javax.swing.JFrame;
import javax.swing.JPanel;

import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.intg.AazonBaseMutableInt;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aczon.AczonBoxCheck;
import aczon.AczonCoordinateConvert;
import aczon.AczonResizeVect;
import aczon.AczonResizeVectFactory;
import aczon.AczonRootFactory;


/**
 * Palette used in grview.DraggableTransformNodeTest.
 * 
 * @author tgreen
 *
 */
public class DraggableTestPalette implements MouseListener {
	
	/**
	 * Constant for the move-mode.
	 */
	public static final int MOVE_MODE = 0;
	
	/**
	 * Constant for the hand-mode.
	 */
	public static final int HAND_MODE = 1;
	
	/**
	 * Constant for the zoom-mode.
	 */
	public static final int ZOOM_MODE = 2;
	
	/**
	 * Constant for the X-zoom-mode.
	 */
	public static final int ZOOM_X_MODE = 3;
	
	/**
	 * Constant for the Y-zoom-mode.
	 */
	public static final int ZOOM_Y_MODE = 4;
	
	/**
	 * Constant for the un-zoon-mode.
	 */
	public static final int UNZOOM_MODE = 5;
	
	/**
	 * Constant for the node connection-mode.
	 */
	public static final int CONNECT_MODE = 6;
	
	/**
	 * Constant for the create-node-mode.
	 */
	public static final int NEW_MODE = 7;
	
	/**
	 * Constant for the propertied editing-mode.
	 */
	public static final int PROPERTIES_MODE = 8;
	
	/**
	 * Constant for the delete-child-mode.
	 */
	public static final int DEL_CHLD_MODE = 9;
	
	
	
	/**
	 * Constant for the maximum mode number.
	 */
	public static final int MAX_MODE = 10;
	
	
	
	/**
	 * The current editing mode of the overall system.
	 */
	protected AazonBaseMutableInt sysMode;
	
	/**
	 * The nodes for the individual palette buttons.
	 */
	protected TestPaletteNode[] txnodes;
	
	/**
	 * Aczon vector representing the resizing of the window.
	 */
	final AazonMutableVect resizeVect;
	
	/**
	 * The root factory.
	 */
	final AczonRootFactory af;
	
	
	
	/**
	 * Allocated singleton instance of the palette.
	 */
	protected static DraggableTestPalette dr;
	
	/**
	 * Allocated singleton for the AazonEnt to be displayed.
	 */
	protected static AazonEnt drEnt;
	
	/**
	 * The scaling factor for the palette.
	 */
	static final double SCALE = 1.0;
	
	
	
	/**
	 * Constructs the palette.
	 * @param _af The root factory.
	 * @param _txnodes The nodes for the individual palette buttons.
	 * @param _resizeVect Aczon vector representing the resizing of the window.
	 * @param _sysMode The current editing mode of the overall system.
	 */
	public DraggableTestPalette( AczonRootFactory _af , TestPaletteNode[] _txnodes , AazonMutableVect _resizeVect , AazonBaseMutableInt _sysMode )
	{
		af = _af;
		txnodes = _txnodes;
		resizeVect = _resizeVect;
		sysMode = _sysMode;
		af.getCanvas().addMouseListener( this );
	}
	

	/**
	 * Handles a mouse-pressed event by finding the button in which the mouse was pressed and then setting the system mode consistent with that button.
	 * @param argo The mouse-pressed event.
	 */
	public void mousePressed(MouseEvent arg0) {
		final int x = arg0.getX();
		final int y = arg0.getY();
		final AazonImmutableVect ivect = AczonCoordinateConvert.convertCoords(x, y, resizeVect, af);
		final int sz = txnodes.length;
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			TestPaletteNode node = txnodes[ count ];
			if( AczonBoxCheck.isInside( ivect , node.getBox0() , node.getBox1() ) )
			{
				sysMode.setCoords( node.getMode() );
				return;
			}
		}
	}
	
	/**
	 * Interface to handle a mouse-clicked event.  Does nothing.
	 * @param arg0 The event.
	 */
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * Interface to handle a mouse-entered event.  Does nothing.
	 * @param arg0 The event.
	 */
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * Interface to handle a mouse-exited event.  Does nothing.
	 * @param arg0 The event.
	 */
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * Interface to handle a mouse-released event.  Does nothing.
	 * @param arg0 The event.
	 */
	public void mouseReleased(MouseEvent arg0) {
	}
	
	/**
	 * Creates and starts an instance of the DraggableTestPalette,
	 * @param univ The universe ID in which to create the palette.
	 * @param sysMode The current editing mode of the overall system.
	 * @return The JFrame containing the palette.
	 */
	public static JFrame start( final Object univ , final AazonBaseMutableInt sysMode )
	{
		int count;
		
		final TestPaletteNode moveNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.9 , 0.9 ) , MOVE_MODE , 
				GNode.getRectGrpOn( univ ) , GNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode handNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.7 , 0.9 ) , HAND_MODE , 
				GNode.getRectGrpOn( univ ) , GNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode zoomNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.5 , 0.9 ) , ZOOM_MODE , 
				GNode.getRectGrpOn( univ ) , GNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode zoomXNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.3 , 0.9 ) , ZOOM_X_MODE , 
				GNode.getRectGrpOn( univ ) , GNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode zoomYNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.1 , 0.9 ) , ZOOM_Y_MODE , 
				GNode.getRectGrpOn( univ ) , GNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode unzoomNode = new TestPaletteNode( new AazonBaseImmutableVect( +0.1 , 0.9 ) , UNZOOM_MODE , 
				GNode.getRectGrpOn( univ ) , GNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode connectNode = new TestPaletteNode( new AazonBaseImmutableVect( +0.3 , 0.9 ) , CONNECT_MODE , 
				PaletteConnect.getRectGrpOn( univ ) , PaletteConnect.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode triNode = new TestPaletteNode( new AazonBaseImmutableVect( +0.5 , 0.9 ) , NEW_MODE , 
				PaletteNew.getRectGrpOn( univ ) , PaletteNew.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode propertiesNode = new TestPaletteNode( new AazonBaseImmutableVect( +0.7 , 0.9 ) , PROPERTIES_MODE , 
				PaletteProperties.getRectGrpOn( univ ) , PaletteProperties.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode delChldNode = new TestPaletteNode( new AazonBaseImmutableVect( +0.9 , 0.9 ) , DEL_CHLD_MODE , 
				PaletteDelChld.getRectGrpOn( univ ) , PaletteDelChld.getRectGrpOff( univ ) , sysMode );
		
		
		
		final TestPaletteNode[] tnodes = { moveNode , handNode , zoomNode , zoomXNode , zoomYNode ,
				unzoomNode , connectNode , triNode , propertiesNode , delChldNode };
		final int sz = tnodes.length;
		
		
		
		final AazonEnt[] nodes = new AazonEnt[ sz ];
		for( count = 0 ; count < sz ; count++ )
		{
			nodes[ count ] = tnodes[ count ].getEnt();
		}

		
		
		final AazonImmutableGroup nodeGroup = new AazonImmutableGroup( nodes );
		
		final AczonRootFactory af = new AczonRootFactory( nodeGroup );
		
		drEnt = nodeGroup;
		
		final JFrame fr = new JFrame();
		
		final JPanel pn = new JPanel();
		
		fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
		
		pn.setLayout( new BorderLayout( 0 , 0 ) );
		
		fr.getContentPane().add( BorderLayout.CENTER , pn );
		
		pn.add( BorderLayout.CENTER , af.getCanvas() );
		
		pn.setMinimumSize( new Dimension( 250 , 250 ) );
		
		pn.setPreferredSize( new Dimension( 250 , 250 ) );
		
		fr.pack();
		
		fr.show();
		
		final AczonResizeVectFactory mf = new AczonResizeVectFactory()
		{
			/**
			 * Creates a resize vector.
			 * @param rf The root factory defining coordinate transforms.
			 * @return The Aczon resize vector.
			 */
			public AczonResizeVect createVect( final AczonRootFactory rf )
			{
				final AczonResizeVect vct = new AczonResizeVect()
				{
					@Override
					protected double getInnerX()
					{
						return( 250 );
					}
					
					@Override
					protected double getInnerY()
					{
						return( 250 );
					}
					
				};
				vct.setFactory( rf );
				return( vct );
			}
		};
		
		
		dr = new DraggableTestPalette( af , tnodes , af.constructResizeVect( mf ) , sysMode );
		
		return( fr );
	}

}

