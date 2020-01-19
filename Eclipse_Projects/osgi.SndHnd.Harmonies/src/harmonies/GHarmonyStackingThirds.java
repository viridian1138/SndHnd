





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







package harmonies;

import gredit.GNode;
import greditharmon.GHarmony;
import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import verdantium.ProgramDirector;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.Harmony;
import core.Intonation;


/**
 * A node for a harmony with a stack of thirds going upward in pitch from a tonic.
 * This is mainly intended for western intonations.  If the intonation is non-Western, then this may not sound particularly good.
 * 
 * @author tgreen
 *
 */
public class GHarmonyStackingThirds extends GHarmony implements Externalizable {
	
	/**
	 * The input intonation from which to determine what is a third.
	 */
	GIntonation i1 = null;
	
	/**
	 * The number of thirds to be stacked.
	 */
	int numThirds = 3;
	
	/**
	 * The name of the key for the tonic of the harmony.
	 */
	String firstNoteKey = "A";

	
	/**
	 * Constructs the node.
	 */
	public GHarmonyStackingThirds() {
		super();
	}

	@Override
	public Harmony genHarmony(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Harmony)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		Harmony w = new HarmonyStackingThirds( (Intonation)( i1.genInton(s) ) , numThirds , firstNoteKey , calcFirstNoteIndex() , calcOffset() );
		
		s.put(this, w);
		
		return( w );
	}

	
	@Override
	public String[] getHarmonyNames() {
		
		int[] offsetCalc = calcOffset();
		
		final String[] priScaleNames = i1.getPriScaleNames();
		
		final String[] ret = new String[ numThirds + 1 ];
		int index = calcFirstNoteIndex();
		int count;
		
		ret[ 0 ] = priScaleNames[ index ];
		
		for( count = 0 ; count < numThirds ; count++ )
		{
			index = ( index + 2 ) % ( offsetCalc.length );
			
			ret[ count + 1 ] = priScaleNames[ index ];
		}
		
		return( ret );
	}
	
	
	/**
	 * Calculates an array containing the number of intonation index offsets required to go up a third in whole steps.
	 * @return Array containing the number of intonation index offsets required to go up a third in whole steps.
	 */
	protected int[] calcOffset()
	{
		final String[] priScaleNames = i1.getPriScaleNames();
		final String[] scaleNames = i1.getScaleNames();
		
		int[] ret = new int[ priScaleNames.length ];
		
		int count;
		for( count = 0 ; count < ret.length ; count++ )
		{
			ret[ count ] = getNamedIndex( scaleNames , priScaleNames[ count ] );
		}
		
		return( ret );	
	}
	
	
	/**
	 * Gets the intonation index that matches a particular scale name.
	 * @param scaleNames  The names for the intonation indices.
	 * @param name The name to be matched.
	 * @return The intonation index that matches a particular scale name.
	 */
	protected int getNamedIndex( String[] scaleNames , String name )
	{
		int count;
		for( count = 0 ; count < scaleNames.length ; count++ )
		{
			if( scaleNames[ count ].equals( name ) )
			{
				return( count );
			}
		}
		
		throw( new RuntimeException( "Inconsistent" ) );
	}
	
	
	/**
	 * Calculates the intonation index of the tonic.
	 * @return The intonation index of the tonic.
	 */
	protected int calcFirstNoteIndex()
	{
		final String[] priScaleNames = i1.getPriScaleNames();
		int count;
		for( count = 0 ; count < priScaleNames.length ; count++ )
		{
			if( priScaleNames[ count ].equals( firstNoteKey ) )
			{
				return( count );
			}
		}
		
		throw( new RuntimeException( "Inconsistent" ) );
	}

	
	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		if( i1 == null )
		{
			i1 = (GIntonation) in;
		}

	}

	@Override
	public void removeChld() {
		i1 = null;
	}

	@Override
	public Object getChldNodes() {
		return( i1 );
	}
	
	/**
	 * Gets the input intonation from which to determine what is a third.
	 * @return The input intonation from which to determine what is a third.
	 */
	public GIntonation getI1() {
		return i1;
	}

	/**
	 * Sets the input intonation from which to determine what is a third.
	 * @param i1 The input intonation from which to determine what is a third.
	 */
	public void setI1(GIntonation i1) {
		this.i1 = i1;
	}
	
	/**
	 * Gets the number of thirds to be stacked.
	 * @return The number of thirds to be stacked.
	 */
	public int getNumThirds() {
		return numThirds;
	}

	/**
	 * Sets the number of thirds to be stacked.
	 * @param numThirds The number of thirds to be stacked.
	 */
	public void setNumThirds(int numThirds) {
		this.numThirds = numThirds;
	}
	
	/**
	 * Gets the name of the key for the tonic of the harmony.
	 * @return The name of the key for the tonic of the harmony.
	 */
	public String getFirstNoteKey() {
		return firstNoteKey;
	}

	/**
	 * Sets the name of the key for the tonic of the harmony.
	 * @param firstNoteKey The name of the key for the tonic of the harmony.
	 */
	public void setFirstNoteKey( String firstNoteKey ) {
		this.firstNoteKey = firstNoteKey;
	}

	@Override
	public String getName() {
		return( "Harmony Stacking Thirds" );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		StackingThirdsEditor editor = new StackingThirdsEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"HarmonyStackingThirds Properties");
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		myv.setProperty("FirstNoteKey", firstNoteKey);
		myv.setInt("NumThirds", numThirds);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			i1 = (GIntonation)( myv.getProperty("I1") );
			firstNoteKey = (String)( myv.getPropertyEx("FirstNoteKey") );
			numThirds = myv.getInt( "NumThirds" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	

}

