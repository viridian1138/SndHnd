




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

import aazon.AazonListener;
import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;

/**
 * Produces a AbzonPathIteratorFactory for a line given vectors for the endpoints of the line.
 * 
 * @author tgreen
 *
 */
public class AbzonSmartLineFactory extends
		AbzonMutablePathIteratorFactory implements AazonListener {
	
	/**
	 * Vector for the first endpoint of the line.
	 */
	AazonVect b0;
	
	/**
	 * Vector for the second endpoint of the line.
	 */
	AazonVect b1;
	
	
	/**
	 * Private constructor.
	 * @param _b0 Vector for the first endpoint of the line.
	 * @param _b1 Vector for the second endpoint of the line.
	 */
	private AbzonSmartLineFactory( AazonVect _b0 , AazonVect _b1 )
	{
		b0 = _b0;
		b1 = _b1;
		
		if( b0 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b0 ).add( this );
		}
		
		if( b1 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b1 ).add( this );
		}
		
	}
	
	/**
	 * Handles a change in one of the vectors by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	@Override
	public AbzonImmutablePathIteratorFactory getImmutableFactory() {
		return( new AbzonImmutableLineFactory( AazonBufferedImmutableVect.construct( b0 ) , AazonBufferedImmutableVect.construct( b1 ) ) );
	}

	/**
	 * Returns a path iterator for the current state of the line.
	 * @param at The affine transform for the iterator.
	 * @param flatness The desired flatness for the iterator.
	 * @return The path iterator.
	 */
	public PathIterator iterator(AffineTransform at, double flatness) {
		return( getImmutableFactory().iterator(at, flatness) );
	}
	
	/**
	 * Returns a AbzonPathIteratorFactory for a line given vectors for the endpoints of the line.
	 * @param _b0 Vector for the first endpoint of the line.
	 * @param _b1 Vector for the second endpoint of the line.
	 * @return The AbzonPathIteratorFactory.
	 */
	public static AbzonPathIteratorFactory construct( AazonVect _b0 , AazonVect _b1 )
	{
		if( ( _b0 instanceof AazonImmutableVect ) && ( _b1 instanceof AazonImmutableVect ) )
		{
			return( new AbzonImmutableLineFactory( (AazonImmutableVect) _b0 , (AazonImmutableVect) _b1 ) );
		}
		
		return( new AbzonSmartLineFactory( _b0 , _b1 ) );
	}

}

