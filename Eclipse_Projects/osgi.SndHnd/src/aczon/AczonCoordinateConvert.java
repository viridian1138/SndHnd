




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

import javax.vecmath.Point3d;

import aazon.AazonListener;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;

public class AczonCoordinateConvert extends AazonMutableVect implements AazonListener {
	
	/**
	 * The vector to be converted.
	 */
	protected AazonVect a;
	
	/**
	 * Resizing vector to be potentially used as a future expansion.
	 */
	protected AazonMutableVect resizeVect;
	
	/**
	 * Root factory providing platform defining coordinate transforms for the window.
	 */
	protected AczonRootFactory platform;
	
	/**
	 * Temporary vector.
	 */
	protected AazonImmutableVect tmp = null;
	
	
	/**
	 * Private constructor.
	 * @param _a The vector to be converted.
	 * @param _resizeVect Resizing vector to be potentially used as a future expansion.
	 * @param _platform Root factory providing platform defining coordinate transforms for the window.
	 */
	private AczonCoordinateConvert( AazonVect _a , AazonMutableVect _resizeVect , AczonRootFactory _platform )
	{
		a = _a;
		resizeVect = _resizeVect;
		platform = _platform;
		
		if( a instanceof AazonMutableVect )
		{
			(  (AazonMutableVect) a ).add( this );
		}
		
		resizeVect.add( this );
	}
	
	/**
	 * Static factory for generating the coordinate transform.
	 * @param _a The vector to be converted.
	 * @param _resizeVect Resizing vector to be potentially used as a future expansion.
	 * @param _platform Root factory providing platform defining coordinate transforms for the window.
	 * @return Generated coordinate conversion vector.
	 */
	public static AazonVect construct( AazonVect _a , AazonMutableVect _resizeVect , AczonRootFactory _platform )
	{
		return( new AczonCoordinateConvert( _a , _resizeVect , _platform ) );
	}

	@Override
	public double getX() {
		
		if( tmp == null )
		{
			tmp = convertCoords( (int)( a.getX() ) , (int)( a.getY() ) , resizeVect , platform );
		}
		
		return( tmp.getX() );
	}

	@Override
	public double getY() {
		
		if( tmp == null )
		{
			tmp = convertCoords( (int)( a.getX() ) , (int)( a.getY() ) , resizeVect , platform );
		}
		
		return( tmp.getY() );
	}
	
	/**
	 * Listener firing events upon notification of a change.
	 */
	public void handleListen()
	{
		tmp = null;
		fire();
	}
	
	/**
	 * Converts the input x-y point to universe coordinates.
	 * @param x The X-coordinate of the input point.
	 * @param y The Y-coordinate of the input point.
	 * @param resizeVect Resizing vector to be potentially used as a future expansion.
	 * @param platform Root factory providing platform defining coordinate transforms for the window.
	 * @return The universe coordinate location of the point.
	 */
	public static AazonImmutableVect convertCoords( final int x , final int y , AazonMutableVect resizeVect , AczonRootFactory platform )
	{
		/* double rx = Math.max( resizeVect.getX() , 1.0 );
		double ry = Math.max( resizeVect.getY() , 1.0 );
		
		final double cx = rx / 2.0;
		final double cy = ry / 2.0;
		final double scale = Math.min( cx , cy );
		
		final double ox = ( x - cx ) / scale;
		final double oy = - ( y - cy ) / scale;
		
		return( new AazonBaseImmutableVect( ox , oy ) ); */
		
		Point3d pointOut = platform.translatePoint(x, y);
		
		/* System.out.println( "x : " + ox + " " + ( pointOut.x ) );
		System.out.println( "y : " + oy + " " + ( pointOut.y ) ); */
		
		return( new AazonBaseImmutableVect( pointOut.x , pointOut.y ) );
	}
	
	/**
	 * Converts the input x-y point to universe coordinates.
	 * @param x The X-coordinate of the input point.
	 * @param y The Y-coordinate of the input point.
	 * @param platform Root factory providing platform defining coordinate transforms for the window.
	 * @return The universe coordinate location of the point.
	 */
	public static Point3d convertCoords( final int x , final int y , AczonRootFactory platform )
	{
		/* double rx = Math.max( resizeVect.getX() , 1.0 );
		double ry = Math.max( resizeVect.getY() , 1.0 );
		
		final double cx = rx / 2.0;
		final double cy = ry / 2.0;
		final double scale = Math.min( cx , cy );
		
		final double ox = ( x - cx ) / scale;
		final double oy = - ( y - cy ) / scale;
		
		return( new AazonBaseImmutableVect( ox , oy ) ); */
		
		Point3d pointOut = platform.translatePoint(x, y);
		
		/* System.out.println( "x : " + ox + " " + ( pointOut.x ) );
		System.out.println( "y : " + oy + " " + ( pointOut.y ) ); */
		
		return( pointOut );
	}

	
}

