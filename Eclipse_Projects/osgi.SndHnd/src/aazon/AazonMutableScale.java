




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


import javax.media.j3d.TransformGroup;

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;


/**
 * AazonEnt performing a mutable scaling of another AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonMutableScale extends AazonImmutableScale {
	
	/**
	 * Constructs the scaling.
	 * @param _ent The AazonEnt being scaled.
	 * @param _trans Vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 */
	public AazonMutableScale( AazonEnt _ent , AazonImmutableVect _trans )
	{
		super( _ent , _trans );
		
		objTranslate.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		objTranslate.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
	}
	
	/**
	 * Constructs the scaling for a zero scale.
	 * @param _ent The AazonEnt to be scaled.
	 */
	public AazonMutableScale( AazonEnt _ent )
	{
		this( _ent , new AazonBaseImmutableVect( 0.0 , 0.0 ) );
	}
	
	/**
	 * Sets the vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 * @param _trans Vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 */
	public void setTrans( AazonImmutableVect _trans )
	{
		trans = _trans;
		if( initCoord )
		{
			initCoord = false;
			init();
		}
	}

	
}

