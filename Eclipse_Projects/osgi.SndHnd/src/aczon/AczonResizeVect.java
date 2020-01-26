




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

import aazon.AazonListener;
import aazon.vect.AazonInnerMutableVect;

/**
 * Aczon vector representing the resizing of the window.
 * 
 * @author tgreen
 *
 */
public abstract class AczonResizeVect extends AazonInnerMutableVect {
	
	/**
	 * The post-listeners for the class that are fired after the vector changes, such as top-level AazonStateFiredMutable.
	 */
	protected AazonListener[] postListeners;
	
	/**
	 * Events to be fired upon an update of the vector, such as AbzonCoordSetEvent, AbzonImmutSetEvent, or AbzonBufferedImmutSetEvent instances.
	 */
	protected Object[] setEvents;
	
	/**
	 * Root factory providing support for window geometry changes.
	 */
	protected AczonRootFactory factory;
	
	/**
	 * Constructs the class.
	 */
	public AczonResizeVect( )
	{
		super();
		postListeners = getPostListeners();
	}
	
	/**
	 * Sets the root factory providing support for window geometry changes.
	 * @param _factory The input root factory.
	 */
	public void setFactory( AczonRootFactory _factory )
	{
		factory = _factory;
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
	 * Handles the end of a window resize update by firing the post-listeners.
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
	 * Handles the resize of the window by updating the vector, and broadcasting events.
	 * @param x The x-axis window size.
	 * @param y The y-axis window size.
	 */
	public void handleResize( int x , int y )
	{
		synchronized( this )
		{
			if( ( ( Math.abs( x - this.x ) ) > 1E-6 ) && ( ( Math.abs( y - this.y ) ) > 1E-6 ) )
			{
				this.setCoords( x , y );
				factory.resetView();
				handleEvent();
			}
		}
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

}

