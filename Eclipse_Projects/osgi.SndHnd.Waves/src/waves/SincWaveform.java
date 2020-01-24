





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
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;

import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.SineWaveform;

/**
 * Waveform returning the sinc function sin( x ) / x.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Sinc_function">https://en.wikipedia.org/wiki/Sinc_function</A>
 * 
 * @author tgreen
 *
 */
public class SincWaveform extends WaveForm {
	
	/**
	 * Sine wave form used to calculate the sinc function.
	 */
	static final SineWaveform sw = new SineWaveform();
	
	/**
	 * Multiplier used to adjust the amplitude of the function.
	 */
	public static final double MULT = 4.0 * Math.PI;
	
	/**
	 * The height of the undefined region near x = 0.
	 */
	double notchHeight;

	/**
	 * Constructs the WaveForm.  Default notchHeight is 1.0
	 * @param _notchHeight The height of the undefined region near x = 0.
	 */
	public SincWaveform( double _notchHeight ) {
		notchHeight = _notchHeight;
	}

	@Override
	public double eval(double p) {
		if( Math.abs( p ) < 0.001 )
		{
			return( notchHeight );
		}
		
		return( sw.eval( p ) / ( 0.5 * MULT * p ) );
	}
	
	/**
	 * Test driver.
	 */
	public static void main( String[] in )
	{
		SincWaveform s = new SincWaveform( 1.0 );
		System.out.println( s.eval( 0.01 ) );
		System.out.println( s.eval( -0.01 ) );
	}
	
    @Override
	public NonClampedCoefficient genClone() throws Throwable {
		return( this );
	}
	
    @Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GSincWaveform wv = new GSincWaveform();
		s.put(this, wv);
		wv.load(notchHeight);
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

