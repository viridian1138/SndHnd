





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

import gredit.EditPackWave;
import gredit.GNode;
import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import verdantium.ProgramDirector;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.Intonation;
import core.WaveForm;
import cwaves.SquareWaveform;

/**
 * A node for an intonation that starts with a base pitch and puts the steps in the scale
 * at the pitches that have a minimum dissonance with the base pitch.  In
 * practice, the best harmonies are slightly separated from the point of
 * minimum dissonance, and hence there are tuning parameters to slide
 * slightly up the curve from a local minimum dissonance to its neighboring
 * local maximum dissonance.
 * 
 * To some extent this intonation is an outgrowth of the section of the 
 * Sethares book "Tuning Timbre Spectrum and Scale" where he
 * speculates that musical scales result from the pitches of minimum
 * dissonance for a particular instrument.  This class makes it possible
 * to automatically determine a scale for a non-traditional timbre.
 * 
 * @author tgreen
 *
 */
public class GDissonanceIntonation extends GIntonation implements Externalizable {
	
	/**
	 * Input intonation providing an initial starting point (e.g. the number of pitches in the desired scale).
	 */
	private GIntonation i1 = null;
	
	/**
	 * The waveform that stays at the base of the scale for the dissonance calculation.
	 */
	private EditPackWave wbase = new EditPackWave();
	
	/**
	 * The waveform that moves up the scale for the dissonance calculation.
	 */
	private EditPackWave wmoving = new EditPackWave();
	
	/**
	 * The number of the melodic interval in which to calculate the intonation.
	 */
	private int melodicIntervalNumber = 3;
	
	/**
	 * The name of the key in which to calculate the intonation.
	 */
	private String key = "A";

	
	@Override
	public Intonation genInton(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Intonation)( s.get(this) ) );
		}
		
		WaveForm wbas = wbase.processWave( new SquareWaveform( 0.25 ) );
		WaveForm wmov = wmoving.processWave( new SquareWaveform( 0.25 ) );
		Intonation i1a = i1.genInton( s );
		
		Intonation w = new DissonanceIntonation( i1a , this , wbas , wmov , key , melodicIntervalNumber );
		
		s.put(this, w);
		
		return( w );
	}
	
	
	@Override
	public GIntonation getBaseIntonation()
	{
		return( i1.getBaseIntonation() );
	}
	
	@Override
	public String[] getScaleNames()
	{
		return( i1.getScaleNames() );
	}
	
	@Override
	public String[] getPriScaleNames()
	{
		return( i1.getPriScaleNames() );
	}

	@Override
	public String getName() {
		return( "DissonanceIntonation" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		i1 = (GIntonation)( in );
	}

	@Override
	public void removeChld() {
		i1 = null;
	}

	public Object getChldNodes() {
		return( i1 );
	}

	/**
	 * Gets the input intonation.
	 * @return The input intonation.
	 */
	public GIntonation getI1() {
		return i1;
	}
	
	/**
	 * Gets the waveform that stays at the base of the scale for the dissonance calculation.
	 * @return The waveform that stays at the base of the scale for the dissonance calculation.
	 */
	public EditPackWave getWbase() {
		return wbase;
	}

	/**
	 * Gets the waveform that moves up the scale for the dissonance calculation.
	 * @return The waveform that moves up the scale for the dissonance calculation.
	 */
	public EditPackWave getWmoving() {
		return wmoving;
	}
	
	/**
	 * Gets the number of the melodic interval in which to calculate the intonation.
	 * @return The number of the melodic interval in which to calculate the intonation.
	 */
	public int getMelodicIntervalNumber() {
		return melodicIntervalNumber;
	}

	/**
	 * Sets the number of the melodic interval in which to calculate the intonation.
	 * @param melodicIntervalNumber The number of the melodic interval in which to calculate the intonation.
	 */
	public void setMelodicIntervalNumber(int melodicIntervalNumber) {
		this.melodicIntervalNumber = melodicIntervalNumber;
	}


	/**
	 * Gets the name of the key in which to calculate the intonation.
	 * @return The name of the key in which to calculate the intonation.
	 */
	public String getKey() {
		return key;
	}


	/**
	 * Sets the name of the key in which to calculate the intonation.
	 * @param key The name of the key in which to calculate the intonation.
	 */
	public void setKey(String key) {
		this.key = key;
	}


	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		DissonanceIntonationEditor editor = new DissonanceIntonationEditor( this , context );
		ProgramDirector.showPropertyEditor(editor, null,
			"DissonanceIntonation Properties");
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		myv.setProperty("Wbase", wbase);
		myv.setProperty("Wmoving", wmoving);
		myv.setInt("MelodicIntervalNumber", melodicIntervalNumber);
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

			i1 = (GIntonation)( myv.getProperty("I1") );
			wbase = (EditPackWave)( myv.getProperty( "Wbase" ) );
			wmoving = (EditPackWave)( myv.getProperty( "Wmoving" ) );
			melodicIntervalNumber = myv.getInt("MelodicIntervalNumber");
			key = (String)( myv.getProperty( "Key" ) );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

