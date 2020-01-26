




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







package palettes;

import gredit.GNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Defines state information for the palette used when editing an instance of class EditPackCoeff.
 * 
 * @author tgreen
 *
 */
public class PaletteClassesCoeff implements IPalette {
	
	/**
	 * Gets the set of classes defined for the creational tool of the palette.
	 * 
	 * @return The set of classes defined for the creational tool of the palette.
	 */
	public ArrayList<Class<? extends GNode>> getPaletteClasses()
	{
		ArrayList<Class<? extends GNode>> lst = new ArrayList<Class<? extends GNode>>();
		
		TreeMap<String,Class<? extends GNode>> tm = getMapClone();
		for( Entry<String,Class<? extends GNode>> e : tm.entrySet() )
		{
			Class<? extends GNode> clss = e.getValue();
			lst.add( clss );
		}
		
		return( lst );
	}
	
	/**
	 * Adds a listener to the palette state that is called whenever a palette class is added or deleted from the creation tool.
	 * 
	 * @param r The listener to be added.
	 */
	public void addPaletteListener( Runnable r )
	{
		addListener( r );
	}
	
	/**
	 * Removes a listener from the palette state.
	 * 
	 * @param r The listener to be removed.
	 */
	public void removePaletteListener( Runnable r )
	{
		removeListener( r );
	}
	
	
	/**
	 * Map from a human-readable name to the GNode class that can be instanced by the palette's creational tool.
	 */
	private final static TreeMap<String,Class<? extends GNode>> paletteMap = new TreeMap<String,Class<? extends GNode>>();
	
	/**
	 * The set of listeners that receive a notification whenever the paletteMap member changes.
	 */
	private final static HashSet<Runnable> listenerSet = new HashSet<Runnable>();
	
	
	/**
	 * Clones the map of GNode classes that can be instanced by the palette's creational tool.
	 * @return A clone of the map of GNode classes that can be instanced by the palette's creational tool.
	 */
	public static synchronized TreeMap<String,Class<? extends GNode>> getMapClone()
	{
		return( (TreeMap<String,Class<? extends GNode>>)( paletteMap.clone() ) );
	}
	
	/**
	 * Adds a GNode class to the palette state that can be instanced by the palette's creational tool.
	 * @param clss The GNode class to be added.
	 * @throws Throwable
	 */
	public static synchronized void addClass( Class<? extends GNode> clss ) throws Throwable
	{
		GNode gn = clss.newInstance();
		String name = gn.getName();
		paletteMap.put(name, clss);
		fireToListeners();
		System.out.println( "Added: " + clss );
	}
	
	/**
	 * Removes a GNode class from the palette state that can be instanced by the palette's creational tool.
	 * @param clss The GNode class to be removed.
	 * @throws Throwable
	 */
	public static synchronized void removeClass( Class<? extends GNode> clss ) throws Throwable
	{
		GNode gn = clss.newInstance();
		String name = gn.getName();
		paletteMap.remove( name );
		fireToListeners();
		System.out.println( "Removed: " + clss );
	}
	
	/**
	 * Fires a palette-change event to all listeners.
	 */
	private static void fireToListeners()
	{
		for( final Runnable r : listenerSet )
		{
			r.run();
		}
	}
	
	/**
	 * Adds a listener to the palette state that is called whenever a palette class is added or deleted from the creation tool.
	 * 
	 * @param run The listener to be added.
	 */
	public static synchronized void addListener( Runnable run )
	{
		listenerSet.add( run );
	}
	
	/**
	 * Removes a listener from the palette state.
	 * 
	 * @param run The listener to be removed.
	 */
	public static synchronized void removeListener( Runnable run )
	{
		listenerSet.remove( run );
	}

}

