





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

/**
 * A waveform for Vector-Valued fBm (fractional Brownian motion) Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * Vector-Valued fBm is also known as vfBm.
 * @author thorngreen
 *
 */
public class VfbmWaveform extends WaveForm {
	
	/**
	 * The input noise function from which the vfBm is generated.  Typically this is a lattice noise.
	 */
	WaveForm noise;
	
	/**
	 * The maximum number of octaves over which to compute the vfBm.
	 */
	double maxoctaves;
	
	/**
	 * The lacunarity parameter of the vfBm.
	 */
	double lacunarity;
	
	/**
	 * The gain parameter of the vfBm.
	 */
	double gain;
	
	
	/**
	 * Constructs the waveform.
	 * @param _noise  The input noise function from which the vfBm is generated.  Typically this is a lattice noise.
	 * @param _maxoctaves  The maximum number of octaves over which to compute the vfBm.
	 * @param _lacunarity  The lacunarity parameter of the vfBm.
	 * @param _gain  The gain parameter of the vfBm.
	 */
	public VfbmWaveform( WaveForm _noise , double _maxoctaves , double _lacunarity , double _gain )
	{
		noise = _noise;
		maxoctaves = _maxoctaves;
		lacunarity = _lacunarity;
		gain = _gain;
	}

	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GVfbmWaveform wv = new GVfbmWaveform();
		s.put(this, wv);
		
		GWaveForm w = noise.genWave(s);
		
		wv.load(w,maxoctaves, lacunarity,gain);
		
		return( wv );
	}

	@Override
	public double eval(double p) {
		double i;
		double amp = 1;
		double pp = p;
		double sum = 0;
		for( i = 0 ; i < maxoctaves ; i++ )
		{
			sum += noise.eval(pp);
			amp *= gain;
			pp *= lacunarity;
		}
		
		return( sum );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable {
		final WaveForm wv = (WaveForm)( noise.genClone() );
		if( wv == noise )
		{
			return( this );
		}
		else
		{
			return( new VfbmWaveform( wv , maxoctaves , lacunarity , gain ) );
		}
	}

	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	
}

