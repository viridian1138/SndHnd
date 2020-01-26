




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

import javax.media.j3d.ViewPlatform;
import javax.swing.JFrame;
import javax.swing.JPanel;

import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonSmartFilledRectangle;
import aazon.AazonSmartLine;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBaseMutableVect;
import aazon.vect.AazonImmutableSubtractVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;
import abzon.AbzonCoordSetEvent;
import aczon.AczonBoxCheck;
import aczon.AczonColor;
import aczon.AczonCoordinateConvert;
import aczon.AczonMouseDragVect;
import aczon.AczonResizeVect;
import aczon.AczonResizeVectFactory;
import aczon.AczonRootFactory;
import aczon.AczonSmartConnectionLine;

/**
 * Tests the ability to drag a rectangle.
 * 
 * @author tgreen
 *
 */
public class DraggableRectTest implements MouseListener, MouseMotionListener {
	
	/**
	 * The root factory.
	 */
	protected AczonRootFactory af;
	
	/**
	 * Vector indicating the mouse-drag location.
	 */
	protected AczonMouseDragVect dragVect = null;
	
	/**
	 * Indicates whether a mouse-drag is underway.
	 */
	protected boolean dragging = false;
	
	/**
	 * The location of the center of the palette button.
	 */
	AazonBaseMutableVect centerVect;
	
	/**
	 * The location of the lower-left corner of the palette button.
	 */
	AazonVect box0;
	
	/**
	 * The location of the upper-right corner of the palette button.
	 */
	AazonVect box1;
	
	/**
	 * Aczon vector representing the resizing of the window.
	 */
	AazonMutableVect resizeVect;
	
	/**
	 * The allocated singleton of the test.
	 */
	protected static DraggableRectTest dr;
	
	
	/**
	 * Handles the presence of the mouse-down inside the rectangle.
	 * @param x The X-coordinate of the mouse event.
	 * @param y The Y-coordinate of the mouse event.
	 * @param offsetVect The offset vector from the center defined by the original mouse click.
	 * @param resizeVect Aczon vector representing the resizing of the window.
	 * @param platform The root factory.
	 */
	protected void handleMouseInsideBox( final int x , final int y , final AazonImmutableVect offsetVect , final AazonMutableVect resizeVect , final AczonRootFactory platform )
	{
		dragVect = new AczonMouseDragVect()
		{
			@Override
			protected double getInnerX()
			{
				return( x );
			}
			
			@Override
			protected double getInnerY()
			{
				return( y );
			}
			
			@Override
			protected void handleListenerEnd()
			{
				dragVect = null;
				( (AbzonCoordSetEvent)( setEvents[ 0 ] ) ).disable();
			}
		};
		
		final AazonVect cvect = AczonCoordinateConvert.construct( dragVect , resizeVect , platform );
		
		final AazonVect ovect = AazonMutableAdditiveVect.construct( cvect , offsetVect );
		
		if( !( ovect instanceof AazonMutableVect ) )
		{
			throw( new RuntimeException( "Inconsistent" ) );
		}
		
		final AbzonCoordSetEvent acse = new AbzonCoordSetEvent( (AazonMutableVect) ovect , centerVect );
		
		final Object[] asetEvents = { acse };
		
		dragVect.setSetEvents( asetEvents );
	}
	
	
	/**
	 * Handles the beginning of the mouse-drag.
	 * @param x The X-coordinate of the mouse event.
	 * @param y The Y-coordinate of the mouse event.
	 * @param resizeVect Aczon vector representing the resizing of the window.
	 * @param platform The root factory.
	 */
	protected void handleMouseDragBegin( final int x , final int y , final AazonMutableVect resizeVect , final AczonRootFactory platform )
	{
		final AazonImmutableVect ivect = AczonCoordinateConvert.convertCoords(x, y, resizeVect, platform);
		if( AczonBoxCheck.isInside( ivect , box0 , box1 ) )
		{
			final AazonImmutableVect offsetVect = AazonImmutableSubtractVect.construct( new AazonBaseImmutableVect( centerVect.getX() , centerVect.getY() ) , ivect );
			handleMouseInsideBox( x , y , offsetVect , resizeVect , platform );
		}
	}
	
	
	/**
	 * Constructs an instance of the test class.
	 * @param _af The root factory.
	 * @param _centerVect The vector to the center of the palette button.
	 * @param _box0 The location of the lower-left corner of the palette button.
	 * @param _box1 The location of the upper-right corner of the palette button.
	 * @param _resizeVect Aczon vector representing the resizing of the window.
	 */
	public DraggableRectTest( AczonRootFactory _af , AazonBaseMutableVect _centerVect , AazonVect _box0 , AazonVect _box1 , AazonMutableVect _resizeVect )
	{
		af = _af;
		centerVect = _centerVect;
		box0 = _box0;
		box1 = _box1;
		resizeVect = _resizeVect;
		af.getCanvas().addMouseListener( this );
		af.getCanvas().addMouseMotionListener( this );
	}
	

