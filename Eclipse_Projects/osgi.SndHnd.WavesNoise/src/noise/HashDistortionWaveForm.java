





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
 * Waveform that distorts an input waveform using a simplified hashing function.
 * @author tgreen
 *
 */
public class HashDistortionWaveForm extends WaveForm {

	/**
	 * Multiplier for the hashing function.
	 */
	protected static final int MULT = 1 << 31;
	
	/**
	 * The input waveform.
	 */
	protected WaveForm wave;
	
	/**
	 * Multiplier for the hashing function.
	 */
	protected int hashMult;
	
	/**
	 * Constructs the waveform.
	 * @param _wave  The input waveform.
	 */
	public HashDistortionWaveForm( WaveForm _wave ) {
		super();
		wave = _wave;
		hashMult = 65599;
	}

	@Override
	public double eval( double w ) {
		final double e = wave.eval( w );
		final int x = (int)( MULT * e );
		final int y = x * hashMult;
		final double w1 = y / ( (double) MULT );
		return( w1 );
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
			return( new HashDistortionWaveForm( wv ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GHashDistortionWaveForm wv = new GHashDistortionWaveForm();
		s.put(this, wv);
		
		GWaveForm ww = wave.genWave(s);
		
		wv.load(ww);
		
		return( wv );
	}


	/**
	* Reads the node from serial storage.
	*/
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	
	
}

