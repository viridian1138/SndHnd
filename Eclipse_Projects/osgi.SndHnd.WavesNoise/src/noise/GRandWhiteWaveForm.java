





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

import gredit.GZWaveBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.WaveForm;

/**
 * Node representing a waveform that produces white noise at lattice points.  The basic principle is to produce a unique
 * seed value at each lattice point, and then use that seed in a random number generator.
 * 
 * @author tgreen
 *
 */
public class GRandWhiteWaveForm extends GZWaveBase implements Externalizable {

	/**
	 * The offset for the seed values.
	 */
	protected int offset = 0;
	
	/**
	 * The maximum number of times to run the random number generator through the initial seed.
	 */
	protected int max = 5;
	
	/**
	 * Constructs the node.
	 */
	public GRandWhiteWaveForm()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm wv = new RandWhiteWaveForm( offset , max );
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads values into the node.
	 * @param _offset  The offset for the seed values.
	 * @param _max  The maximum number of times to run the random number generator through the initial seed.
	 */
	public void load( int _offset , int _max  )
	{
		offset = _offset;
		max = _max;
	}

	@Override
	public String getName() {
		return( "Noise -- Rand White" );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		RandWhiteWaveFormEditor editor = new RandWhiteWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Rand White Wave Form Properties");
	}

	
	
	/**
	 * Gets the offset for the seed values.
	 * @return The offset for the seed values.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the offset for the seed values.
	 * @param offset The offset for the seed values.
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Gets the maximum number of times to run the random number generator through the initial seed.
	 * @return The maximum number of times to run the random number generator through the initial seed.
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Sets the maximum number of times to run the random number generator through the initial seed.
	 * @param max The maximum number of times to run the random number generator through the initial seed.
	 */
	public void setMax(int max) {
		this.max = max;
	}

	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("Offset", offset);
		myv.setInt("Max", max);

		out.writeObject(myv);
	}

	
	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			offset = myv.getInt("Offset");
			max = myv.getInt("Max");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}


