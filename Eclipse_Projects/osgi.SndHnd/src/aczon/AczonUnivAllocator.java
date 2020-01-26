




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

/**
 * Allocates a new universe ID number to each Aczon universe, up to the signed 32-bit limit of about two billion Aczon universes.
 * 
 * @author tgreen
 *
 */
public class AczonUnivAllocator {
	
	/**
	 * The current allocation universe ID.
	 */
	private static int univ = 5;
	
	/**
	 * Returns a new allocation universe ID.
	 * @return The allocation universe ID.
	 */
	public static synchronized Object allocateUniv()
	{
		Integer ret = new Integer( univ );
		univ++;
		return( ret );
	}

}

