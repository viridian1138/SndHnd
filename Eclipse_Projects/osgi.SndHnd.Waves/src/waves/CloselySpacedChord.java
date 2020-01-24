





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

import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;


/**
 * Waveform intended to "thicken" an input wave by producing a chord with multiple copies of the input wave at slightly different frequencies.
 * Each successive pitch shift exponentially decays in amplitude.
 * 
 * @author tgreen
 *
 */
public class CloselySpacedChord extends WaveForm {

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

	/**
	 * The input waveform to be thickened.
	 */
	private WaveForm evalWave;
	
	
	/**
	 * Constructs the waveform.
	 * @param _waveCount Number of pitch-shifted copies of the input wave to use in building the chord.
	 * @param _includeZeroWave Whether to include a copy of the original input waveform.
	 * @param _initialFreqMultiplier Frequency multiplier to be applied to produce each successive pitch shift.
	 * @param _amplitudeMultiplier Amplitude multiplier to be applied upon each successive pitch shift.
	 * @param _evalWave The input waveform to be thickened.
	 */
	public CloselySpacedChord( int _waveCount , boolean _includeZeroWave ,
			double _initialFreqMultiplier , double _amplitudeMultiplier ,
			WaveForm _evalWave ) {
		super();
		waveCount = _waveCount;
		includeZeroWave = _includeZeroWave;
		initialFreqMultiplier = _initialFreqMultiplier;
		amplitudeMultiplier = _amplitudeMultiplier;
		evalWave = _evalWave;
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( evalWave.genClone() );
		if( wv == evalWave )
		{
			return( this );
		}
		else
		{
			return( new CloselySpacedChord(waveCount , includeZeroWave , initialFreqMultiplier , amplitudeMultiplier , wv ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GCloselySpacedChord wv = new GCloselySpacedChord();
		s.put(this, wv);
		
		GWaveForm w = evalWave.genWave(s);
		
		wv.load(w, waveCount , includeZeroWave , initialFreqMultiplier , amplitudeMultiplier);
		
		return( wv );
	}

	@Override
	public double eval(double p) {
		double max = 0.0;
		double sum = 0.0;
		
		if( includeZeroWave )
		{
			sum += evalWave.eval( p );
			max += 1.0;
		}
		
		double fmult = initialFreqMultiplier;
		double freq = fmult;
		double mult = amplitudeMultiplier;
		
		if( waveCount > 0 )
		{
			sum += mult * evalWave.eval( freq * p );
			max += mult;
		}
		
		int count;
		for( count = 1 ; count < waveCount ; count++ )
		{
			fmult = Math.sqrt( fmult );
			freq = freq * fmult;
			mult = mult * amplitudeMultiplier;
			
			sum += mult * evalWave.eval( freq * p );
			max += mult;
		}
		
		return( sum / max );
	}

	/**
	* Reads the node from serial storage.
	*/
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	
}

