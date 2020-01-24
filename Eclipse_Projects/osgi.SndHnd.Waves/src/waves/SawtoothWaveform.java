





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
 * Waveform returning a sawtooth function.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Sawtooth_wave">https://en.wikipedia.org/wiki/Sawtooth_wave</A>
 * 
 * @author tgreen
 *
 */
public class SawtoothWaveform extends WaveForm implements Externalizable {

	/**
	 * Constructs the WaveForm.
	 */
	public SawtoothWaveform() {
		super();
	}
	
	@Override
	public double eval(double param) {
		return( param >= 0.0 ? evalPos( param ) : evalMinus( param ) );
	}

	/**
	 * Evaluates the waveform for positive parameters.
	 * @param param The input parameter.
	 * @return The result of the evaluation.
	 */
	protected static double evalPos(double param) {
		int pval = (int) param;
		double pdelta = param - pval;
		double val = 2.0 * pdelta - 1.0;
		return( val );
	}
	
	/**
	 * Evaluates the waveform for negative parameters.
	 * @param param The input parameter.
	 * @return The result of the evaluation.
	 */
	protected static double evalMinus(double param) {
		int pval = (int) param;
		double pdelta = pval - param;
		double val = 2.0 * pdelta - 1.0;
		return( val );
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
		
		GSawtoothWaveform wv = new GSawtoothWaveform();
		s.put(this, wv);
		return( wv );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			
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
		

		out.writeObject(myv);
	}

}

