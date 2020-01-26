




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







package abzon;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Link;

import aazon.AazonEnt;
import aazon.AazonImmutableEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonListener;
import aazon.AazonMutableEnt;
import aazon.dbl.AazonBufferedImmutableDbl;
import aazon.dbl.AazonDbl;
import aazon.dbl.AazonImmutableDbl;
import aazon.dbl.AazonMutableDbl;

/**
 * Produces an AazonEnt for a shape given the path iterator factories of the shape.
 * 
 * @author tgreen
 *
 */
public class AbzonSmartShape implements AazonMutableEnt, AazonListener {
	
	/**
	 * Mutable version of the shape that encapsulates the current state of the shape.
	 */
	AbzonMutableShape ent;
	
	/**
	 * The input path iterator factory.
	 */
	AbzonPathIteratorFactory fac;
	
	/**
	 * The desired flatness for the iterator.
	 */
	AazonDbl flatness;
	
	/**
	 * The input affine transform of the shape.  NOTE: it is assumed that the affine transform will remain unchanged throughout the lifetime of the shape.
	 */
	AffineTransform at;
	
	/**
	 * The input Appearance of the shape.  NOTE: it is assumed that the Appearance will remain unchanged throughout the lifetime of the shape.
	 */
	Appearance app;
	
	/**
	 * Private constructor.
	 * @param _fac The path iterator factory.
	 * @param _flatness The desired flatness for the iterator.
	 * @param _at The input affine transform of the shape.  NOTE: it is assumed that the affine transform will remain unchanged throughout the lifetime of the shape.
	 * @param _app The input Appearance of the shape.  NOTE: it is assumed that the Appearance will remain unchanged throughout the lifetime of the shape.
	 */
	private AbzonSmartShape( AbzonPathIteratorFactory _fac , AazonDbl _flatness , AffineTransform _at , Appearance _app )
	{
		fac = _fac;
		flatness = _flatness;
		at = _at;
		app = _app;
		
		if( fac instanceof AbzonMutablePathIteratorFactory )
		{
			AbzonMutablePathIteratorFactory fn = (AbzonMutablePathIteratorFactory) fac;
			AbzonImmutableShape shape = new AbzonImmutableShape( fn.getImmutableFactory() , AazonBufferedImmutableDbl.construct( flatness ) , at , app );
			ent = new AbzonMutableShape( shape );
		}
		else
		{
			AbzonImmutableShape shape = new AbzonImmutableShape( (AbzonImmutablePathIteratorFactory) fac , AazonBufferedImmutableDbl.construct( flatness ) , at , app );
			ent = new AbzonMutableShape( shape );
		}
		
		if( fac instanceof AbzonMutablePathIteratorFactory )
		{
			( (AbzonMutablePathIteratorFactory) fac ).add( this );
		}
		
		if( flatness instanceof AazonMutableDbl )
		{
			( (AazonMutableDbl) flatness ).add( this );
		}
	}

	/**
	 * Gets the branch group used by the shape.
	 * @return The branch group used by the shape.
	 */
	public BranchGroup getBranch() {
		return( ent.getBranch() );
	}

	/**
	 * Gets the link used by the shape.
	 * @return The link used by the shape.
	 */
	public Link getLink() {
		return( ent.getLink() );
	}
	
	/**
	 * Gets the current state of the mutable shape.
	 * @return The current state of the mutable shape.
	 */
	public AazonImmutableEnt genImmutable()
	{
		return( ent.genImmutable() );
	}

	/**
	 * Handles a change to one of the constituents of the shape by regenerating the shape.
	 */
	public void handleListen() {
		if( fac instanceof AbzonMutablePathIteratorFactory )
		{
			AbzonMutablePathIteratorFactory fn = (AbzonMutablePathIteratorFactory) fac;
			AbzonImmutableShape line = new AbzonImmutableShape( fn.getImmutableFactory() , AazonBufferedImmutableDbl.construct( flatness ) , at  , app );
			ent.setShape( line );
		}
		else
		{
			AbzonImmutableShape line = new AbzonImmutableShape( (AbzonImmutablePathIteratorFactory) fac , AazonBufferedImmutableDbl.construct( flatness ) , at  , app );
			ent.setShape( line );
		}
	}
	
	/**
	 * Sets the appearance of the shape.
	 * @param _app The input Appearance of the shape.  NOTE: it is assumed that the Appearance will remain unchanged throughout the lifetime of the shape.
	 */
	public void setAppearance( Appearance _app )
	{
		app = _app;
		handleListen();
	}
	
	/**
	 * Produces an AazonEnt for a shape given the path iterator factory of the shape.
	 * @param _fac The path iterator factory.
	 * @param _flatness The desired flatness for the iterator.
	 * @param _at The input affine transform of the shape.  NOTE: it is assumed that the affine transform will remain unchanged throughout the lifetime of the shape.
	 * @param _app The input Appearance of the shape.  NOTE: it is assumed that the Appearance will remain unchanged throughout the lifetime of the shape.
	 * @param forceMute Whether to force the production of the AazonEnt to be done as if the shape was mutable.
	 * @return The AazonEnt
	 */
	public static AazonEnt construct( AbzonPathIteratorFactory _fac , AazonDbl _flatness , AffineTransform _at , Appearance _app , boolean forceMute )
	{
		if( ( _fac instanceof AbzonImmutablePathIteratorFactory ) && ( _flatness instanceof AazonImmutableDbl ) && ( !forceMute ) )
		{
			return( new AbzonImmutableShape( (AbzonImmutablePathIteratorFactory) _fac , (AazonImmutableDbl) _flatness , _at , _app ) );
		}
		
		return( new AbzonSmartShape( _fac , _flatness, _at , _app ) );
	}
	
	/**
	 * Adds the self-active parts of this shape to an input ArrayList.
	 * @param in The input ArrayList.
	 */
	public void genSelfActive( ArrayList in )
	{
		in.add( this );
	}
	
	/**
	 * Produces an AazonEnt for a shape given the path iterator factories of the shape.
	 * @param _fac The path iterator factories.
	 * @param _flatness The desired flatness for the iterator.
	 * @param _at The input affine transform of the shape.  NOTE: it is assumed that the affine transform will remain unchanged throughout the lifetime of the shape.
	 * @param _app The input Appearance of the shape.  NOTE: it is assumed that the Appearance will remain unchanged throughout the lifetime of the shape.
	 * @param forceMute Whether to force the production of the AazonEnt to be done as if the shape was mutable.
	 * @return The AazonEnt
	 */
	public static AazonEnt construct( AbzonPathIteratorFactory[] _fac , AazonDbl _flatness , AffineTransform _at , Appearance _app , boolean forceMute )
	{
		final int len = _fac.length;
		final AazonEnt[] ents = new AazonEnt[ len ];
		int count;
		for( count = 0 ; count < len ; count++ )
		{
			ents[ count ] = AbzonSmartShape.construct( _fac[ count ] , _flatness, _at, _app, forceMute);
		}
		if( len == 1 )
		{
			return( ents[ 0 ] );
		}
		return( new AazonImmutableGroup( ents ) );
	}

	
}

