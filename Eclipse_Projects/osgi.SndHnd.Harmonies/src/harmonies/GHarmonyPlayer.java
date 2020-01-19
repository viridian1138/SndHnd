





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
import greditinton.GIntonation;
import greditharmon.GHarmony;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;

/**
 * Node for playing an input harmony, i.e. audibly playing the pitches of the harmony.
 * The button to play the harmony is in the property editor for the node.
 * @author tgreen
 *
 */
public class GHarmonyPlayer extends GNode  implements Externalizable {
	
	/**
	 * The input harmony to be played.
	 */
	private GHarmony i1;
	
	/**
	 * The input intonation from which to generate the tonic of the harmony.
	 */
	private GIntonation i2;
	
	/**
	 * The index on the scale at which to play the tonic of the harmony.
	 */
	private int ind = 0;
	
	/**
	 * The number of the melodic interval in which to play the tonic of the harmony.
	 */
	private int melodicIntervalNumber = 3;

	
	@Override
	public Object genObj(HashMap s) {
		throw( new RuntimeException( "Not Suppported" ) );
	}

	@Override
	public String getName() {
		return( "HarmonyPlayer" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GIntonation ) || ( in instanceof GHarmony ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GHarmony )
		{
			i1 = (GHarmony) in;
		}
		else
		{
			i2 = (GIntonation)( in );
		}
		
	}

	@Override
	public void removeChld() {
		i1 = null;
		i2 = null;
	}

	@Override
	public Object getChldNodes() {
		Object[] ob = { i1 , i2 };
		return( ob );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		HarmonyPlayerEditor editor = new HarmonyPlayerEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Harmony Player Properties");
	}
	
	/**
	 * Gets the input harmony to be played.
	 * @return The input harmony to be played.
	 */
	public GHarmony getI1() {
		return i1;
	}

	/**
	 * Sets the input harmony to be played.
	 * @param i1 The input harmony to be played.
	 */
	public void setI1(GHarmony i1) {
		this.i1 = i1;
	}
	
	/**
	 * Gets the input intonation from which to generate the tonic of the harmony.
	 * @return The input intonation from which to generate the tonic of the harmony.
	 */
	public GIntonation getI2() {
		return i2;
	}

	/**
	 * Sets the input intonation from which to generate the tonic of the harmony.
	 * @param i2 The input intonation from which to generate the tonic of the harmony.
	 */
	public void setI2(GIntonation i2) {
		this.i2 = i2;
	}

	/**
	 * Gets the index on the scale at which to play the tonic of the harmony.
	 * @return The index on the scale at which to play the tonic of the harmony.
	 */
	public int getInd() {
		return ind;
	}

	/**
	 * Sets the index on the scale at which to play the tonic of the harmony.
	 * @param ind The index on the scale at which to play the tonic of the harmony.
	 */
	public void setInd(int ind) {
		this.ind = ind;
	}

	/**
	 * Gets the number of the melodic interval in which to play the tonic of the harmony.
	 * @return The number of the melodic interval in which to play the tonic of the harmony.
	 */
	public int getMelodicIntervalNumber() {
		return melodicIntervalNumber;
	}

	/**
	 * Sets the number of the melodic interval in which to play the tonic of the harmony.
	 * @param melodicIntervalNumber The number of the melodic interval in which to play the tonic of the harmony.
	 */
	public void setMelodicIntervalNumber(int melodicIntervalNumber) {
		this.melodicIntervalNumber = melodicIntervalNumber;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		if( i2 != null ) myv.setProperty("I2", i2);
		myv.setInt("Ind", ind);
		myv.setInt("MelodicIntervalNumber", melodicIntervalNumber);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			i1 = (GHarmony)( myv.getProperty("I1") );
			i2 = (GIntonation)( myv.getProperty("I2") );
			ind = myv.getInt( "Ind" );
			melodicIntervalNumber = myv.getInt( "MelodicIntervalNumber" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

