





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

/**
 * A harmony that returns a typical triad for testing purposes.
 * 
 * @author tgreen
 *
 */
public class TestTriad extends Harmony {

	/**
	 * Constructs the harmony.
	 */
	public TestTriad() {
	}

	@Override
	public double[] calcHarmony() {
		final double[] ret = { 1.0 , 1.25 , 1.7 };
		return( ret );
	}
	
	@Override
	public GHarmony genHarmony( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GHarmony)( s.get( this ) ) );
		}
		
		GHarmony wv = new GTestTriad();
		s.put(this, wv);
		return( wv );
	}

	
}

