




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
 * Test palette used in DraggableTransformNodeTest.
 * 
 * @author tgreen
 *
 */
public class DraggableTestPalette implements MouseListener {
	
	/**
	 * Constant for a test move-mode.
	 */
	public static final int MOVE_MODE = 0;
	
	/**
	 * Constant for a test hand-mode.
	 */
	public static final int HAND_MODE = 1;
	
	/**
	 * Constant for a test connect-mode.
	 */
	public static final int CONNECT_MODE = 2;
	
	/**
	 * Constant for a test triangle-mode.
	 */
	public static final int TRI_MODE = 3;
	
	/**
	 * Constant for a test quadrilateral-mode.
	 */
	public static final int QUAD_MODE = 4;
	
	/**
	 * Constant for the maximum mode number.
	 */
	public static final int MAX_MODE = 5;
	
	/**
	 * The scaling factor for the palette.
	 */
	final static double SCALE = 1.0;
	
	
	/**
	 * The "system mode" for the palette.
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
	 * Allocated singleton instance of the test palette.
	 */
	protected static DraggableTestPalette dr;
	
	/**
	 * Allocated singleton for the AazonEnt to be displayed.
	 */
	protected static AazonEnt drEnt;
	
	
	
	/**
	 * Constructs the test palette.
	 * @param _af The root factory.
	 * @param _txnodes The nodes for the individual palette buttons.
	 * @param _resizeVect Aczon vector representing the resizing of the window.
	 * @param _sysMode The "system mode" for the palette.
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
	 * Handles a mouse-press event.
	 * @param arg0 The input event.
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
	 * Handles a mouse-click event.
	 * @param arg0 The input event.
	 */
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * Handles a mouse-enter event.
	 * @param arg0 The input event.
	 */
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * Handles a mouse-exit event.
	 * @param arg0 The input event.
	 */
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * Handles a mouse-released event.
	 * @param arg0 The input event.
	 */
	public void mouseReleased(MouseEvent arg0) {
	}
	
	
	/**
	 * Starts the execution of the palette.
	 * @param sysMode The "system mode" to be displayed in the palette.
	 */
	public static void start( final AazonBaseMutableInt sysMode )
	{
		int count;
		
		final Object univ = new Integer( 6 );
			
		
		final TestPaletteNode moveNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.9 , 0.9 ) , MOVE_MODE , 
				QuadTestBuilderNode.getRectGrpOn( univ ) , QuadTestBuilderNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode handNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.7 , 0.9 ) , HAND_MODE , 
				QuadTestBuilderNode.getRectGrpOn( univ ) , QuadTestBuilderNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode connectNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.5 , 0.9 ) , CONNECT_MODE , 
				QuadTestBuilderNode.getRectGrpOn( univ ) , QuadTestBuilderNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode triNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.3 , 0.9 ) , TRI_MODE , 
				TriTestBuilderNode.getRectGrpOn( univ ) , TriTestBuilderNode.getRectGrpOff( univ ) , sysMode );
		
		final TestPaletteNode quadNode = new TestPaletteNode( new AazonBaseImmutableVect( -0.1 , 0.9 ) , QUAD_MODE , 
				QuadTestBuilderNode.getRectGrpOn( univ ) , QuadTestBuilderNode.getRectGrpOff( univ ) , sysMode );
		
		
		
		final TestPaletteNode[] tnodes = { moveNode , handNode , connectNode , triNode , quadNode };
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
	}

}
