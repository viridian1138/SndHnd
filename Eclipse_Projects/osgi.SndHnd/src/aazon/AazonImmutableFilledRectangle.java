




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









package aazon;



import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;

import aazon.vect.AazonImmutableVect;


/**
 * AazonEnt for an immutable filled rectangle.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableFilledRectangle implements AazonImmutableEnt {

	/**
	 * Vector to the first corner of the rectangle.
	 */
	AazonImmutableVect a1;
	
	/**
	 * Vector to the second corner of the rectangle.
	 */
	AazonImmutableVect a2;
	
	/**
	 * The appearance of the filled rectangle.
	 */
	Appearance ctrlApp;
	
	/**
	 * Constructs the filled rectangle.
	 * @param _a1 Vector to the first corner of the rectangle.
	 * @param _a2 Vector to the second corner of the rectangle.
	 * @param _ctrlApp The appearance of the filled rectangle.
	 */
	public AazonImmutableFilledRectangle( final AazonImmutableVect _a1 , final AazonImmutableVect _a2 , Appearance _ctrlApp )
	{
		a1 = _a1;
		a2 = _a2;
		ctrlApp = _ctrlApp;
	}
	
	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		final int triFormat = GeometryArray.COORDINATES;
		double x1 = a1.getX();
		double y1 = a1.getY();
		double x2 = a2.getX();
		double y2 = a2.getY();
		double xa = Math.min( x1 , x2 );
		double ya = Math.min( y1 , y2 );
		double xb = Math.max( x1 , x2 );
		double yb = Math.max( y1 , y2 );
		TriangleArray tris = new TriangleArray(6, triFormat);
		final double[] vert2 = { xa, ya, 0, // v1
				xb, ya, 0, // v2
				xb, yb, 0, // v3
				xa, ya, 0, // v4
				xa, yb, 0, // v5
				xb, yb, 0, // v6
		};
		tris.setCoordinates(0, vert2);
		Shape3D rect = new Shape3D(tris, ctrlApp);
		return( rect );
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		// Does Nothing.
	}

	
}

