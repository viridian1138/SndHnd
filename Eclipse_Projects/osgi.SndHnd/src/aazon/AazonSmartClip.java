




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
import javax.media.j3d.Node;

import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;


/**
 * Mutable AazonEnt that performs clipping on another AazonEnt.
 * 
 * @author tgreen
 *
 */
public class AazonSmartClip implements AazonImmutableEnt, AazonListener {
	
	/**
	 * The current state of the clip.
	 */
	protected AazonMutableClip ent;
	
	/**
	 * The AazonEnt to be clipped.
	 */
	protected AazonEnt aent;
	
	/**
	 * Vector to the clipping position.
	 */
	protected AazonVect a;
	
	/**
	 * Vector representing the clipping width and height.
	 */
	protected AazonVect b;
	
	/**
	 * Private constructor.
	 * @param _aent The AazonEnt to be clipped.
	 * @param _a Vector to the clipping position.
	 * @param _b Vector representing the clipping width and height.
	 */
	private AazonSmartClip( AazonEnt _aent , AazonVect _a , AazonVect _b )
	{
		aent = _aent;
		a = _a;
		b = _b;
		AazonImmutableClip rect = new AazonImmutableClip( aent , AazonBufferedImmutableVect.construct( a ) , 
				AazonBufferedImmutableVect.construct( b ) );
		ent = new AazonMutableClip( rect );
		
		if( a instanceof AazonMutableVect )
		{
			( (AazonMutableVect) a ).add( this );
		}
		
		if( b instanceof AazonMutableVect )
		{
			( (AazonMutableVect) b ).add( this );
		}
	}

	/**
	 * Gets the node representing the AazonEnt.
	 * @return The node representing the AazonEnt.
	 */
	public Node getNode() {
		return( ent.getNode() );
	}

	/**
	 * Handles a change to one of the vectors (or the appearance) by updating the state.
	 */
	public void handleListen() {
		ent.setClip( AazonBufferedImmutableVect.construct( a ) , 
				AazonBufferedImmutableVect.construct( b ) );
	}

	/**
	 * Returns an AazonEnt that performs clipping on another AazonEnt.
	 * @param _aent The AazonEnt to be clipped.
	 * @param _a Vector to the clipping position.
	 * @param _b Vector representing the clipping width and height.
	 * @param forceMute Whether to force the production of the AazonEnt to be done as if the shape was mutable.
	 * @return AazonEnt that performs clipping on another AazonEnt.
	 */
	public static AazonEnt construct( AazonEnt _aent , AazonVect _a , AazonVect _b , boolean forceMute )
	{
		if( ( _a instanceof AazonImmutableVect ) && ( _b instanceof AazonImmutableVect ) && ( !forceMute ) )
		{
			return( new AazonImmutableClip( _aent , (AazonImmutableVect) _a ,  (AazonImmutableVect) _b ) );
		}
		
		return( new AazonSmartClip( _aent , _a , _b ) );
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

