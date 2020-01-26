




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
 * AazonEnt for an immutable control point.
 * 
 * @author tgreen
 *
 */
public class AazonImmutableControlPoint implements AazonImmutableEnt {

	/**
	 * Vector to the center of the control point.
	 */
	AazonImmutableVect a;
	
	/**
	 * The appearance of the control point.
	 */
	Appearance ctrlApp;
	
	/**
	 * Constructs the control point.
	 * @param _a Vector to the center of the control point.
	 * @param _ctrlApp The appearance of the control point.
	 */
	public AazonImmutableControlPoint( final AazonImmutableVect _a , Appearance _ctrlApp )
	{
		a = _a;
		ctrlApp = _ctrlApp;
	}
	
	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		final int triFormat = GeometryArray.COORDINATES;
		TriangleArray tris = new TriangleArray(6, triFormat);
		final double x1 = a.getX();
		final double y1 = a.getY();
		final double[] vert2 = { -0.005+x1, -0.005+y1, 0, // v1
				0.005+x1, -0.005+y1, 0, // v2
				0.005+x1, 0.005+y1, 0, // v3
				-0.005+x1, -0.005+y1, 0, // v4
				-0.005+x1, 0.005+y1, 0, // v5
				0.005+x1, 0.005+y1, 0, // v6
		};
		tris.setCoordinates(0, vert2);
		Shape3D controlPoint = new Shape3D(tris, ctrlApp);
		return( controlPoint );
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

