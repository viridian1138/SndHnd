





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

import core.Harmony;
import core.Intonation;

/**
 * Harmony for a power chord with one harmony pitch at the fifth,
 * and a second harmony pitch at the melodic interval (usually the octave).
 * 
 * @author tgreen
 *
 */
public class HarmonyPowerChord extends Harmony {
	
	/**
	 * Input intonation from which to determine the harmony.
	 */
	Intonation i1;
	
	/**
	 * The index into the input intonation at which to find the fifth.
	 */
	int fifthIndex;

	/**
	 * Constructs the harmony.
	 * @param _i1  Input intonation from which to determine the harmony.
	 * @param _fifthIndex  The index into the input intonation at which to find the fifth.
	 */
	public HarmonyPowerChord( Intonation _i1 , int _fifthIndex ) {
		i1 = _i1;
		fifthIndex = _fifthIndex;
	}

	@Override
	public double[] calcHarmony() {
		
		double[] h1 = i1.calcIntonation();
		
		double[] ret = { h1[ 0 ] , h1[ fifthIndex ] , h1[ h1.length - 1 ] };
		
		return( ret );
	}
	
	@Override
	public GHarmony genHarmony( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GHarmony)( s.get( this ) ) );
		}
		
		GHarmonyPowerChord wv = new GHarmonyPowerChord();
		wv.setI1( i1.genInton(s) );
		
		s.put(this, wv);
		return( wv );
	}

}

