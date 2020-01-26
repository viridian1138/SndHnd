




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
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Link;

import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;


/**
 * An AazonEnt for a mutable line between two vector endpoints.
 * 
 * @author tgreen
 *
 */
public class AazonSmartLine implements AazonMutableEnt, AazonListener {
	
	/**
	 * The current state of the mutable line.
	 */
	protected AazonMutableLine ent;
	
	/**
	 * Vector to the first endpoint of the line.
	 */
	protected AazonVect a;
	
	/**
	 * Vector to the second endpoint of the line.
	 */
	protected AazonVect b;
	
	/**
	 * The appearance of the line.
	 */
	protected Appearance app;
	
	/**
	 * Private constructor.
	 * @param _a Vector to the first endpoint of the line.
	 * @param _b Vector to the second endpoint of the line.
	 * @param _app The appearance of the line.
	 */
	private AazonSmartLine( AazonVect _a , AazonVect _b , Appearance _app )
	{
		a = _a;
		b = _b;
		app = _app;
		AazonImmutableLine line = new AazonImmutableLine( AazonBufferedImmutableVect.construct( a ) , 
				AazonBufferedImmutableVect.construct( b ) , app );
		ent = new AazonMutableLine( line );
		
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
	 * Gets the branch group for the AazonEnt.
	 * @return The branch group for the AazonEnt.
	 */
	public BranchGroup getBranch() {
		return( ent.getBranch() );
	}

	/**
	 * Gets the link for the AazonEnt.
	 * @return The link for the AazonEnt.
	 */
	public Link getLink() {
		return( ent.getLink() );
	}
	
	/**
	 * Returns the current state of the mutable AazonEnt.
	 * @return The current state of the mutable AazonEnt.
	 */
	public AazonImmutableEnt genImmutable()
	{
		return( ent.genImmutable() );
	}

	/**
	 * Handles a change to one of the vectors (or the appearance) by updating the state.
	 */
	public void handleListen() {
		AazonImmutableLine line = new AazonImmutableLine( AazonBufferedImmutableVect.construct( a ) , 
				AazonBufferedImmutableVect.construct( b ) , app );
		ent.setLine( line );
	}
	
	/**
	 * Sets the appearance of the line.
	 * @param _app The appearance of the line.
	 */
	public void setAppearance( Appearance _app )
	{
		app = _app;
		handleListen();
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
	
	/**
	 * Returns an AazonEnt for a line between two vector endpoints.
	 * @param _a Vector to the first endpoint of the line.
	 * @param _b Vector to the second endpoint of the line.
	 * @param _app The appearance of the line.
	 * @param forceMute Whether to force the production of the AazonEnt to be done as if the shape was mutable.
	 * @return AazonEnt for a line between two vector endpoints.
	 */
	public static AazonEnt construct( AazonVect _a , AazonVect _b , Appearance _app , boolean forceMute )
	{
		if( ( _a instanceof AazonImmutableVect ) && ( _b instanceof AazonImmutableVect ) && ( !forceMute ) )
		{
			return( new AazonImmutableLine( (AazonImmutableVect) _a ,  (AazonImmutableVect) _b , _app ) );
		}
		
		return( new AazonSmartLine( _a , _b , _app ) );
	}

	
}

