




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
 * Produces a AbzonPathIteratorFactory for a cubic Bezier curve given the Bezier points of the curve.
 * 
 * @author tgreen
 *
 */
public class AbzonSmartCubicCurveFactory extends
		AbzonMutablePathIteratorFactory implements AazonListener {
	
	/**
	 * Vector for the b0 Bezier point.
	 */
	AazonVect b0;
	
	/**
	 * Vector for the b1 Bezier point.
	 */
	AazonVect b1;
	
	/**
	 * Vector for the b2 Bezier point.
	 */
	AazonVect b2;
	
	/**
	 * Vector for the b3 Bezier point.
	 */
	AazonVect b3;
	
	
	/**
	 * Private constructor.
	 * @param _b0 Vector for the b0 Bezier point.
	 * @param _b1 Vector for the b1 Bezier point.
	 * @param _b2 Vector for the b2 Bezier point.
	 * @param _b3 Vector for the b3 Bezier point.
	 */
	private AbzonSmartCubicCurveFactory( AazonVect _b0 , AazonVect _b1 , AazonVect _b2 , AazonVect _b3 )
	{
		b0 = _b0;
		b1 = _b1;
		b2 = _b2;
		b3 = _b3;
		
		if( b0 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b0 ).add( this );
		}
		
		if( b1 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b1 ).add( this );
		}
		
		if( b2 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b2 ).add( this );
		}
		
		if( b3 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b3 ).add( this );
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
		return( new AbzonImmutableCubicCurveFactory( AazonBufferedImmutableVect.construct( b0 ) , AazonBufferedImmutableVect.construct( b1 ) , 
				AazonBufferedImmutableVect.construct( b2 ) , AazonBufferedImmutableVect.construct( b3 ) ) );
	}
	
	/**
	 * Returns a path iterator for the current state of the Bezier curve.
	 * @param at The affine transform for the iterator.
	 * @param flatness The desired flatness for the iterator.
	 * @return The path iterator.
	 */
	public PathIterator iterator(AffineTransform at, double flatness) {
		return( getImmutableFactory().iterator(at, flatness) );
	}
	
	/**
	 * Produces a AbzonPathIteratorFactory for a cubic Bezier curve given the Bezier points of the curve.
	 * @param _b0 Vector for the b0 Bezier point.
	 * @param _b1 Vector for the b1 Bezier point.
	 * @param _b2 Vector for the b2 Bezier point.
	 * @param _b3 Vector for the b3 Bezier point.
	 * @return The AbzonPathIteratorFactory.
	 */
	public static AbzonPathIteratorFactory construct( AazonVect _b0 , AazonVect _b1 , AazonVect _b2 , AazonVect _b3 )
	{
		if( ( _b0 instanceof AazonImmutableVect ) && ( _b1 instanceof AazonImmutableVect ) && ( _b2 instanceof AazonImmutableVect ) && ( _b3 instanceof AazonImmutableVect ) )
		{
			return( new AbzonImmutableCubicCurveFactory( (AazonImmutableVect) _b0 , (AazonImmutableVect) _b1 , (AazonImmutableVect) _b2 , (AazonImmutableVect) _b3 ) );
		}
		
		return( new AbzonSmartCubicCurveFactory( _b0 , _b1 , _b2 , _b3 ) );
	}

	
}

