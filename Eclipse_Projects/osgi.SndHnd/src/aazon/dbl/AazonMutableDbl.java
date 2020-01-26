




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







package aazon.dbl;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.WeakHashMap;

import aazon.AazonListener;
 
/**
 * Abstract base class for an Aazon mutable double.
 * 
 * @author tgreen
 *
 */
public abstract class AazonMutableDbl extends AazonDbl {

	/**
	 * The map of listeners.
	 */
	protected WeakHashMap<AazonListener,WeakReference<AazonListener>> map = new WeakHashMap<AazonListener,WeakReference<AazonListener>>();

	/**
	 * Adds a listener.
	 * @param l The listener to add.
	 */
	public void add( AazonListener l )
	{
		map.put(  l , new WeakReference<AazonListener>( l ) );
	}
	
	/**
	 * Removes a listener.
	 * @param l The listener to remove.
	 */
	public void remove( AazonListener l )
	{
		map.remove( l );
	}
	
	/**
	 * Fires events to the listeners.
	 */
	protected void fire()
	{
		for( final AazonListener l : map.keySet() )
		{
			l.handleListen();
		}
	}
	
	/**
	 * Gets the set of listeners.
	 * @return The set of listeners.
	 */
	public Set<AazonListener> getListeners()
	{
		return( map.keySet() );
	}
	
	
}

