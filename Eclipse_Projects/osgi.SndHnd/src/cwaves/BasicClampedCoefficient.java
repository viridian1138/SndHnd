




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


import gredit.GClampedCoefficient;
import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.ClampedCoefficient;
import core.NonClampedCoefficient;
import core.WaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;



/**
 * A clamped coefficient, which is encouraged but not required to be clamped to [0, 1], which takes in a waveform, which is encouraged but not required to be clamped to [-1, 1], and affine-maps its values from [-1, 1] to [0,1].
 * 
 * @author thorngreen
 *
 */
public class BasicClampedCoefficient extends ClampedCoefficient  implements Externalizable {

	/**
	 * The input waveform.
	 */
	WaveForm wave;
	
	/**
	 * Constructs the node.
	 * @param _wave The input waveform.
	 */
	public BasicClampedCoefficient(WaveForm _wave) {
		super();
		wave = _wave;
	}
	
	/**
	 *Constructor for persistence purposes only.
	 *
	 */
	public BasicClampedCoefficient()
	{
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		return( ( wave.eval( non_phase_distorted_param ) + 1.0 ) / 2.0 );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		return( this );
	}
	
	@Override
	public GClampedCoefficient genClamped( HashMap<Object,GNode> s )
	{
		if( s.get( this ) != null )
		{
			return( (GClampedCoefficient)( s.get( this ) ) );
		}
		
		GBasicClampedCoefficient wv = new GBasicClampedCoefficient();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w);
		
		return( wv );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (WaveForm)( myv.getPropertyEx("Wave") );
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

		myv.setProperty("Wave", wave);

		out.writeObject(myv);
	}

}