	/**
	 * Handles a mouse-click event.
	 * @param arg0 The input event.
	 */
	public void mouseClicked(MouseEvent arg0) {
		if( dragVect != null )
		{
			dragVect.mouseClicked(arg0);
		}
		else
		{
			
		}
	}

	/**
	 * Handles a mouse-enter event.
	 * @param arg0 The input event.
	 */
	public void mouseEntered(MouseEvent arg0) {
		if( dragVect != null )
		{
			dragVect.mouseEntered(arg0);
		}
		else
		{
			
		}
	}

	/**
	 * Handles a mouse-exit event.
	 * @param arg0 The input event.
	 */
	public void mouseExited(MouseEvent arg0) {
		if( dragVect != null )
		{
			dragVect.mouseExited(arg0);
		}
		else
		{
			
		}
		dragging = false;
	}

	/**
	 * Handles a mouse-pressed event.
	 * @param arg0 The input event.
	 */
	public void mousePressed(MouseEvent arg0) {
		if( dragVect != null )
		{
			dragVect.mousePressed(arg0);
		}
		else
		{
			if( !dragging )
			{
				handleMouseDragBegin( arg0.getX() , arg0.getY() , resizeVect , af );
			}
		}
		dragging = true;
	}

	/**
	 * Handles a mouse-released event.
	 * @param arg0 The input event.
	 */
	public void mouseReleased(MouseEvent arg0) {
		if( dragVect != null )
		{
			dragVect.mouseReleased(arg0);
		}
		else
		{
			
		}
		dragging = false;
	}

	/**
	 * Handles a mouse-drag event.
	 * @param arg0 The input event.
	 */
	public void mouseDragged(MouseEvent arg0) {
		if( dragVect != null )
		{
			dragVect.mouseDragged(arg0);
		}
		else
		{
			if( !dragging )
			{
				handleMouseDragBegin( arg0.getX() , arg0.getY() , resizeVect , af );
			}
		}
		dragging = true;
	}

	/**
	 * Handles a mouse-moved event.
	 * @param arg0 The input event.
	 */
	public void mouseMoved(MouseEvent arg0) {
		if( dragVect != null )
		{
			dragVect.mouseMoved(arg0);
		}
		else
		{
			
		}
	}

	
	
	/**
	 * Tests the ability to drag a rectangle.
	 * @param args Args.
	 */
	public static void main(String[] args) {
		
		final AazonEnt line1 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , 0.0 ) , new AazonBaseImmutableVect( 1.0 , 1.0 ) , AczonColor.getLineMagenta() , false );
		
		final AazonEnt line2 = AazonSmartLine.construct( new AazonBaseImmutableVect( 0.0 , 0.0 ) , new AazonBaseImmutableVect( -1.0 , 1.0 ) , AczonColor.getLineGreen() , false );
		
		final AazonEnt line3 = AazonSmartLine.construct( new AazonBaseImmutableVect( -0.2 , -0.2 ) , new AazonBaseImmutableVect( -0.4 , -0.4 ) , AczonColor.getLineGreen() , false );
		
		final AazonEnt[] ents0 = { line1 , line2 , line3 };
		
		final AazonImmutableGroup gp0 = new AazonImmutableGroup( ents0 );
		
		final AazonBaseMutableVect centerVect = new AazonBaseMutableVect( 0.25 , 0.25 );
		
		final AazonVect box0 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( -0.1 , -0.1 ) );
		
		final AazonVect box1 = AazonMutableAdditiveVect.construct( centerVect , new AazonBaseImmutableVect( 0.1 , 0.1 ) );
		
		final AazonEnt rect = AazonSmartFilledRectangle.construct( box0 , box1 , AczonColor.getFillCyan() , false );
		
		final AazonEnt ln0 = AczonSmartConnectionLine.construct( new AazonBaseImmutableVect( 0.7 , 0.6 ) , AczonColor.getLineOrange() , centerVect , AczonColor.getLineWhite() );
		
		final AazonEnt[] ents1 = { gp0 , ln0 , rect };
		
		final AazonImmutableOrderedGroup gp1 = new AazonImmutableOrderedGroup( ents1 );
		
		final AczonRootFactory af = new AczonRootFactory( gp1 );
		
		final JFrame fr = new JFrame();
		
		final JPanel pn = new JPanel();
		
		fr.getContentPane().setLayout( new BorderLayout( 0 , 0 ) );
		
		pn.setLayout( new BorderLayout( 0 , 0 ) );
		
		fr.getContentPane().add( BorderLayout.CENTER , pn );
		
		pn.add( BorderLayout.CENTER , af.getCanvas() );
		
		pn.setMinimumSize( new Dimension( 500 , 500 ) );
		
		pn.setPreferredSize( new Dimension( 500 , 500 ) );
		
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
						return( 500 );
					}
					
					@Override
					protected double getInnerY()
					{
						return( 500 );
					}
					
				};
				vct.setFactory( rf );
				return( vct );
			}
		};
		
		
		dr = new DraggableRectTest( af , centerVect , box0 , box1 , af.constructResizeVect( mf ) );

	}

	
	
}


