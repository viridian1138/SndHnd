




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







package aazon.builderNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import aazon.vect.AazonBaseMutableVect;


/**
 * Aazon utilities for determining which child nodes are transitively reachable from a particular node, and assigning initial locations.
 * 
 * @author tgreen
 *
 */
public class AazonTransChld {
	
	/**
	 * Returns BuilderNodes that are reachable from a single step of the transitivity chain.
	 * @param chld The initial starting object.
	 * @return The BuilderNodes that are reachable from a single step of the transitivity chain.
	 */
	public static BuilderNode[] getTransChld(Object chld) {
		if (chld == null) {
			return (new BuilderNode[0]);
		}

		if (chld instanceof BuilderNode[]) {
			return ((BuilderNode[]) chld);
		}
		
		if( chld instanceof BuilderNode )
		{
			BuilderNode ob = (BuilderNode) chld;
			BuilderNode[] ret = { ob };
			return( ret );
		}

		if (chld instanceof Object[]) {
			Object[] ob = (Object[]) chld;
			final int len = ob.length;
			BuilderNode[] ret = new BuilderNode[len];
			int count;
			for (count = 0; count < len; count++) {
				ret[count] = (BuilderNode) (ob[count]);
			}
			return (ret);
		}

		if (chld instanceof ArrayList) {
			ArrayList<BuilderNode> ob = (ArrayList<BuilderNode>) chld;
			final int len = ob.size();
			BuilderNode[] ret = new BuilderNode[len];
			int count;
			for (count = 0; count < len; count++) {
				ret[count] = ob.get(count); 
			}
			return (ret);
		}

		if (chld instanceof Vector) {
			Vector<BuilderNode> ob = (Vector<BuilderNode>) chld;
			final int len = ob.size();
			BuilderNode[] ret = new BuilderNode[len];
			int count;
			for (count = 0; count < len; count++) {
				ret[count] = ob.elementAt(count); 
			}
			return (ret);
		}
		
		System.out.println( chld );
		throw( new RuntimeException( "Not Supported" ) );
	}
	
	
	/**
	 * Generates child numbering for the transitive node set.
	 * @param s The set of nodes to traverse.
	 * @param level The number of the current child level.
	 * @param n The initial starting node.
	 * @param nodeMap The output map of nodes associating nodes to child levels.
	 */
	private static void initialChldNum( final HashSet<BuilderNode> s , final int level , final BuilderNode n , final HashMap<BuilderNode,Integer> nodeMap )
	{
		if( s.contains( n ) )
		{
			return;
		}
		
		s.add( n );
		
		Integer ia = (Integer)( nodeMap.get( n ) );
		
		if( ia == null )
		{
			nodeMap.put( n , new Integer( level ) );
		}
		else
		{
			if( ia.intValue() < level )
			{
				nodeMap.put( n , new Integer( level ) );
			}
		}
		
		BuilderNode[] nodes = getTransChld( n.getChldNodes() );
		int count;
		int len = nodes.length;
		
		for( count = 0 ; count < len ; count++ )
		{
			initialChldNum( s , level + 1 , nodes[ count ] , nodeMap );
		}
	}
	
	
	/**
	 * Assigns initial coordinate positions to each node.
	 * @param univ The universe ID of the nodes.
	 * @param s The set of nodes for which to assign initial positions.
	 * @param rand Random number generator.
	 * @param n Initial starting node.
	 */
	public static void initialCoords( Object univ , HashSet<BuilderNode> s , Random rand , BuilderNode n )
	{
		
		HashMap<BuilderNode,Integer> nodeMap = new HashMap<BuilderNode,Integer>();
		
		HashSet<BuilderNode> sa = new HashSet<BuilderNode>();
		initialChldNum( sa , 0 , n , nodeMap );
		
		for( final BuilderNode node : s )
		{
			if( !( sa.contains( node ) ) )
			{
				nodeMap.put(node, new Integer(0));
			}
		}
		
		for( final BuilderNode b : sa )
		{
			s.add( b );
		}
		
		TreeMap<Integer,Vector<BuilderNode>> trs = new TreeMap<Integer,Vector<BuilderNode>>();
		for( final Entry<BuilderNode,Integer> e : nodeMap.entrySet() )
		{
			final BuilderNode nd = e.getKey();
			final Integer lev = e.getValue();
			Vector<BuilderNode> vect = trs.get( lev );
			if( vect == null )
			{
				vect = new Vector<BuilderNode>();
				trs.put(lev, vect);
			}
			vect.add( nd );
		}
		
		for( Entry<Integer,Vector<BuilderNode>> e : trs.entrySet() )
		{
			final Integer k = e.getKey();
			final int ki = k.intValue();
			Vector<BuilderNode> vct = e.getValue();
			final int len = vct.size();
			int count;
			for( count = 0 ; count < len ; count++ )
			{
				BuilderNode nd = vct.elementAt( count );
				
				final AazonBaseMutableVect vect = nd.getCenterVect(univ, AazonDefaultCenterVectLocationFactory.getFact() );
				
				final double x = 0.3 * ki - 0.9;
				
				final double y = -0.15 * ki - 0.3 * count + 0.9;
				
				vect.setCoords( x , y );
			}
			
		}
		
	}
	
	/**
	 * Collects the transitive set of nodes reachable for a particular BuilderNode.
	 * @param s The output set into which to collect the nodes.
	 * @param n The input initial start node.
	 */
	public static void collectBuilderNodes( HashSet<BuilderNode> s , BuilderNode n )
	{
		System.out.println( "RunB..." );
		if( s.contains( n ) )
		{
			return;
		}
		
		s.add( n );
		
		BuilderNode[] nodes = getTransChld( n.getChldNodes() );
		int count;
		int len = nodes.length;
		
		for( count = 0 ; count < len ; count++ )
		{
			collectBuilderNodes( s , nodes[ count ] );
		}
	}
	
	

}


