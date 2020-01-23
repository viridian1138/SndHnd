





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
 * Waveform generating approximate white noise using a simplified hashing function.
 * @author tgreen
 *
 */
public class HashWhiteWaveForm extends WaveForm {

	/**
	 * Multiplier for the hashing function.
	 */
	protected static final int MULT = 1 << 31;
	
	/**
	 * Multiplier for the hashing function.
	 */
	protected double tmult;
	
	/**
	 * Multiplier for the hashing function.
	 */
	protected double tma;
	
	/**
	 * Addend for the hashing function.
	 */
	protected double tmb;
	
	/**
	 * Multiplier for the hashing function.
	 */
	protected int hashMult;
	
	/**
	 * Constructs the waveform.
	 * @param _tmult  Multiplier for the hashing function.
	 */
	public HashWhiteWaveForm( double _tmult ) {
		super();
		tmult = _tmult;
		tma = 0.53147638582379454239574 / 0.5;
		tmb = 0.637724401110901;
		hashMult = 65599;
	}

	/**
	 * Constructs the waveform.
	 * @param _tmult  Multiplier for the hashing function.
	 * @param _tma  Multiplier for the hashing function.
	 * @param _tmb  Addend for the hashing function.
	 * @param _hashMult  Multiplier for the hashing function.
	 */
	public HashWhiteWaveForm( double _tmult , double _tma , double _tmb , int _hashMult )
	{
		tmult = _tmult;
		tma = _tma;
		tmb = _tmb;
		hashMult = _hashMult;
	}

	@Override
	public double eval( double w ) {
		w = tma * w + tmb;
		final int x = (int)( MULT * ( ( tmult * w ) % 1.0 ) );
		final int y = x * hashMult;
		final double w1 = y / ( (double) MULT );
		return( w1 );
	}
	
	@Override
	public NonClampedCoefficient genClone()
	{
		return( this );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GHashWhiteWaveForm wv = new GHashWhiteWaveForm();
		s.put(this, wv);
		
		wv.load(tmult,tma,tmb,hashMult);
		
		return( wv );
	}

	/**
	* Reads the node from serial storage.
	*/
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	
}

