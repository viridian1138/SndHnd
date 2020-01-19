





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







package harmonies;

import gredit.GNode;
import greditharmon.GHarmony;
import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.Harmony;

/**
 * Node representing a harmony for a power chord with one harmony pitch at the fifth,
 * and a second harmony pitch at the melodic interval (usually the octave).
 * 
 * @author tgreen
 *
 */
public class GHarmonyPowerChord extends GHarmony implements Externalizable {
	
	/**
	 * Input intonation from which to determine the harmony.
	 */
	private GIntonation i1;

	/**
	 * Constructs the node.
	 */
	public GHarmonyPowerChord() {
		super();
	}

	@Override
	public Harmony genHarmony(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Harmony)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		Harmony w = new HarmonyPowerChord( i1.genInton(s) , i1.genPriScaleIndices()[ 4 ] );
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "Harmony -- Power Chord" );
	}
	
	@Override
	public String[] getHarmonyNames()
	{
		final String[] str = { "P0" , "P1" , "P2" };
		return( str );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		i1 = (GIntonation) in;
	}

	@Override
	public void removeChld() {
		i1 = null;
	}

	public Object getChldNodes() {
		Object[] ob = { i1 };
		return( ob );
	}
	
	/**
	 * Gets the input intonation from which to determine the harmony.
	 * @return Input intonation from which to determine the harmony.
	 */
	public GIntonation getI1() {
		return i1;
	}

	/**
	 * Sets the input intonation from which to determine the harmony.
	 * @param i1 Input intonation from which to determine the harmony.
	 */
	public void setI1(GIntonation i1) {
		this.i1 = i1;
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			i1 = (GIntonation)( myv.getProperty("I1") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

