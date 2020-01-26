




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









package dissurf;


/**
 * An X-Y coordinate in the domain of the dissonance surface.
 * 
 * @author tgreen
 *
 */
public class Coord {
	
	/**
	 * The X-Coordinate in the domain of the dissonance surface.
	 */
	int x;
	
	/**
	 * The Y-Coordinate in the domain of the dissonance surface.
	 */
	int y;
	
	/**
	 * Constructs the coordinate.
	 * @param _x The X-Coordinate in the domain of the dissonance surface.
	 * @param _y The Y-Coordinate in the domain of the dissonance surface.
	 */
	public Coord( int _x , int _y )
	{
		x = _x;
		y = _y;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coord other = (Coord) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	
	

}


