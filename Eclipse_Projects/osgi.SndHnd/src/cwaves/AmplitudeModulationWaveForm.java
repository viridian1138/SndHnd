




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


import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;


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
 * The primary difference between this and class AmplitudeDistortion is that the modulation is not clamped.
 * 
 * @author thorngreen
 */
public class AmplitudeModulationWaveForm extends WaveForm {

	/**
	 * The input waveform.
	 */
	WaveForm coeff1;
	
	/**
	 * The drive (amplification) function that modulates the input waveform.
	 */
	NonClampedCoefficient coeff2;
	
	/**
	 * Constructor for persistence purposes only.
	 */
	public AmplitudeModulationWaveForm()
	{
	}

	/**
	 * Constructs the waveform.
	 * @param _coeff1 The input waveform.
	 * @param _coeff2 The drive (amplification) function that modulates the input waveform.
	 */
	public AmplitudeModulationWaveForm( WaveForm _coeff1 , NonClampedCoefficient _coeff2 ) {
		super();
		coeff1 = _coeff1;
		coeff2 = _coeff2;
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		return( coeff1.eval( non_phase_distorted_param ) 
					* coeff2.eval( non_phase_distorted_param ) );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm c1 = (WaveForm)( coeff1.genClone() );
		final NonClampedCoefficient c2 = coeff2.genClone();
		if( ( c1 == coeff1 ) && ( c2 == coeff2 ) )
		{
			return( this );
		}
		else
		{
			return( new AmplitudeModulationWaveForm( c1 , c2 ) );
		}
	}

	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GAmplitudeModulationWaveForm wv = new GAmplitudeModulationWaveForm();
		s.put(this, wv);
		
		GWaveForm a1 = coeff1.genWave(s);
		GNonClampedCoefficient a2 = coeff2.genCoeff(s);
		
		wv.load(a1,a2);
		
		return( wv );
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("coeff1", coeff1);
		myv.setProperty("coeff2", coeff2);

		out.writeObject(myv);
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coeff1 = (WaveForm)( myv.getPropertyEx("coeff1") );
			coeff2 = (NonClampedCoefficient)( myv.getPropertyEx("coeff2") );
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

