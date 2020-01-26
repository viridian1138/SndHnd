




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
 * A node representing a waveform that adds an offset to the amplitude of the input waveform.
 * 
 * @author tgreen
 *
 */
public class GOffsetWaveform extends GWaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	private GWaveForm chld;
	
	/**
	 * The coefficient to offset the amplitude.
	 */
	private GNonClampedCoefficient offset;
	
	/**
	 * Constructs the node.
	 */
	public GOffsetWaveform()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = chld.genWave(s);
		
		NonClampedCoefficient off = offset.genCoeff(s);
		
		OffsetWaveform wv = new OffsetWaveform( w , off );
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param in The input waveform.
	 * @param off The coefficient to offset the amplitude.
	 */
	public void load( GWaveForm in , GNonClampedCoefficient off )
	{
		chld = in;
		offset = off;
	}

	public Object getChldNodes() {
		Object[] ob = { chld , offset };
		return( ob );
	}

	@Override
	public String getName() {
		return( "Offset" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GNonClampedCoefficient ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			chld = (GWaveForm) in;
		}
		else
		{
			offset = (GNonClampedCoefficient) in;
		}

	}

	@Override
	public void removeChld() {
		chld = null;
		offset = null;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		if( offset != null ) myv.setProperty("Offset", offset);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			chld = (GWaveForm)( myv.getProperty("Chld") );
			offset = (GNonClampedCoefficient)( myv.getProperty("Offset") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
