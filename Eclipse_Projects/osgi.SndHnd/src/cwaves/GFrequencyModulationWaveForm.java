




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

import gredit.GNode;
import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Node representing a waveform performing frequency modulation synthesis on an input waveform (i.e. it frequency-modulates an input waveform).
 * 
 * See:  https://en.wikipedia.org/wiki/Frequency_modulation_synthesis
 * 
 * @author tgreen
 *
 */
public class GFrequencyModulationWaveForm extends GWaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	GWaveForm w1;
	
	/**
	 * The frequency modulation coefficient, where a constant value of unity is equivalent to no applied modulation.
	 */
	GNonClampedCoefficient w2;

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm a1 = w1.genWave(s);
		NonClampedCoefficient a2 = w2.genCoeff(s);
		
		FrequencyModulationWaveForm wv = new FrequencyModulationWaveForm( a1 , a2 );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Object[] ob  = { w1 , w2 };
		return( ob );
	}

	@Override
	public String getName() {
		return( "FrequencyModulation" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GNonClampedCoefficient ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			if( w1 == null )
			{
				w1 = (GWaveForm) in;
				return;
			}
			w2 = (GWaveForm) in;
			return;
		}
		if( in instanceof GNonClampedCoefficient )
		{
			w2 = (GNonClampedCoefficient) in;
		}

	}
	
	/**
	 * Loads new values into the node.
	 * @param _w1 The input waveform.
	 * @param _w2 The frequency modulation coefficient, where a constant value of unity is equivalent to no applied modulation.
	 */
	public void load( GWaveForm _w1 , GNonClampedCoefficient _w2 )
	{
		w1 = _w1;
		w2 = _w2;
	}

	@Override
	public void removeChld() {
		w1 = null;
		w2 = null;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( w1 != null ) myv.setProperty("W1", w1);
		if( w2 != null ) myv.setProperty("W2", w2);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			w1 = (GWaveForm)( myv.getProperty("W1") );
			w2 = (GNonClampedCoefficient)( myv.getProperty("W2") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

