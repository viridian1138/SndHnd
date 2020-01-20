





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







package intonations;

import gredit.GNode;
import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * An abstract base class for a node with no children representing a Partch 43-tone intonation in the key of A.
 * See https://en.wikipedia.org/wiki/Harry_Partch's_43-tone_scale
 * @author tgreen
 *
 */
public abstract class GZPartchIntonation43BaseA extends GPartchIntonation43 implements Externalizable {

	/**
	 * Constructs the node.
	 */
	public GZPartchIntonation43BaseA()
	{
	}

	public Object getChldNodes() {
		return( null );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( false );
	}

	@Override
	public void performAssign(GNode in) {
		throw( new RuntimeException( "Not Supported" ) );

	}

	@Override
	public void removeChld() {
	}
	
	@Override
	public GIntonation getBaseIntonation()
	{
		return( this );
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
