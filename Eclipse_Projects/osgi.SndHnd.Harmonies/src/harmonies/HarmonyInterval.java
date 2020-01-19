





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

import greditharmon.GHarmony;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import core.Harmony;
import core.Intonation;

/**
 * A two-part harmony with the pitch ratio between the tonic and the upper
 * pitch at a specified interval ratio.
 * 
 * @author tgreen
 *
 */
public class HarmonyInterval extends Harmony {
	
	/**
	 * Input intonation.
	 */
	Intonation i1;
	
	/**
	 * The pitch ratio for the two-part harmony.
	 */
	double val;
	
	/**
	 * The name to be given to the harmony pitch.
	 */
	String valName;

	/**
	 * Constructs the harmony.
	 * @param _i1 Input intonation.
	 * @param _val  The pitch ratio for the two-part harmony.
	 * @param _valName  The name to be given to the harmony pitch.
	 */
	public HarmonyInterval( Intonation _i1 , double _val , String _valName ) {
		i1 = _i1;
		val = _val;
		valName = _valName;
	}

	@Override 
	public double[] calcHarmony() {
		
		double[] ret = { 1.0 , val };
		
		return( ret );
	}
	
	@Override
	public GHarmony genHarmony( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GHarmony)( s.get( this ) ) );
		}
		
		GHarmonyInterval wv = new GHarmonyInterval();
		wv.setI1( i1.genInton(s) );
		wv.setVal( val );
		wv.setValName( valName );
		
		s.put(this, wv);
		return( wv );
	}

}

