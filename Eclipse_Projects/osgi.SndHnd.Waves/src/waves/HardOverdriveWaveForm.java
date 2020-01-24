





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
 * 
 * Waveform applying a hard clipping overdrive to an input waveform.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Distortion_(music)">https://en.wikipedia.org/wiki/Distortion_(music)</A>
 * 
 * @author tgreen
 *
 */
public class HardOverdriveWaveForm extends WaveForm {
	
	/**
	 * Input waveform.
	 */
	private WaveForm wave;
	
	/**
	 * Multiplier to use before applying the clipping.
	 */
	private double mult;

	/**
	 * Constructs the waveform.
	 * @param in Input waveform.
	 * @param imult Multiplier to use before applying the clipping.
	 */
	public HardOverdriveWaveForm( WaveForm in , double imult ) {
		wave = in;
		mult = imult;
	}

	@Override
	public double eval(double param) {
		double dbl = mult * ( wave.eval( param ) );
		dbl = Math.max( dbl , -1.0 );
		dbl = Math.min( dbl , 1.0 );
		return( dbl );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable {
		return( new HardOverdriveWaveForm( wave , mult ) );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GHardOverdriveWaveForm wv = new GHardOverdriveWaveForm();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w,mult);
		
		return( wv );
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub

	}

}

