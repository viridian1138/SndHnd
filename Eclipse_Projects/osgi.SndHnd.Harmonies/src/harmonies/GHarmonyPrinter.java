





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







package harmonies;

import gredit.GNode;
import greditharmon.GHarmony;

import java.util.HashMap;

import core.Harmony;

/**
 * Node for printing the contents of a harmony to the console upon the action to edit the node's properties.
 * @author tgreen
 *
 */
public class GHarmonyPrinter extends GNode {
	
	/**
	 * The input harmony.
	 */
	protected GHarmony harmon = null;

	/**
	 * Constructs the node.
	 */
	public GHarmonyPrinter() {
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context ) throws Throwable
	{
		final String[] harmonyNames = harmon.getHarmonyNames();
		final Harmony harmony = harmon.genHarmony( context );
		final double[] harmonyValues = harmony.calcHarmony();
		if( harmonyValues.length != harmonyNames.length )
		{
			System.out.println( harmonyNames.length );
			System.out.println( harmonyValues.length );
			throw( new RuntimeException( "Inconsistent" ) );
		}
		System.out.println( "***Harmony***" );
		int count;
		for( count = 0 ; count < harmonyNames.length ; count++ )
		{
			String str = count + " : " + ( harmonyNames[ count ] ) + " : " + ( harmonyValues[ count ] );
			System.out.println( str );
		}
		System.out.println( "***End***" );
	}

	@Override
	public Object genObj(HashMap s) {
		throw( new RuntimeException( "NotSupported" ) );
	}

	@Override
	public String getName() {
		return( "HarmonyPrinter" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GHarmony );
	}

	@Override
	public void performAssign(GNode in) {
		harmon = (GHarmony) in;
	}

	@Override
	public void removeChld() {
		harmon = null;
	}

	public Object getChldNodes() {
		return( harmon );
	}

	
}

