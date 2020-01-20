





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







package intonations;

import gredit.GNode;
import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;

/**
 * A class for generating a typical mode (for instance a Lydian mode) from
 * a western 12-tone scale.
 * 
 * @author tgreen
 *
 */
public class GTypicalMode extends GAbstractMode implements Externalizable
{
	
	/**
	 * A major-key mode.
	 */
	public static final int MAJOR_MODE = 0;
	
	/**
	 * A natural minor mode.
	 */
	public static final int NATURAL_MINOR_MODE = 1;
	
	/**
	 * A harmonic minor mode.
	 */
	public static final int HARMONIC_MINOR_MODE = 2;
	
	/**
	 * An ascending melodic minor mode.
	 */
	public static final int MELODIC_MINOR_MODE_ASCENDING = 3;
	
	/**
	 * A descending melodic minor mode.
	 */
	public static final int MELODIC_MINOR_MODE_DESCENDING = 4;
	
	/**
	 * An ionian mode.
	 */
	public static final int IONIAN_MODE = 5;
	
	/**
	 * A dorian mode.
	 */
	public static final int DORIAN_MODE = 6;
	
	/**
	 * A Phrygian mode.
	 */
	public static final int PHRYGIAN_MODE = 7;
	
	/**
	 * A lydian mode.
	 */
	public static final int LYDIAN_MODE = 8;
	
	/**
	 * A Mixolydian mode.
	 */
	public static final int MIXOLYDIAN_MODE = 9;
	
	/**
	 * An aeolian mode.
	 */
	public static final int AEOLIAN_MODE = 10;
	
	/**
	 * A locrian mode.
	 */
	public static final int LOCRIAN_MODE = 11;
	
	/**
	 * A neapolitan minor mode.
	 */
	public static final int NEAPOLITAN_MINOR_MODE = 12;
	
	/**
	 * A neapolitan major mode.
	 */
	public static final int NEAPOLITAN_MAJOR_MODE = 13;
	
	/**
	 * A neapolitan dorian mode.
	 */
	public static final int NEAPOLITAN_DORIAN_MODE = 14;
	
	/**
	 * A neapolitan mixolidian mode.
	 */
	public static final int NEAPOLITAN_MIXOLIDIAN_MODE = 15;
	
	/**
	 * An enigmatic mode.
	 */
	public static final int ENIGMATIC_MODE = 16;
	
	/**
	 * A minor locrian mode.
	 */
	public static final int MINOR_LOCRIAN_MODE = 17;
	
	/**
	 * A major locrian mode.
	 */
	public static final int MAJOR_LOCRIAN_MODE = 18;
	
	/**
	 * A leading whole tone mode.
	 */
	public static final int LEADING_WHOLE_TONE_MODE = 19;
	
	/**
	 * A jazz minor mode.
	 */
	public static final int JAZZ_MINOR_MODE = 20;
	
	/**
	 * A blues seven-note mode.
	 */
	public static final int BLUES_SEVEN_NOTE_MODE = 21;
	
	
	
	/**
	 * The current typical mode.
	 */
	protected int mode = MAJOR_MODE;
	
	/**
	 * The key of the current typical mode.
	 */
	protected String key = "A";
	

	/**
	 * Constructs the mode.
	 */
	public GTypicalMode() {
	}

	@Override
	protected int getIndexOffset() {
		String[] names = i1.getScaleNames();
		int cnt = 0;
		for( cnt = 0 ; cnt < names.length ; cnt++ )
		{
			if( key.equals( names[ cnt ] ) )
			{
				return( cnt );
			}
		}
		throw( new RuntimeException( "Key Doesn't Match." ) );
	}

	@Override
	protected int getKeyStart() {
		String[] names = i1.getPriScaleNames();
		int cnt = 0;
		for( cnt = 0 ; cnt < names.length ; cnt++ )
		{
			if( key.equals( names[ cnt ] ) )
			{
				return( cnt );
			}
		}
		throw( new RuntimeException( "Key Doesn't Match." ) );
	}

	@Override
	protected String getKeyString() {
		switch( mode )
		{
			case MAJOR_MODE:
			case IONIAN_MODE:
			{
				return( "TTSTTTS" );
			}
			
			case NATURAL_MINOR_MODE:
			case MELODIC_MINOR_MODE_DESCENDING:
			{
				return( "TSTTSTT" );
			}
			
			case HARMONIC_MINOR_MODE:
			{
				return( "TSTTSFS" );
			}
			
			case MELODIC_MINOR_MODE_ASCENDING:
			{
				return( "TSTTTTS" );
			}
			
			case DORIAN_MODE:
			{
				return( "TSTTTST" );
			}
			
			case PHRYGIAN_MODE:
			{
				return( "STTTSTT" );
			}
			
			case LYDIAN_MODE:
			{
				return( "TTTSTTS" );
			}
			
			case MIXOLYDIAN_MODE:
			{
				return( "TTSTTST" );
			}
			
			case AEOLIAN_MODE:
			{
				return( "TSTTSTT" );
			}
			
			case LOCRIAN_MODE:
			{
				return( "STTSTTT" );
			}
			
			case NEAPOLITAN_MINOR_MODE:
			{
				return( "STTTSFS" );
			}
			
			case NEAPOLITAN_MAJOR_MODE:
			{
				return( "SFSTTTS" );
			}
			
			case NEAPOLITAN_DORIAN_MODE:
			{
				return( "STTTTST" );
			}
			
			case NEAPOLITAN_MIXOLIDIAN_MODE:
			{
				return( "SFSTTST" );
			}
			
			case ENIGMATIC_MODE:
			{
				return( "SFTTTSS" );
			}
			
			case MINOR_LOCRIAN_MODE:
			{
				return( "TSTSTTT" );
			}
			
			case MAJOR_LOCRIAN_MODE:
			{
				return( "TTSSTTT" );
			}
			
			case JAZZ_MINOR_MODE:
			{
				return( "TSTTTTS" );
			}
			
			case BLUES_SEVEN_NOTE_MODE:
			{
				return( "TSTSSFT" );
			}
			
			
		}
		throw( new RuntimeException( "NotSupported" ) );
	}

	@Override
	public String getName() {
		return( "TypicalMode" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		i1 = (GIntonation) in;
	}

	@Override
	public void removeChld() {
		i1 = null;
	}

	public Object getChldNodes() {
		return( i1 );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		TypicalModeEditor editor = new TypicalModeEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"TypicalMode Properties");
	}
	
	
	/**
	 * Gets the typical mode of the intonation.
	 * @return The typical mode of the intonation.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Sets the typical mode of the intonation.
	 * @param mode The typical mode of the intonation.
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	
	/**
	 * Returns the string describing the key
	 * (for instance key of C#) of the intonation.
	 * Note that they key is relative to the 
	 * intonation i1 member.
	 * @return The string describing the key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns the string describing the key
	 * (for instance key of C#) of the intonation.
	 * Note that they key is relative to the 
	 * intonation i1 member.
	 * @param key The string describing the key.
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the intonation from which to generate the mode.
	 * @return The intonation from which to generate the mode.
	 */
	public GIntonation getI1()
	{
		return( i1 );
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("Mode", mode);
		myv.setProperty("Key",key);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			mode = myv.getInt( "Mode" );
			key = (String)( myv.getProperty( "Key" ) );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

