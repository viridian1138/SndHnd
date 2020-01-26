




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







package aczon;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import aazon.AazonListener;
import aazon.vect.AazonInnerMutableVect;

/***
 * Aczon vector representing the position of the mouse during a mouse-drag operation.
 * 
 * @author tgreen
 *
 */
public abstract class AczonMouseDragVect extends AazonInnerMutableVect implements MouseListener,
		MouseMotionListener {
	
	/**
	 * The post-listeners for the class that are fired after the vector changes, such as top-level AazonStateFiredMutable.
	 */
	protected AazonListener[] postListeners;
	
	/**
	 * Events to be fired upon an update of the vector, such as AbzonCoordSetEvent, AbzonImmutSetEvent, or AbzonBufferedImmutSetEvent instances.
	 */
	protected Object[] setEvents;
	
	/**
	 * Constructs the class.
	 */
	public AczonMouseDragVect()
	{
		super();
		postListeners = getPostListeners();
	}
	
	/**
	 * Handles the initial setup of the class by firing initial events.
	 */
	public void handleInitialSetup()
	{
		fire();
		handleEvent();
	}
	
	/**
	 * Handles the end of a mouse-drag update by firing the post-listeners.
	 */
	protected void handleEvent()
	{
		final int sz = postListeners.length;;
		for( int count = 0 ; count < sz ; count++ )
		{
			final AazonListener l = postListeners[ count ];
			l.handleListen();
		}
	}
	
	/**
	 * Handles a mouse-drag update by updating the vector, and broadcasting events.
	 * @param x The x-coordinate at the position.
	 * @param y The y-coordinate at the position.
	 */
	protected void handleDrag( int x , int y )
	{
		synchronized( this )
		{
			this.setCoords( x , y );
			handleEvent();
		}
	}
	
	/**
	 * Handles the end of a mouse-drag by updating the vector, broadcasting one final set of events, clearing memory, and then calling handleListenerEnd().
	 * @param x The x-coordinate at the final position.
	 * @param y The y-coordinate at the final position.
	 */
	protected void handleDragEnd( int x , int y )
	{
		synchronized( this )
		{
			handleDrag( x , y );
			map.clear();
			handleListenerEnd();
			postListeners = null;
			setEvents = null;
		}
	}
	
	/**
	 * Handles a mouse-dragged event.
	 * @param arg0 The event.
	 */
	public void mouseDragged(MouseEvent arg0) {
		handleDrag( arg0.getX() , arg0.getY() );
	}

	/**
	 * Handles a mouse-moved event.
	 * @param arg0 The event.
	 */
	public void mouseMoved(MouseEvent arg0) {
		handleDragEnd( arg0.getX() , arg0.getY() );
	}
	
	/**
	 * Handles a mouse-released event.
	 * @param arg0 The event.
	 */
	public void mouseReleased( MouseEvent arg0)
	{
		handleDragEnd( arg0.getX() , arg0.getY() );
	}
	
	/**
	 * Handles a mouse-exited event.
	 * @param arg0 The event.
	 */
	public void mouseExited( MouseEvent arg0)
	{
		handleDragEnd( arg0.getX() , arg0.getY() );
	}
	
	/**
	 * Handles a mouse-clicked event.
	 * @param arg0 The event.
	 */
	public void mouseClicked(MouseEvent arg0) {
	}

	/**
	 * Handles a mouse-entered event.
	 * @param arg0 The event.
	 */
	public void mouseEntered(MouseEvent arg0) {
	}
	
	/**
	 * Handles a mouse-pressed event.
	 * @param arg0 The event.
	 */
	public void mousePressed(MouseEvent arg0) {
	}
	
	/**
	 * Method that can be overridden to handle the final tear-down at the end of a mouse-drag operation.
	 */
	protected void handleListenerEnd()
	{
	}
	
	/**
	 * Gets the post-listeners for the class that are fired after the vector changes, such as top-level AazonStateFiredMutable.
	 * @return The post-listeners for the class.
	 */
	protected AazonListener[] getPostListeners()
	{
		AazonListener[] listeners = { };
		return( listeners );
	}
	
	/**
	 * Sets the events to be fired upon an update of the vector, such as AbzonCoordSetEvent, AbzonImmutSetEvent, or AbzonBufferedImmutSetEvent instances.
	 * @param in The set of events to be fired.
	 */
	public void setSetEvents( Object[] in )
	{
		setEvents = in;
	}

}

