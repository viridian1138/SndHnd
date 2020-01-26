




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
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Group;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

import aazon.AazonImmutableEnt;
import aazon.dbl.AazonImmutableDbl;

/**
 * Abzon class for an immutable shape.
 * 
 * @author tgreen
 *
 */
public class AbzonImmutableShape implements AazonImmutableEnt {
	
	/**
	 * The input path iterator factory for the shape.
	 */
	AbzonImmutablePathIteratorFactory fac;
	
	/**
	 * The desired flatness for the iterator.
	 */
	AazonImmutableDbl flatness;
	
	/**
	 * The input affine transform of the shape.  NOTE: it is assumed that the affine transform will remain unchanged throughout the lifetime of the shape.
	 */
	AffineTransform at;
	
	/**
	 * The input Appearance of the shape.  NOTE: it is assumed that the Appearance will remain unchanged throughout the lifetime of the shape.
	 */
	Appearance app;
	
	/**
	 * Constructs the class.
	 * @param _fac The input path iterator factory for the shape.
	 * @param d The desired flatness for the iterator.
	 * @param _at The input affine transform of the shape.  NOTE: it is assumed that the affine transform will remain unchanged throughout the lifetime of the shape.
	 * @param _app The input Appearance of the shape.  NOTE: it is assumed that the Appearance will remain unchanged throughout the lifetime of the shape.
	 */
	public AbzonImmutableShape( AbzonImmutablePathIteratorFactory _fac , AazonImmutableDbl d , AffineTransform _at , Appearance _app )
	{
		fac = _fac;
		flatness = d;
		at = _at;
		app = _app;
	}

	/**
	 * Generates the node representing the shape.
	 * @return The node representing the shape.
	 */
	public Node getNode() {
		final double flatness = this.flatness.getX();
		
		PathIterator it = fac.iterator(at, flatness);
		
		int cnt = 0;
		double[] tcoords = new double[ 2 ];
		ArrayList<Integer> vect = new ArrayList<Integer>();
		
		while( !( it.isDone() ) )
		{
			int cval = it.currentSegment( tcoords );
			if( cval == PathIterator.SEG_MOVETO )
			{
				vect.add( new Integer( cnt ) );
			}
			cnt++;
			it.next();
		}
		
		
		it = fac.iterator(at, flatness);
		
		
		Point3d[] myCoords = new Point3d[cnt];
		int cc = 0;
		
		Point3d start = null;
		
				
		while( !( it.isDone() ) )
		{
			int cval = it.currentSegment( tcoords );
			switch( cval )
			{
			case PathIterator.SEG_CLOSE:
				myCoords[ cc ] = start;
				break;
				
			case PathIterator.SEG_LINETO:
				myCoords[ cc ] = new Point3d( tcoords[ 0 ] , tcoords[ 1 ] , 0.0 );
				break;
				
			case PathIterator.SEG_MOVETO:
				myCoords[ cc ] = new Point3d( tcoords[ 0 ] , tcoords[ 1 ] , 0.0 );
				start = myCoords[ cc ];
				break;
			
			default:
				throw( new RuntimeException( "" + cval ) );
			}
			
			cc++;
			it.next();
		}
		
		final int vsz = vect.size();
		final int vsza = vsz - 1;
		int[] strip_counts = new int[ vsz ];
		int count;
		for( count = 0 ; count < vsza ; count++ )
		{
			Integer ia = vect.get( count );
			Integer ib = vect.get( count + 1 );
			strip_counts[ count ] = ib.intValue() - ia.intValue();
		}
		if( vsza >= 0 )
		{
			Integer ia = vect.get( vsza );
			strip_counts[ vsza ] = cnt - ia.intValue();
		}
		
		if( myCoords.length == 0 )
		{
			return( new Group() );
		}
		

		LineStripArray larr = new LineStripArray(myCoords.length,
				GeometryArray.COORDINATES, strip_counts );
		
		larr.setCoordinates(0, myCoords);
		
		Shape3D line = new Shape3D(larr, app);
		return(line);
	}
	
	/**
	 * Adds the self-active parts of this shape to an input ArrayList.
	 * @param in The input ArrayList.
	 */
	public void genSelfActive( ArrayList in )
	{
		// Does Nothing
	}

	
}

