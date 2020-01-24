





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
import cwaves.CosineWaveform;

/**
 * A waveform for a variant of the sinc function using the formula cos( x ) / ( 0.5 * ( abs( x ) - PI / 4 ) )
 * 
 * Formula created by the author from the idea that there must be something that is to cosine as sinc is to sine.
 * 
 * @author tgreen
 *
 */
public class SincCoWaveform extends WaveForm {
	
	/**
	 * Cosine wave form used to calculate the function.
	 */
	static final CosineWaveform sw = new CosineWaveform();
	
	/**
	 * Multiplier used to adjust the amplitude of the function.
	 */
	public static final double MULT = 4.0 * Math.PI;
	
	/**
	 * The height of the undefined region near x = +/- 0.25.
	 */
	double notchHeight;

	/**
	 * Constructs the waveform.
	 * @param _notchHeight The height of the undefined region near x = +/- 0.25.
	 */
	public SincCoWaveform( double _notchHeight ) {
		notchHeight = _notchHeight;
	}

	@Override
	public double eval(double p) {
		if( Math.abs( Math.abs( p ) - 0.25 ) < 0.001 )
		{
			return( - notchHeight );
		}
		
		return( sw.eval( p ) / ( 0.5 * MULT * ( Math.abs( p ) - 0.25 ) ) );
	}
	
	/**
	 * Test driver.
	 * @param in Input params.
	 */
	public static void main( String[] in )
	{
		SincCoWaveform s = new SincCoWaveform( 1.0 );
		System.out.println( s.eval( -0.24 ) );
		System.out.println( s.eval( -0.25 ) );
		System.out.println( s.eval( -0.26 ) );
		/* CosineWaveform s2 = new CosineWaveform();
		System.out.println( s2.eval( 0.01 ) );
		System.out.println( s2.eval( -0.01 ) ); */
		/* SineWaveform s3 = new SineWaveform();
		System.out.println( s3.eval( 0.01 ) );
		System.out.println( s3.eval( -0.01 ) ); */
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
		
		GSincCoWaveform wv = new GSincCoWaveform();
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

