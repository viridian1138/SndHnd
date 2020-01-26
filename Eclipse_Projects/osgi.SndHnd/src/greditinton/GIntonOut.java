




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







package greditinton;


import gredit.GNode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.Intonation;

/**
 * A node representing the output node of an EditPackIntonation.
 * 
 * @author tgreen
 *
 */
public class GIntonOut extends GIntonation implements Externalizable {
	
	/**
	 * The intonation to which the output node connects.
	 */
	protected GIntonation inton = null;

	@Override
	public Intonation genInton(HashMap s) {
		return( inton.genInton(s) );
	}

	public Object getChldNodes() {
		return( inton );
	}

	@Override
	public String getName() {
		return( "Inton Out" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		inton = (GIntonation) in;
	}

	@Override
	public void removeChld() {
		inton = null;
	}
	
	@Override
	public String[] getScaleNames()
	{
		return( inton.getScaleNames() );
	}
	
	@Override
	public String[] getPriScaleNames()
	{
		return( inton.getPriScaleNames() );
	}
	
	@Override
	public GIntonation getBaseIntonation()
	{
		return( inton.getBaseIntonation() );
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( inton != null ) myv.setProperty("Inton", inton);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			inton = (GIntonation)( myv.getProperty("Inton") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}


