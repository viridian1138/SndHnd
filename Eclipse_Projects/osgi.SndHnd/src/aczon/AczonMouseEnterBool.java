




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

import aazon.AazonListener;
import aazon.bool.AazonBaseImmutableBool;
import aazon.bool.AazonMutableBool;

/***
 * Aczon boolean representing whether the mouse has entered the window.
 * 
 * @author tgreen
 *
 */
public class AczonMouseEnterBool extends AazonMutableBool implements MouseListener {
	
	/**
	 * The post-listeners for the class that are fired after the boolean changes and the state change events are fired, such as top-level AazonStateFiredMutable.
	 */
	protected AazonListener[] postListeners;
	
	/**
	 * The encapsulated boolean state.
	 */
	protected AazonBaseImmutableBool bool = AazonBaseImmutableBool.FALSE;
	
	/**
	 * Constructs the class.
	 */
	public AczonMouseEnterBool()
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
	
	@Override
	public AazonBaseImmutableBool getBool()
	{
		return( bool );
	}
	
	/**
	 * Handles the setting of the boolean and the firing of the state change events by firing the post-listeners.
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
	 * Handles the setting of the boolean.
	 * @param _bool The new boolean value to set.
	 */
	protected void handleSet( AazonBaseImmutableBool _bool )
	{
		synchronized( this )
		{
			bool = _bool;
			fire();
			handleEvent();
		}
	}
	
	
	/**
	 * Handles a mouse-entered event.
	 * @param arg0 The event.
	 */
	public void mouseEntered( MouseEvent arg0 )
	{
		handleSet( AazonBaseImmutableBool.TRUE );
	}
	
	/**
	 * Handles a mouse-exited event.
	 * @param arg0 The event.
	 */
	public void mouseExited( MouseEvent arg0)
	{
		handleSet( AazonBaseImmutableBool.FALSE );
	}
	
	/**
	 * Handles a mouse-clicked event.
	 * @param arg0 The event.
	 */
	public void mouseClicked( MouseEvent arg0)
	{
	}
	
	/**
	 * Handles a mouse-pressed event.
	 * @param arg0 The event.
	 */
	public void mousePressed( MouseEvent arg0)
	{
	}
	
	/**
	 * Handles a mouse-released event.
	 * @param arg0 The event.
	 */
	public void mouseReleased( MouseEvent arg0)
	{
	}
	
	/**
	 * Gets the post-listeners for the class that are fired after the boolean changes and the state change events are fired, such as top-level AazonStateFiredMutable.
	 * @return The post-listeners for the class.
	 */
	protected AazonListener[] getPostListeners()
	{
		AazonListener[] listeners = { };
		return( listeners );
	}

}

