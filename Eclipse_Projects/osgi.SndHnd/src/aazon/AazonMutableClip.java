




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

import javax.media.j3d.ModelClip;

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;


/**
 * Mutable AazonEnt that performs clipping on another AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonMutableClip extends AazonImmutableClip {
	
	/**
	 * Constructs the mutable clip.
	 * @param _ent The AazonEnt to be clipped.
	 * @param _p Vector to the clipping position.
	 * @param _wh Vector representing the clipping width and height.
	 */
	public AazonMutableClip( AazonEnt _ent , AazonImmutableVect _p , AazonImmutableVect _wh )
	{
		super( _ent , _p , _wh );
		
		clip.setCapability( ModelClip.ALLOW_ENABLE_READ );
		clip.setCapability( ModelClip.ALLOW_ENABLE_WRITE );
		
		clip.setCapability( ModelClip.ALLOW_PLANE_READ );
		clip.setCapability( ModelClip.ALLOW_PLANE_WRITE );
	}
	
	/**
	 * Constructs the mutable clip for an extremely large clipping region.
	 * @param _ent The AazonEnt to be clipped.
	 */
	public AazonMutableClip( AazonEnt _ent )
	{
		this( _ent , new AazonBaseImmutableVect( -10000.0 , -10000.0 ) , new AazonBaseImmutableVect( 20000.0 , 20000.0 ) );
	}
	
	/**
	 * Sets the parameters of the clipping.
	 * @param _p Vector to the clipping position.
	 * @param _wh Vector representing the clipping width and height.
	 */
	public void setClip( AazonImmutableVect _p , AazonImmutableVect _wh )
	{
		p = _p;
		wh = _wh;
		if( initClip )
		{
			initClip = false;
			init();
		}
	}

	
}

