




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

/**
 * Waveform performing frequency modulation synthesis on an input waveform (i.e. it frequency-modulates an input waveform).
 * 
 * See:  https://en.wikipedia.org/wiki/Frequency_modulation_synthesis
 * 
 * @author tgreen
 *
 */
public class FrequencyModulationWaveForm extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	WaveForm w1;
	
	/**
	 * The frequency modulation coefficient, where a constant value of unity is equivalent to no applied modulation.
	 */
	NonClampedCoefficient w2;

	/**
	 * Constructs the waveform.
	 * @param _w1 The input waveform.
	 * @param _w2 The frequency modulation coefficient, where a constant value of unity is equivalent to no applied modulation.
	 */
	public FrequencyModulationWaveForm( WaveForm _w1 , NonClampedCoefficient _w2 ) {
		w1 = _w1;
		w2 = _w2;
	}

	@Override
	public double eval(double param) {
		return( w1.eval( param * w2.eval( param ) ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( w1.genClone() );
		final NonClampedCoefficient cl = w2.genClone();
		if( ( wv == w1 ) && ( cl == w2 ) )
		{
			return( this );
		}
		else
		{
			return( new FrequencyModulationWaveForm( wv , cl ) );
		}
	}

	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GFrequencyModulationWaveForm wv = new GFrequencyModulationWaveForm();
		s.put(this, wv);
		
		GWaveForm a1 = w1.genWave(s);
		GNonClampedCoefficient a2 = w2.genCoeff(s);
		
		wv.load(a1,a2);
		
		return( wv );
	}

	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

}

