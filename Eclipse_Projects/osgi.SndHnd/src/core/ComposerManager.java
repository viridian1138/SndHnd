




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







package core;

import java.util.HashSet;
import java.util.TreeMap;


/**
 * Manages the list of defined composer classes (subclasses of IComposerController).  OSGi bundle activators have the responsibility to add/remove composer classes to/from this class in most cases.
 * 
 * @author tgreen
 *
 */
public class ComposerManager {
	
	/**
	 * The map of currently-defined composer classes indexed by human-readable name.  TreeMap is used so that
	 * the composer classes are indexed in alphabetical order by human-readable name.
	 */
	private final static TreeMap<String,Class<? extends IComposerController>> composerMap = new TreeMap<String,Class<? extends IComposerController>>();
	
	/**
	 * The set of listeners to be notified when a composer class is added/removed.
	 */
	private final static HashSet<Runnable> listenerSet = new HashSet<Runnable>();
	
	/**
	 * Gets a clone of the map of currently-defined composer classes,
	 * @return A clone of the map of currently-defined composer classes,
	 */
	public static synchronized TreeMap<String,Class<? extends IComposerController>> getMapClone()
	{
		return( (TreeMap<String,Class<? extends IComposerController>>)( composerMap.clone() ) );
	}
	
	/**
	 * Adds an composer class to the manager.  Usually this is called by an OSGi bundle activator.
	 * @param clss The composer class to be added to the manager.
	 * @param name The human-readable name of the composer class.
	 */
	public static synchronized void addClass( Class<? extends IComposerController> clss , String name )
	{
		composerMap.put(name, clss);
		fireToListeners();
		System.out.println( "Added: " + clss );
	}
	
	/**
	 * Removes an composer class from the composer.  Usually this is called by an OSGi bundle activator.
	 * @param clss The composer class to be removed from the manager.
	 * @param name The human-readable name of the composer class.
	 */
	public static synchronized void removeClass( Class<? extends IComposerController> clss , String name )
	{
		composerMap.remove( name );
		fireToListeners();
		System.out.println( "Removed: " + clss );
	}
	
	/**
	 * Fires a change notification to all registered listeners indicating that a composer has been added or removed.
	 */
	private static void fireToListeners()
	{
		for( final Runnable r : listenerSet )
		{
			r.run();
		}
	}
	
	/**
	 * Adds a listener to the manager.
	 * @param run The listener to be added.
	 */
	public static synchronized void addListener( Runnable run )
	{
		listenerSet.add( run );
	}
	
	/**
	 * Removes a listener from the manager.
	 * @param run The listener to be removed.
	 */
	public static synchronized void removeListener( Runnable run )
	{
		listenerSet.remove( run );
	}

	
}

