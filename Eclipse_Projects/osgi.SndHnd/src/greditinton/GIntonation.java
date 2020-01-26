




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
 * An abstract base class for a node representing an intonation and musical scale.
 * 
 * See https://en.wikipedia.org/wiki/intonation_(music)
 * 
 * Class GIntonation interacts with class core.Intonation.  GIntonation is single-threaded, mutable, and
 * editable, whereas core.Intonation is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public abstract class GIntonation extends GNode implements Externalizable {
	
	/**
	 * Generates an instance of the intonation represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the intonation represented by this node.
	 */
	public abstract Intonation genInton( HashMap s );
	
	/**
	 * Gets the name of each increment on the musical scale for the intonation.
	 * @return  The name of each increment on the musical scale for the intonation.
	 */
	public abstract String[] getScaleNames();
	
	/**
	 * Gets the names on the musical scale for the intonation which do not have an accidental.
	 * @return The names on the musical scale for the intonation which do not have an accidental.
	 */
	public abstract String[] getPriScaleNames();
	
	/**
	 * Gets the base intonation that is the generator for this intonation.  For instance, this allows the caller to determine whether the result of q GConflate is a western intonation.
	 * @return The base intonation that is the generator for the output intonation.
	 */
	public abstract GIntonation getBaseIntonation();
	
	/**
	 * Gets the indices into the musical scale for those scale names returned by getPriScaleNames().
	 * @return The indices into the musical scale for those scale names returned by getPriScaleNames().
	 */
	public int[] genPriScaleIndices()
	{
		int count;
		final String[] scaleNames = getScaleNames();
		final String[] priScaleNames = getPriScaleNames();
		final int[] ret = new int[ priScaleNames.length ];
		final HashMap<String,Integer> hm = new HashMap<String,Integer>();
		for( count = 0 ; count < scaleNames.length ; count++ )
		{
			hm.put( scaleNames[ count ] , new Integer( count ) );
		}
		for( count = 0 ; count < priScaleNames.length ; count++ )
		{
			Integer cnt = hm.get( priScaleNames[ count ] );
			ret[ count ] = cnt.intValue();
		}
		return( ret );
	}
	
	@Override
	public final Object genObj( HashMap<GNode,Object> s )
	{
		return( genInton( s ) );
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

