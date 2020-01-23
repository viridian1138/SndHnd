





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







package noise;

import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.SineWaveform;

/**
 * Waveform approximating smoke density as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class SmokeDensityWaveForm extends WaveForm {
	
	/**
	 * Sine wave used in generating the smoke density.
	 */
	private static SineWaveform sine = new SineWaveform();
	
	/**
	 * The noise to be applied in generating the smoke density.  Typically this would be a lattice noise.
	 */
	private WaveForm noise;
	
	/**
	 * Constructs the waveform.
	 * @param _noise  The noise to be applied in generating the smoke density.  Typically this would be a lattice noise.
	 */
	public SmokeDensityWaveForm( WaveForm _noise )
	{
		noise = _noise;
	}

	@Override
	public double eval(double p) {
		double d = noise.eval(p);
		double d2 = sine.eval( d / 2.0 + 0.5 );
		return( d2 );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( noise.genClone() );
		if( wv == noise )
		{
			return( this );
		}
		else
		{
			return( new SmokeDensityWaveForm( wv ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GSmokeDensity wv = new GSmokeDensity();
		s.put(this, wv);
		
		GWaveForm ww = noise.genWave(s);
		
		wv.load(ww);
		
		return( wv );
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	
}

