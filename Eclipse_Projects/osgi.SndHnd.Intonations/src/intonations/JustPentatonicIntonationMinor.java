





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

/**
 * A just minor pentatonic intonation.
 * @author tgreen
 *
 */
public class JustPentatonicIntonationMinor extends PentatonicIntonation {
	
	public static final double JUST_TUNE_0 = 1.0;
	
	public static final double JUST_TUNE_1 = 6.0 / 5.0;
	
	public static final double JUST_TUNE_2 = 4.0 / 3.0;

	public static final double JUST_TUNE_3 = 3.0 / 2.0;
	
	public static final double JUST_TUNE_4 = 9.0 / 5.0;
	
	public static final double JUST_TUNE_5 = 2.0;
	
	/**
	 * Constructs the intonation.
	 */
	public JustPentatonicIntonationMinor()
	{
	}

	@Override
	public double[] calcIntonation() {
		final double[] ret = {
				JUST_TUNE_0 , JUST_TUNE_1 , JUST_TUNE_2 , JUST_TUNE_3 , JUST_TUNE_4 ,
				JUST_TUNE_5  };
		return( ret );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GJustPentatonicIntonationMinor wv = new GJustPentatonicIntonationMinor();
		s.put(this, wv);
		return( wv );
	}

	
}

