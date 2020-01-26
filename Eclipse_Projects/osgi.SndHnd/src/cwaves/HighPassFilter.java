




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







package cwaves;

import gredit.GWaveForm;

import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform implementing a high-pass filter on an input waveform as the value of the input waveform minus the low-pass filter result.
 * 
 * Note: this is still a work in progress.
 * 
 * See:  https://beausievers.com/synth/synthbasics/#lowpasshighpass
 * 
 * @author tgreen
 *
 */
public class HighPassFilter extends LowPassFilter {

	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 * @param _intervalHalfLength The half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 * @param _sampleLen The number of samples used to perform the filtering.
	 */
	public HighPassFilter(WaveForm _wave, double _intervalHalfLength,
			int _sampleLen) {
		super(_wave, _intervalHalfLength, _sampleLen);
	}

	/**
	 * Constructor for persistence purposes only.
	 */
	public HighPassFilter() {
		super();
	}
	
	@Override
	public double eval(double param) {
		double lowPass = super.eval( param );
		double orig = wave.eval( param );
		return( orig - lowPass );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( wave.genClone() );
		if( wv == wave )
		{
			return( this );
		}
		else
		{
			return( new HighPassFilter( wv , intervalHalfLength , sampleLen ) );
		}
	}
	

	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GHighPassFilter wv = new GHighPassFilter();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w,intervalHalfLength,sampleLen);
		
		return( wv );
	}

}

