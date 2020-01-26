




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







package gredit;

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
 * An abstract base class for a node representing a waveform, which is encouraged but not required to be clamped to [-1, 1].
 * 
 * Where possible, and particularly for the typical synthesizer waveforms such as sine, square, triangle, etc., it
 * is encouraged but not required for the waveform to have a period of unity.
 * 
 * Where the waveform is a lattice noise wave for which there is no obvious repeating period, it is encouraged but
 * not required for the waveform to have a lattice spacing of unity as this often produces a wave period that
 * is within an octave of unity.
 * 
 * Where the waveform is sampled from an audio file and hence there is no obvious period, there is
 * a useAutocorrelation() method to indicate that autocorrelation should be used to determine the wave 
 * period for a particular part of the sample.
 * 
 * There are some cases where the period of the wave is not on the same octave as the audibly
 * perceived period.  This is handled on a case-by-case basis by subclasses of IntelligentAgent.
 * 
 * Class GWaveForm interacts with class core.WaveForm.  GWaveForm is single-threaded, mutable, and
 * editable, whereas core.WaveForm is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public abstract class GWaveForm extends GNonClampedCoefficient implements Externalizable {
	
	/**
	 * Generates an instance of the waveform represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the waveform represented by this node.
	 */
	public abstract WaveForm genWave( HashMap s );
	
	@Override
	public final NonClampedCoefficient genCoeff( HashMap<GNode,Object> s )
	{
		return( genWave( s ) );
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}


