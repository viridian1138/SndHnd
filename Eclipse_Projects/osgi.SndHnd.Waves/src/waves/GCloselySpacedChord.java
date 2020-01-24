





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







package waves;

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.WaveForm;


/**
 * A node representing a waveform intended to "thicken" an input wave by producing a chord with multiple copies of the input wave at slightly different frequencies.
 * Each successive pitch shift exponentially decays in amplitude.
 * 
 * @author tgreen
 *
 */
public class GCloselySpacedChord extends GWaveForm  implements Externalizable {
	
	/**
	 * The input waveform to be thickened.
	 */
	private GWaveForm wave;
	
	/**
	 * Number of pitch-shifted copies of the input wave to use in building the chord.
	 */
	private int waveCount = 10;
	
	/**
	 * Whether to include a copy of the original input waveform.
	 */
	private boolean includeZeroWave = false;
	
	/**
	 * Frequency multiplier to be applied to produce each successive pitch shift.
	 */
	private double initialFreqMultiplier = 1.01;
	
	/**
	 * Amplitude multiplier to be applied upon each successive pitch shift.
	 */
	private double amplitudeMultiplier = 0.25;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		
		CloselySpacedChord wv = new CloselySpacedChord( waveCount , includeZeroWave ,
				initialFreqMultiplier , amplitudeMultiplier , w );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( wave );
	}

	@Override
	public String getName() {
		return( "Closely Spaced Chord" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		wave = (GWaveForm) in;
	}

	@Override
	public void removeChld() {
		wave = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param w The input waveform to be thickened.
	 * @param _waveCount  The number of pitch-shifted copies of the input wave to use in building the chord.
	 * @param _includeZeroWave  Whether to include a copy of the original input waveform.
	 * @param _initialFreqMultiplier The frequency multiplier to be applied to produce each successive pitch shift.
	 * @param _amplitudeMultiplier The amplitude multiplier to be applied upon each successive pitch shift.
	 */
	public void load( GWaveForm w , int _waveCount , boolean _includeZeroWave ,
			double _initialFreqMultiplier , double _amplitudeMultiplier )
	{
		wave = w;
		waveCount = _waveCount;
		includeZeroWave = _includeZeroWave;
		initialFreqMultiplier = _initialFreqMultiplier;
		amplitudeMultiplier = _amplitudeMultiplier;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		CloselySpacedChordEditor editor = new CloselySpacedChordEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Closely Spaced Chord Properties");
	}

	/**
	 * Gets the number of pitch-shifted copies of the input wave to use in building the chord.
	 * @return Number of pitch-shifted copies of the input wave to use in building the chord.
	 */
	public int getWaveCount() {
		return waveCount;
	}

	/**
	 * Sets the number of pitch-shifted copies of the input wave to use in building the chord.
	 * @param waveCount The number of pitch-shifted copies of the input wave to use in building the chord.
	 */
	public void setWaveCount(int waveCount) {
		this.waveCount = waveCount;
	}

	/**
	 * Gets whether to include a copy of the original input waveform.
	 * @return Whether to include a copy of the original input waveform.
	 */
	public boolean isIncludeZeroWave() {
		return includeZeroWave;
	}

	/**
	 * Sets whether to include a copy of the original input waveform.
	 * @param includeZeroWave Whether to include a copy of the original input waveform.
	 */
	public void setIncludeZeroWave(boolean includeZeroWave) {
		this.includeZeroWave = includeZeroWave;
	}

	/**
	 * Gets the frequency multiplier to be applied to produce each successive pitch shift.
	 * @return The frequency multiplier to be applied to produce each successive pitch shift.
	 */
	public double getInitialFreqMultiplier() {
		return initialFreqMultiplier;
	}

	/**
	 * Sets the frequency multiplier to be applied to produce each successive pitch shift.
	 * @param initialFreqMultiplier The frequency multiplier to be applied to produce each successive pitch shift.
	 */
	public void setInitialFreqMultiplier(double initialFreqMultiplier) {
		this.initialFreqMultiplier = initialFreqMultiplier;
	}

	/**
	 * Gets the amplitude multiplier to be applied upon each successive pitch shift.
	 * @return The amplitude multiplier to be applied upon each successive pitch shift.
	 */
	public double getAmplitudeMultiplier() {
		return amplitudeMultiplier;
	}

	/**
	 * Sets the amplitude multiplier to be applied upon each successive pitch shift.
	 * @param amplitudeMultiplier The amplitude multiplier to be applied upon each successive pitch shift.
	 */
	public void setAmplitudeMultiplier(double amplitudeMultiplier) {
		this.amplitudeMultiplier = amplitudeMultiplier;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
		myv.setInt("WaveCount", waveCount);
		myv.setBoolean("IncludeZeroWave",includeZeroWave);
		myv.setDouble("InitialFreqMultiplier", initialFreqMultiplier);
		myv.setDouble("AmplitudeMultiplier", amplitudeMultiplier);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (GWaveForm)( myv.getProperty("Wave") );
			waveCount = myv.getInt("WaveCount");
			includeZeroWave = myv.getBoolean("IncludeZeroWave");
			initialFreqMultiplier = myv.getDouble("InitialFreqMultiplier");
			amplitudeMultiplier = myv.getDouble("AmplitudeMultiplier");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

