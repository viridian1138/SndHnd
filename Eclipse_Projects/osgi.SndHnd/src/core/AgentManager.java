




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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Manages the list of defined agent classes (subclasses of IntelligentAgent).  OSGi bundle activators have the responsibility to add/remove agent classes to/from this class in most cases.
 * 
 * @author tgreen
 *
 */
public class AgentManager {
	
	/**
	 * The map of currently-defined agent classes indexed by human-readable name.  TreeMap is used so that
	 * the agent classes are indexed in alphabetical order by human-readable name.
	 */
	private final static TreeMap<String,Class<? extends IntelligentAgent>> agentMap = new TreeMap<String,Class<? extends IntelligentAgent>>();
	
	/**
	 * The set of listeners to be notified when an agent class is added/removed.
	 */
	private final static HashSet<Runnable> listenerSet = new HashSet<Runnable>();
	
	/**
	 * Gets a clone of the map of currently-defined agent classes,
	 * @return A clone of the map of currently-defined agent classes,
	 */
	public static synchronized TreeMap<String,Class<? extends IntelligentAgent>> getMapClone()
	{
		return( (TreeMap<String,Class<? extends IntelligentAgent>>)( agentMap.clone() ) );
	}
	
	/**
	 * Adds an agent class to the manager.  Usually this is called by an OSGi bundle activator.
	 * @param clss The agent class to be added to the manager.
	 * @param name The human-readable name of the agent class.
	 */
	public static synchronized void addClass( Class<? extends IntelligentAgent> clss , String name )
	{
		agentMap.put(name, clss);
		fireToListeners();
		System.out.println( "Added: " + clss );
	}
	
	/**
	 * Removes an agent class from the manager.  Usually this is called by an OSGi bundle activator.
	 * @param clss The agent class to be removed from the manager.
	 * @param name The human-readable name of the agent class.
	 */
	public static synchronized void removeClass( Class<? extends IntelligentAgent> clss , String name )
	{
		agentMap.remove( name );
		fireToListeners();
		System.out.println( "Removed: " + clss );
	}
	
	/**
	 * Fires a change notification to all registered listeners indicating that an agent has been added or removed.
	 */
	private static void fireToListeners()
	{
		for( Runnable r : listenerSet )
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
	
	/**
	 * Clears rough-draft waveform maps from all registered agents.
	 */
	public static void clearMaps()
	{
		for( Entry<String,Class<? extends IntelligentAgent>> e : agentMap.entrySet() )
		{
			Class<? extends IntelligentAgent> clss = e.getValue();
			try
			{
				Field fd = clss.getField( "roughDraftWaveformMap" );
		    	HashMap<String,WaveForm> hm = (HashMap<String,WaveForm>)( fd.get( null ) );
		    	hm.clear();
			}
			catch( Throwable ex )
			{
				ex.printStackTrace( System.out );
			}
		}
	}

	
}

