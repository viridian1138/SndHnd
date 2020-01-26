




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

import aazon.AazonListener;
import aazon.dbl.AazonDbl;
import aazon.dbl.AazonMutableDbl;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;

/**
 * Aczon double representing the scale factor for converting coordinates in the window due to e.g. window resizes.
 * 
 * @author tgreen
 *
 */
public class AczonScaleConvert extends AazonMutableDbl implements AazonListener {
	
	/**
	 * Resizing vector to be potentially used as a future expansion.
	 */
	protected AazonMutableVect resizeVect;
	
	/**
	 * Root factory providing platform defining coordinate transforms for the window.
	 */
	protected AczonRootFactory platform;
	
	/**
	 * Private constructor.
	 * @param _resizeVect Resizing vector to be potentially used as a future expansion.
	 * @param _platform Root factory providing platform defining coordinate transforms for the window.
	 */
	private AczonScaleConvert( AazonMutableVect _resizeVect , AczonRootFactory _platform )
	{
		resizeVect = _resizeVect;
		platform = _platform;
		resizeVect.add( this );
	}

	@Override
	public double getX() {
		AazonImmutableVect vct0 = AczonCoordinateConvert.convertCoords(0, 0, resizeVect, platform);
		AazonImmutableVect vct1 = AczonCoordinateConvert.convertCoords(1, 0, resizeVect, platform);
		return( Math.abs( vct1.getX() - vct0.getX() ) );
	}

	/**
	 * Listener firing events upon notification of a change.
	 */
	public void handleListen() {
		fire();
	}
	
	/**
	 * Static factory method for generating the scale factor.
	 * @param _resizeVect Resizing vector to be potentially used as a future expansion.
	 * @param _platform Root factory providing platform defining coordinate transforms for the window.
	 * @return Generated scale factor double.
	 */
	public static AazonDbl construct( AazonMutableVect _resizeVect , AczonRootFactory _platform )
	{	
		return( new AczonScaleConvert( _resizeVect , _platform ) );
	}

}
