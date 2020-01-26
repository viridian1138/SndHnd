




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







package aczon;

import aazon.vect.AazonVect;

/**
 * Class for checking whether a point is inside a box.
 * 
 * @author tgreen
 *
 */
public class AczonBoxCheck {
	
	/**
	 * Checks whether a point is inside a box.
	 * @param v The point to be checked.
	 * @param b0 The bottom-left of the box.
	 * @param b1 The top-right of the box.
	 * @return Whether the point is inside the box.
	 */
	public static boolean isInside( AazonVect v , AazonVect b0 , AazonVect b1 )
	{
		final double vx = v.getX();
		final double vy = v.getY();
		
		final double b0x = b0.getX();
		final double b0y = b0.getY();
		
		final double b1x = b1.getX();
		final double b1y = b1.getY();
		
		/* System.out.println( "++----------------------" );
		System.out.println( vx );
		System.out.println( vy );
		System.out.println( b0x );
		System.out.println( b0y );
		System.out.println( b1x );
		System.out.println( b1y ); */
		
		final double x0 = Math.min(b0x, b1x);
		final double x1 = Math.max(b0x, b1x);
		
		final double y0 = Math.min(b0y, b1y);
		final double y1 = Math.max(b0y, b1y);
		
		return( ( vx >= x0 ) && ( vx <= x1 ) && ( vy >= y0 ) && ( vy <= y1 ) );
	}

}

