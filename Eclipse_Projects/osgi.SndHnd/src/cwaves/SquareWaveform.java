




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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Waveform for a square wave.
 * 
 * See:  https://en.wikipedia.org/wiki/Square_wave
 * 
 * @author tgreen
 *
 */
public class SquareWaveform extends WaveForm implements Externalizable {
	
	/**
	 * One-half the width of the negative side of the square wave.
	 */
	protected double atk;

	/**
	 * Constructs the waveform.
	 * @param _atk One-half the width of the negative side of the square wave.
	 */
	public SquareWaveform( double _atk ) {
		super();
		atk = _atk;
	}
	
	@Override
	public double eval(double param) {
		return( param >= 0.0 ? evalPos( param ) : evalMinus( param ) );
	}

	/**
	 * Evaluates the square wave for positive parameters.
	 * @param param The parameter value.
	 * @return The result of evaluating the square wave.
	 */
	protected final double evalPos(double param) {
		int pval = (int) param;
		double pdelta = param - pval;
		if( pdelta < atk )
			return( -1.0 );
		if( pdelta > ( 1.0 - atk ) )
			return( -1.0 );
		return( 1.0 );
	}
	
	/**
	 * Evaluates the square wave for negative parameters.
	 * @param param The parameter value.
	 * @return The result of evaluating the square wave.
	 */
	protected final double evalMinus(double param) {
		int pval = (int) param;
		double pdelta = pval - param;
		if( pdelta < atk )
			return( -1.0 );
		if( pdelta > ( 1.0 - atk ) )
			return( -1.0 );
		return( 1.0 );
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
		
		GSquareWaveform wv = new GSquareWaveform();
		s.put(this, wv);
		wv.load(atk);
		return( wv );
	}
	
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			atk = myv.getDouble( "Atk" );
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
		
		myv.setDouble( "Atk" , atk );

		out.writeObject(myv);
	}

}

