




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
import javax.media.j3d.LineArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

import aazon.vect.AazonImmutableVect;


/**
 * AazonEnt for an immutable outline of a rectangle.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableOutlineRectangle implements AazonImmutableEnt {

	/**
	 * Vector to the first corner of the rectangle.
	 */
	AazonImmutableVect a1;
	
	/**
	 * Vector to the second corner of the rectangle.
	 */
	AazonImmutableVect a2;
	
	/**
	 * The appearance of the outline of the rectangle.
	 */
	Appearance ctrlApp;
	
	/**
	 * Constructs the rectangle outline.
	 * @param _a1 Vector to the first corner of the rectangle.
	 * @param _a2 Vector to the second corner of the rectangle.
	 * @param _ctrlApp The appearance of the outline of the rectangle.
	 */
	public AazonImmutableOutlineRectangle( final AazonImmutableVect _a1 , final AazonImmutableVect _a2 , Appearance _ctrlApp )
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
		double x1 = a1.getX();
		double y1 = a1.getY();
		double x2 = a2.getX();
		double y2 = a2.getY();
		double xa = Math.min( x1 , x2 );
		double ya = Math.min( y1 , y2 );
		double xb = Math.max( x1 , x2 );
		double yb = Math.max( y1 , y2 );
		Point3d[] myCoords = new Point3d[8];
		myCoords[0] = new Point3d(xa,ya,0.0);
		myCoords[1] = new Point3d(xb,ya,0.0);
		myCoords[2] = new Point3d(xb,ya,0.0);
		myCoords[3] = new Point3d(xb,yb,0.0);
		myCoords[4] = new Point3d(xb,yb,0.0);
		myCoords[5] = new Point3d(xa,yb,0.0);
		myCoords[6] = new Point3d(xa,yb,0.0);
		myCoords[7] = new Point3d(xa,ya,0.0);

		LineArray larr = new LineArray(myCoords.length,
				GeometryArray.COORDINATES);
		larr.setCoordinates(0, myCoords);

		Shape3D line = new Shape3D(larr, ctrlApp);
		return(line);
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

