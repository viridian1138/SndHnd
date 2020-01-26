




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







package abzon;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * An immutable factory for producing path iterators from a GeneralPath.
 * 
 * @author tgreen
 *
 */
public class AbzonImmutableGeneralPathFactory implements
		AbzonImmutablePathIteratorFactory {
	
	/**
	 * The input GeneralPath.  NOTE: it is assumed that the GeneralPath will remain unchanged throughout the lifetime of the factory.
	 */
	protected GeneralPath gp;
	
	/**
	 * Constructs the class.
	 * @param _gp The input GeneralPath.  NOTE: it is assumed that the GeneralPath will remain unchanged throughout the lifetime of the factory.
	 */
	public AbzonImmutableGeneralPathFactory( GeneralPath _gp )
	{
		gp = _gp;
	}

	/**
	 * Returns a path iterator for the general path.
	 * @param at The affine transform for the iterator.
	 * @param flatness The desired flatness for the iterator.
	 * @return The path iterator.
	 */
	public PathIterator iterator(AffineTransform at, double flatness) {
		return( gp.getPathIterator(at, flatness) );
	}

	
}

