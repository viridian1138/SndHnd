




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

import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;



/**
 * AazonEnt performing a mutable coordinate translation of another AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonSmartTranslation implements AazonImmutableEnt, AazonListener {
	
	/**
	 * The mutable translation.
	 */
	protected AazonMutableTranslation trans;
	
	/**
	 * Vector holding the coordinate translation.
	 */
	protected AazonMutableVect vect;
	
	/**
	 * Private constructor.
	 * @param _ent The AazonEnt being translated.
	 * @param _vect Vector holding the coordinate translation.
	 */
	private AazonSmartTranslation( AazonEnt _ent , AazonMutableVect _vect )
	{
			vect = _vect;
			trans = new AazonMutableTranslation( _ent , AazonBufferedImmutableVect.construct( _vect ) );
			vect.add( this );
	}
	
	/**
	 * Returns an AazonEnt performing a coordinate translation of another AazonEnt.
	 * @param _ent The AazonEnt being translated.
	 * @param _vect Vector holding the coordinate translation.
	 * @return AazonEnt performing a coordinate translation of another AazonEnt.
	 */
	public static AazonEnt construct( AazonEnt _ent , AazonVect _vect )
	{
		if( _vect instanceof AazonMutableVect )
		{
			return( new AazonSmartTranslation( _ent , (AazonMutableVect) _vect ) );
		}
		else
		{
			return( new AazonImmutableTranslation( _ent , (AazonImmutableVect) _vect ) );
		}
	}
	
	/**
	 * Handles a change to the translation vector by updating the state.
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

