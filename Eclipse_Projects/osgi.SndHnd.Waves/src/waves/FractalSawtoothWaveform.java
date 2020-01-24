





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
 * 
 * A fractal (self-similar) version of a sawtooth waveform.
 * 
 * @author tgreen
 *
 */
public class FractalSawtoothWaveform extends WaveForm implements Externalizable {

	/**
	 * Constructs the waveform.
	 */
	public FractalSawtoothWaveform() {
		super();
	}
	
	@Override
	public double eval(double param) {
		return( param >= 0.0 ? evalPos( param ) : evalMinus( param ) );
	}

	/**
	 * Evaluates the waveform for positive values of the parameter.
	 * @param param The input parameter.
	 * @return The evaluated waveform.
	 */
	protected static double evalPos(double param) {
		int pval = (int) param;
		return( evalFract( 20 , param , pval , 1.0 , pval + 1.0 , -1.0 , 0.33  ) );
	}
	
	/**
	 * Evaluates the waveform for negative values of the parameter.
	 * @param param The input parameter.
	 * @return The evaluated waveform.
	 */
	protected static double evalMinus(double param) {
		int pval = (int) param;
		return( evalFract( 20 , param , pval - 1.0 , 1.0 , pval , -1.0 , 0.33  ) );
	}
	
	/**
	 * Evaluates the fractal.
	 * @param inum The iteration number.
	 * @param val The input parameter value.
	 * @param x0 The start X of the current segment of the fractal.
	 * @param y0 The start Y of the current segment of the fractal.
	 * @param x1 The end X of the current segment of the fractal.
	 * @param y1 The end Y of the current segment of the fractal.
	 * @param delt The amount to split the Y axis during the next iteration.
	 * @return The result of the evaluation.
	 */
	protected static double evalFract( int inum , double val , double x0 , double y0 , double x1 , double y1 , double delt )
	{
		if( inum == 0  )
		{
			double vmult = ( val - x0 ) / ( x1 - x0 );
			if( vmult < 0.0 ) vmult = 0.0;
			if( vmult > 1.0 ) vmult = 1.0;
			return( ( y1 - y0 ) * vmult + y0 );
		}
		else
		{
			double xsplit = ( x0 + x1 ) / 2.0;
			double ysplit = ( y0 + y1 ) / 2.0;
			if( val <= xsplit )
			{
				return( evalFract( inum- 1 , val , x0 , y0 , xsplit , ysplit - delt , delt / 2.0  ) );
			}
			else
			{
				return( evalFract( inum- 1 , val , xsplit , ysplit + delt , x1 , y1 , delt / 2.0  ) );
			}
			
		}
		
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
		
		GFractalSawtoothWaveform wv = new GFractalSawtoothWaveform();
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

