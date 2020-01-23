





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
 * Waveform approximating a heterogeneous procedural terrain function as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class HeteroTerrainWaveForm extends WaveForm {
	
	/**
	 * The noise to be applied in generating the function.  Typically this would be a lattice noise.
	 */
	WaveForm noise;
	
	/**
	 * The H parameter of the function.
	 */
	double H;
	
	/**
	 * The lacunarity of the function.
	 */
	double lacunarity;
	
	/**
	 * The number of octaves over which to evaluate the function.
	 */
	double octaves;
	
	/**
	 * The offset parameter for the function.
	 */
	double offset;
	
	/**
	 * Internal gain parameter used in calculating the wave.
	 */
	double q0;
	
	/**
	 * Constructs the waveform.
	 * @param _noise The noise to be applied in generating the function.  Typically this would be a lattice noise.
	 * @param _H The H parameter of the function.
	 * @param _lacunarity The lacunarity of the function.
	 * @param _octaves The number of octaves over which to evaluate the function.
	 * @param _offset The offset parameter for the function.
	 */
	public HeteroTerrainWaveForm( WaveForm _noise , double _H , 
			double _lacunarity , double _octaves , double _offset  )
	{
		noise = _noise;
		H = _H;
		lacunarity = _lacunarity;
		octaves = _octaves;
		offset = _offset;
		
		q0 = Math.pow(lacunarity,-H);
	}

	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GHeteroTerrainWaveForm wv = new GHeteroTerrainWaveForm();
		s.put(this, wv);
		
		GWaveForm w = noise.genWave(s);
		
		wv.load(w,H, lacunarity,octaves, offset);
		
		return( wv );
	}

	@Override
	public double eval(double p) {
		int i;
		double value = offset + noise.eval( p );
		double increment;
		p *= lacunarity;
		final double aval = q0;
		double bval = aval;
		
		for( i = 1 ; i < octaves ; i++ )
		{
			increment = noise.eval( p ) + offset;
			increment *= bval;
			increment *= value;
			value += increment;
			p *= lacunarity;
			bval *= aval;
		}
		
		double remainder = octaves - (int) octaves;
		if( Math.abs( remainder ) > 1E-9 )
		{
			increment = ( noise.eval( p ) + offset ) * bval;
			value += remainder * increment * value;
		}
		
		return( value );
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
			return( new HeteroTerrainWaveForm( wv , H , lacunarity , octaves , offset ) );
		}
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

}

