




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

import javax.media.j3d.Node;

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;


/**
 * AazonEnt performing a mutable scaling of another AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonSmartScale implements AazonImmutableEnt, AazonListener {
	
	/**
	 * The mutable scaling.
	 */
	protected AazonMutableScale trans;
	
	/**
	 * Vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 */
	protected AazonMutableVect vect;
	
	/**
	 * Private constructor.
	 * @param _ent The AazonEnt being scaled.
	 * @param _vect Vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 */
	private AazonSmartScale( AazonEnt _ent , AazonMutableVect _vect )
	{
			vect = _vect;
			trans = new AazonMutableScale( _ent , AazonBufferedImmutableVect.construct( _vect ) );
			vect.add( this );
	}
	
	/**
	 * Returns an AazonEnt performing a scaling of another AazonEnt.
	 * @param _ent The AazonEnt being scaled.
	 * @param _vect Vector holding the coordinate scaling for the X-Axis and the Y-Axis.
	 * @return AazonEnt performing a scaling of another AazonEnt.
	 */
	public static AazonEnt construct( AazonEnt _ent , AazonVect _vect )
	{
		if( _vect instanceof AazonMutableVect )
		{
			return( new AazonSmartScale( _ent , (AazonMutableVect) _vect ) );
		}
		else
		{
			return( new AazonImmutableScale( _ent , (AazonImmutableVect) _vect ) );
		}
	}
	
	/**
	 * Returns an AazonEnt performing a scaling of another AazonEnt.
	 * @param _ent The AazonEnt being scaled.
	 * @param _scale The scale factor.
	 * @return AazonEnt performing a scaling of another AazonEnt.
	 */
	public static AazonEnt construct( AazonEnt _ent , double _scale )
	{
		return( construct( _ent , new AazonBaseImmutableVect( _scale , _scale ) ) );
	}
	
	/**
	 * Handles a change to the scaling vector by updating the state.
	 */
	public void handleListen()
	{
		trans.setTrans( AazonBufferedImmutableVect.construct( vect ) );
	}

	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		return( trans.getNode() );
	}
	
	/**
	 * Stores self-active objects.
	 * 
	 * @param in ArrayList in which to store self-active objects.
	 */
	public void genSelfActive( ArrayList in )
	{
		in.add( this );
	}

}

