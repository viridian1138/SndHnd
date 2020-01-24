





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

import gredit.EditPackWave;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;

import meta.DataFormatException;
import meta.VersionBuffer;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;

/**
 * Meme for approximating a guitar timbre using a spread of sawtooth waves.
 * 
 * @author tgreen
 *
 */
public class GPrimitiveGuitarMeme extends GWaveMeme  implements Externalizable  {

	@Override
	public void initMeme(EditPackWave edit) {
		HashSet elem = edit.getElem();
		
		GCloselySpacedChord cls = new GCloselySpacedChord();
		edit.getWaveOut().performAssign( cls );
		
		GSpreadWaveform spread = new GSpreadWaveform();
		cls.performAssign( spread );
		
		GSawtoothWaveform saw = new GSawtoothWaveform();
		spread.performAssign( saw );
		
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, edit.getWaveOut());

	}
	
	/**
	 * Constructs the meme.
	 */
	public GPrimitiveGuitarMeme()
	{
		setName( "Meme -- Primitive Guitar" );
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

