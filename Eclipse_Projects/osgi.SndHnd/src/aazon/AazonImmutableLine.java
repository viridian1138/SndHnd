




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
 * AazonEnt for an immutable line.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableLine implements AazonImmutableEnt {

	/**
	 * Vector to the first endpoint of the line.
	 */
	AazonImmutableVect a1;
	
	/**
	 * Vector to the second endpoint of the line.
	 */
	AazonImmutableVect a2;
	
	/**
	 * The appearance of the line.
	 */
	Appearance ctrlApp;
	
	/**
	 * 
	 * @param _a1 Vector to the first endpoint of the line.
	 * @param _a2 Vector to the second endpoint of the line.
	 * @param _ctrlApp The appearance of the line.
	 */
	public AazonImmutableLine( final AazonImmutableVect _a1 , final AazonImmutableVect _a2 , Appearance _ctrlApp )
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
		Point3d[] myCoords = new Point3d[2];
		myCoords[0] = new Point3d( a1.getX() , a1.getY() ,0.0);
		myCoords[1] = new Point3d( a2.getX() , a2.getY() ,0.0);

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

