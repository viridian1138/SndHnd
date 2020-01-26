




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
import java.awt.geom.PathIterator;

/**
 * A mutable AbzonMutablePathIteratorFactory for a general path.
 * 
 * @author tgreen
 *
 */
public class AbzonMutableGeneralPathFactory extends
		AbzonMutablePathIteratorFactory {
	
	/**
	 * The current state of the mutable factory.
	 */
	AbzonImmutableGeneralPathFactory crv;
	
	/**
	 * Constructs the mutable factory.
	 * @param _crv The initial state of the mutable factory.
	 */
	public AbzonMutableGeneralPathFactory( AbzonImmutableGeneralPathFactory _crv )
	{
		crv = _crv;
	}

	/**
	 * Returns a path iterator for the current state of the factory.
	 * @param at The affine transform for the iterator.
	 * @param flatness The desired flatness for the iterator.
	 * @return The path iterator.
	 */
	public PathIterator iterator(AffineTransform at, double flatness) {
		return( crv.iterator(at, flatness) );
	}
	
	/**
	 * Sets the current state of the mutable factory.
	 * @param _crv The current state of the mutable factory.
	 */
	public void setCrv( AbzonImmutableGeneralPathFactory _crv )
	{
		crv = _crv;
		fire();
	}
	
	@Override
	public AbzonImmutablePathIteratorFactory getImmutableFactory()
	{
		return( crv );
	}

	
}

