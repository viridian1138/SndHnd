




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


import gredit.GClampedCoefficient;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.ClampedCoefficient;
import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * 
 * Waveform performing amplitude modulation synthesis upon an input wave.
 * 
 * See: https://www.soundonsound.com/techniques/amplitude-modulation
 * 
 * The modulating input can also amplify the level of the input wave for implementations of distortion and/or overdriving.
 * 
 * See: https://en.wikipedia.org/wiki/Distortion_(music)
 * 
 * The primary difference between this and class AmplitudeModulationWaveform is that the modulation is clamped.  Distortion systems tend to have some clamp (often a clamp that produces some form of clipping) on the end of the normal amplification range.
 * 
 * @author thorngreen
 */
public class AmplitudeDistortion extends WaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	WaveForm wave;
	
	/**
	 * The drive (amplification) function that modulates the input waveform.
	 */
	ClampedCoefficient drive;

	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 * @param _drive The drive (amplification) function that modulates the input waveform.
	 */
	public AmplitudeDistortion(WaveForm _wave , ClampedCoefficient _drive ) {
		super();
		wave = _wave;
		drive = _drive;
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		return( drive.eval( wave.eval( non_phase_distorted_param ) ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( wave.genClone() );
		final ClampedCoefficient dr = (ClampedCoefficient)( drive.genClone() );
		if( ( wv == wave ) && ( dr == drive ) )
		{
			return( this );
		}
		else
		{
			return( new AmplitudeDistortion( wv , dr ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GAmplitudeDistortion wv = new GAmplitudeDistortion();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		GClampedCoefficient d = drive.genClamped(s);
		
		wv.load(w,d);
		
		return( wv );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (WaveForm)( myv.getPropertyEx("Wave") );
			drive = (ClampedCoefficient)( myv.getPropertyEx("Drive") );
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("Wave", wave);
		myv.setProperty("Drive", drive);

		out.writeObject(myv);
	}

}

