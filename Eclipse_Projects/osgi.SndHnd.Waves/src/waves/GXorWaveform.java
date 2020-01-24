





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

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.WaveForm;

/**
 * Node representing waveform generating the fuzzy logic version of an exclusive or between two input waves.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Fuzzy_logic">https://en.wikipedia.org/wiki/Fuzzy_logic</A>
 * 
 * @author tgreen
 *
 */
public class GXorWaveform extends GWaveForm  implements Externalizable {
	
	/**
	 * The first input of the exclusive or.
	 */
	private GWaveForm chld;
	
	/**
	 * The second input of the exclusive or.
	 */
	private GWaveForm chld2;
	
	/**
	 * Constructs the node.
	 */
	public GXorWaveform()
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
		WaveForm w2 = chld2.genWave(s);
		
		XorWaveform wv = new XorWaveform( w , w2 );
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param in  The first input of the exclusive or.
	 * @param in2 The second input of the exclusive or.
	 */
	public void load( GWaveForm in ,  GWaveForm in2  )
	{
		chld = in;
		chld2 = in2;
	}

	public Object getChldNodes() {
		Object[] ob = { chld , chld2 };
		return( ob );
	}

	@Override
	public String getName() {
		return( "Xor" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		if( chld == null )
		{
			chld = (GWaveForm) in;
		}
		else
		{
			chld2 = (GWaveForm) in;
		}
		

	}

	@Override
	public void removeChld() {
		chld = null;
		chld2 = null;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		if( chld2 != null ) myv.setProperty("Chld2", chld2);

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
			chld2 = (GWaveForm)( myv.getProperty("Chld2") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

