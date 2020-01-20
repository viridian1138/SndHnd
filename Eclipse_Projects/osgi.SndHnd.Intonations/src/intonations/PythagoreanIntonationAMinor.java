





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

import greditinton.GIntonation;

import java.util.HashMap;

import core.WesternIntonation;

/**
 * A pythagorean intonation for a western 12-tone scale in the key of A-Minor.
 * 
 * @author tgreen
 *
 */
public class PythagoreanIntonationAMinor extends WesternIntonation {
	
	public static final double PYTHAGOREAN_TUNE_0 = 1.0;
	
	public static final double PYTHAGOREAN_TUNE_1 = 256.0 / 243.0;
	
	public static final double PYTHAGOREAN_TUNE_2 = 9.0 / 8.0;

	public static final double PYTHAGOREAN_TUNE_3 = 32.0 / 27.0;
	
	public static final double PYTHAGOREAN_TUNE_4 = 81.0 / 64.0;
	
	public static final double PYTHAGOREAN_TUNE_5 = 4.0 / 3.0;
	
	public static final double PYTHAGOREAN_TUNE_6 = 729.0 / 512.0;
	
	public static final double PYTHAGOREAN_TUNE_7 = 3.0 / 2.0;
	
	public static final double PYTHAGOREAN_TUNE_8 = 128.0 / 81.0;
	
	public static final double PYTHAGOREAN_TUNE_9 = 27.0 / 16.0;
	
	public static final double PYTHAGOREAN_TUNE_10 = 16.0 / 9.0;
	
	public static final double PYTHAGOREAN_TUNE_11 = 243.0 / 128.0;
	
	public static final double PYTHAGOREAN_TUNE_12 = 2.0;
	
	
	/**
	 * Constructs the intonation.
	 */
	public PythagoreanIntonationAMinor()
	{
	}

	@Override
	public double[] calcIntonation() {
		final double[] ret = {
				PYTHAGOREAN_TUNE_0 , PYTHAGOREAN_TUNE_1 , PYTHAGOREAN_TUNE_2 , PYTHAGOREAN_TUNE_3 , PYTHAGOREAN_TUNE_4 ,
				PYTHAGOREAN_TUNE_5 , PYTHAGOREAN_TUNE_6 , PYTHAGOREAN_TUNE_7 , PYTHAGOREAN_TUNE_8 , PYTHAGOREAN_TUNE_9 ,
				PYTHAGOREAN_TUNE_10 , PYTHAGOREAN_TUNE_11 , PYTHAGOREAN_TUNE_12 };
		return( ret );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GPythagoreanIntonationAMinor wv = new GPythagoreanIntonationAMinor();
		s.put(this, wv);
		return( wv );
	}

	
}

