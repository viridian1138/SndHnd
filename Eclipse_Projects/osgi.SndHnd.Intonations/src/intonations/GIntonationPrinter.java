





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







package intonations;

import gredit.GNode;
import greditinton.GIntonation;

import java.util.HashMap;

import core.Intonation;

/**
 * Node for printing the contents of an intonation to the console upon the action to edit the node's properties.
 * @author tgreen
 *
 */
public class GIntonationPrinter extends GNode {
	
	/**
	 * The input intonation.
	 */
	protected GIntonation inton = null;

	/**
	 * Constructs the node.
	 */
	public GIntonationPrinter() {
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context ) throws Throwable
	{
		final String[] scaleNames = inton.getScaleNames();
		final Intonation intonation = inton.genInton( context );
		final double[] scale = intonation.calcIntonation();
		if( scale.length != ( scaleNames.length + 1 ) )
		{
			throw( new RuntimeException( "Inconsistent" ) );
		}
		System.out.println( "***Scale***" );
		int count;
		for( count = 0 ; count < scaleNames.length ; count++ )
		{
			String str = count + " : " + ( scaleNames[ count ] ) + " : " + ( scale[ count ] );
			System.out.println( str );
		}
		String str = count + " : " + ( scaleNames[ 0 ] ) + " : " + ( scale[ count ] );
		System.out.println( str );
		System.out.println( "***PriNames***" );
		String[] priNames = inton.getPriScaleNames();
		for( count = 0 ; count < priNames.length ; count++ )
		{
			System.out.println( priNames[ count ] );
		}
		System.out.println( "***End***" );
	}

	@Override
	public Object genObj(HashMap s) {
		throw( new RuntimeException( "NotSupported" ) );
	}

	@Override
	public String getName() {
		return( "IntonationPrinter" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		inton = (GIntonation) in;
	}

	@Override
	public void removeChld() {
		inton = null;
	}

	public Object getChldNodes() {
		return( inton );
	}

	
}

