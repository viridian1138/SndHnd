




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







package greditcoeffn;


import gredit.GNode;
import gredit.GNonClampedCoefficient;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;

/**
 * A node representing the output node of an EditPackCoeffn.
 * 
 * @author tgreen
 *
 */
public class GCoeffnOut extends GNonClampedCoefficient implements Externalizable {
	
	/**
	 * The non-clamped coefficient to which the output node connects.
	 */
	protected GNonClampedCoefficient coeff = null;

	@Override
	public NonClampedCoefficient genCoeff(HashMap s) {
		return( coeff.genCoeff(s) );
	}

	public Object getChldNodes() {
		return( coeff );
	}

	@Override
	public String getName() {
		return( "Coeff Out" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GNonClampedCoefficient );
	}

	@Override
	public void performAssign(GNode in) {
		coeff = (GNonClampedCoefficient) in;
	}

	@Override
	public void removeChld() {
		coeff = null;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( coeff != null ) myv.setProperty("Coeff", coeff);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coeff = (GNonClampedCoefficient)( myv.getProperty("Coeff") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}


