




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

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Link;
import javax.vecmath.Color3f;

import aazon.vect.AazonBufferedImmutableVect;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;


/**
 * An AazonEnt for a mutable block of text.
 * 
 * @author tgreen
 *
 */
public class AazonSmartBlockText implements AazonMutableEnt, AazonListener {
	
	/**
	 * The current state of the AazonMutableEnt.
	 */
	protected AazonMutableBlockText ent;
	
	/**
	 * The vector to the position where the text starts rendering.
	 */
	protected AazonVect a;
	
	/**
	 * The string to be displayed as block text.
	 */
	String str;
	
	/**
	 * The color of the text.
	 */
	Color3f txtCol;
	
	/**
	 * The name of the requested font for the block text.
	 */
	String fontName;
	
	/**
	 * The size of the requested font for the block text.
	 */
	int fontSize;
	
	/**
	 * The style of the requested font for the block text.  The possible styles can be obtained from the static final int members of class java.awt.Font.
	 */
	int fontStyle;
	
	
	/**
	 * Constructs the AazonSmartBlockText.
	 * @param _a The vector to the position where the text starts rendering.
	 * @param _str The string to be displayed as block text.
	 * @param _txtCol The color of the text.
	 * @param _fontName The name of the requested font for the block text.
	 * @param _fontSize The size of the requested font for the block text.
	 * @param _fontStyle The style of the requested font for the block text.  The possible styles can be obtained from the static final int members of class java.awt.Font.
	 */
	private AazonSmartBlockText( final AazonVect _a , final String _str , Color3f _txtCol , String _fontName , int _fontSize , int _fontStyle )
	{
		a = _a;
		str = _str;
		txtCol = _txtCol;
		fontName = _fontName;
		fontSize = _fontSize;
		fontStyle = _fontStyle;
		AazonImmutableBlockText rect = new AazonImmutableBlockText( AazonBufferedImmutableVect.construct( a ) , 
				str , txtCol , fontName , fontSize , fontStyle );
		ent = new AazonMutableBlockText( rect );
		
		if( a instanceof AazonMutableVect )
		{
			( (AazonMutableVect) a ).add( this );
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
	 * Handles a change to the vector (or the appearance) by updating the state.
	 */
	public void handleListen() {
		AazonImmutableBlockText rect = new AazonImmutableBlockText( AazonBufferedImmutableVect.construct( a ) , 
				 str , txtCol , fontName , fontSize , fontStyle );
		ent.setRect( rect );
	}

	/**
	 * Sets the state of the appearance of the AazonSmartBlockText.
	 * @param _str The string to be displayed as block text.
	 * @param _txtCol The color of the text.
	 * @param _fontName The name of the requested font for the block text.
	 * @param _fontSize The size of the requested font for the block text.
	 * @param _fontStyle The style of the requested font for the block text.  The possible styles can be obtained from the static final int members of class java.awt.Font.
	 */
	public void setAppearance( final String _str , Color3f _txtCol , String _fontName , int _fontSize , int _fontStyle )
	{
		str = _str;
		txtCol = _txtCol;
		fontName = _fontName;
		fontSize = _fontSize;
		fontStyle = _fontStyle;
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
	 * Returns an AazonEnt for a block of text.
	 * @param _a The vector to the position where the text starts rendering.
	 * @param _str The string to be displayed as block text.
	 * @param _txtCol The color of the text.
	 * @param _fontName The name of the requested font for the block text.
	 * @param _fontSize The size of the requested font for the block text.
	 * @param _fontStyle The style of the requested font for the block text.  The possible styles can be obtained from the static final int members of class java.awt.Font.
	 * @param forceMute Whether to force the production of the AazonEnt to be done as if the shape was mutable.
	 * @return AazonEnt for a block of text.
	 */
	public static AazonEnt construct( final AazonVect _a , final String _str , Color3f _txtCol , String _fontName , double _fontSize , int _fontStyle , boolean forceMute )
	{
		int fz = (int)( _fontSize );
		if( fz >= 3 )
		{
			return( construct( _a , _str , _txtCol , _fontName , fz , _fontStyle , forceMute ) );
		}
		
		return( new AazonImmutableGroup() );
	}
	
	/**
	 * Returns an AazonEnt for a block of text.
	 * @param _a The vector to the position where the text starts rendering.
	 * @param _str The string to be displayed as block text.
	 * @param _txtCol The color of the text.
	 * @param _fontName The name of the requested font for the block text.
	 * @param _fontSize The size of the requested font for the block text.
	 * @param _fontStyle The style of the requested font for the block text.  The possible styles can be obtained from the static final int members of class java.awt.Font.
	 * @param forceMute Whether to force the production of the AazonEnt to be done as if the shape was mutable.
	 * @return AazonEnt for a block of text.
	 */
	public static AazonEnt construct( final AazonVect _a , final String _str , Color3f _txtCol , String _fontName , int _fontSize , int _fontStyle , boolean forceMute )
	{
		if( ( _a instanceof AazonImmutableVect ) && ( !forceMute ) )
		{
			return( new AazonImmutableBlockText( (AazonImmutableVect) _a , _str , _txtCol , _fontName , _fontSize , _fontStyle ) );
		}
		
		return( new AazonSmartBlockText( _a , _str , _txtCol , _fontName , _fontSize , _fontStyle ) );
	}

	
}

