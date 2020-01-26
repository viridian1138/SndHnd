




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
import core.ClampedCoefficient;
import core.NonClampedCoefficient;


/**
 * An abstract base class for a node representing a clamped coefficient, which is encouraged but not required to be clamped to [0, 1].
 * 
 * Class GClampedCoefficient interacts with class core.ClampedCoefficient.  GClampedCoefficient is single-threaded, mutable, and
 * editable, whereas core.ClampedCoefficient is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public abstract class GClampedCoefficient extends GNonClampedCoefficient implements Externalizable {
	
	/**
	 * Generates an instance of the clamped coefficient represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the clamped coefficient represented by this node.
	 */
	public abstract ClampedCoefficient genClamped( HashMap<GNode,Object> s );
	
	@Override
	public final NonClampedCoefficient genCoeff( HashMap<GNode,Object> s )
	{
		return( genClamped( s ) );
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


