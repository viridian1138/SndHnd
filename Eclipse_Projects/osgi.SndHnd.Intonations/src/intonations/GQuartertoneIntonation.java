





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

import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Abstract base class for a node representing a quartertone intonation and scale.
 * @author tgreen
 *
 */
public abstract class GQuartertoneIntonation extends GIntonation implements Externalizable {
	
	/**
	 * Returns the default set of names for notes on the quartertone scale.
	 * @return The default set of names for notes on the quartertone scale.
	 */
	private static String[] getScaleNamesStatic()
	{
		final String[] noteNames =
		{
			"A", "A!" , "A#" , "A##" , 
			"B", "B!" ,
			"C", "C!" , "C#" , "C##" , 
			"D", "D!" , "D#" , "D##" , 
			"E", "E!" ,
			"F", "F!" , "F#" , "F##" , 
			"G", "G!" , "G#" , "G##" 
		};
		return( noteNames );
	}
	
	@Override
	public String[] getScaleNames()
	{
		return( getScaleNamesStatic() );
	}
	
	@Override
	public String[] getPriScaleNames()
	{
		final String[] noteNames =
		{
			"A", "B" , "C", "D", "E", "F" , "G"
		};
		return( noteNames );
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

