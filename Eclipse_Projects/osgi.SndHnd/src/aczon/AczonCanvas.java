




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







package aczon;

import java.awt.GraphicsConfiguration;
import java.util.Vector;

import javax.media.j3d.Canvas3D;

/**
 * An Aczon definition of a canvas.
 * 
 * @author tgreen
 *
 */
public class AczonCanvas extends Canvas3D {
	
	/**
	 * The list of resize vectors for the canvas.
	 */
	Vector<AczonResizeVect> resizeVects = new Vector<AczonResizeVect>();
	
	/**
	 * Adds a resize vector to the canvas.
	 * @param vect The input resize vector.
	 */
	public void addResizeVect( AczonResizeVect vect )
	{
		resizeVects.add( vect );
	}
	
	/**
	 * Fires events indicating the resizing of the window.
	 * @param x The X-Axis size of the window.
	 * @param y The Y-Axis size of the window.
	 */
	protected void fireResize( int x , int y )
	{
		final Vector<AczonResizeVect> resizeVects = this.resizeVects;
		final int sz = resizeVects.size();
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			resizeVects.elementAt( count ).handleResize( x , y );
		}
	}
	
	/**
	 * Constructs the canvas.
	 * @param _config The input graphics configuration.
	 */
	public AczonCanvas( GraphicsConfiguration _config )
	{
		super( _config );
	}

	@Override
	public void reshape( int a , int b , int c , int d )
	{
		super.reshape(a,b,c,d);
		fireResize( c , d );
	}
	
	@Override
	public void setBounds( int a , int b , int c , int d )
	{
		super.setBounds(a,b,c,d);
		fireResize( c , d );
	}
	
}

