





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
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;

/**
 * Node for playing an input intonation, i.e. audibly playing the pitches on the scale.
 * The player transitions one up the scale for one intonation, and then down the
 * scale on a different intonation.  Having two different intonations is useful for
 * e.g. ascending the scale in a major mode and then descending the scale
 * in a minor mode.
 * The button to play the intonation is in the property editor for the node.
 * @author tgreen
 *
 */
public class GIntonationPlayerTwoSide extends GNode  implements Externalizable {
	
	/**
	 * Intonation for playing the ascending pitches.
	 */
	private GIntonation i1;
	
	/**
	 * Intonation for playing the descending pitches.
	 */
	private GIntonation i2;
	
	/**
	 * The index on the scale at which to start playing.
	 */
	private int ind = 0;
	
	/**
	 * The number of the melodic interval in which to play the intonation.
	 */
	private int melodicIntervalNumber = 3;


	@Override
	public Object genObj(HashMap s) {
		throw( new RuntimeException( "Not Suppported" ) );
	}

	@Override
	public String getName() {
		return( "IntonationPlayerTwoSide" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		if( i1 == null )
		{
			i1 = (GIntonation)( in );
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

	public Object getChldNodes() {
		Object[] ob = { i1 , i2 };
		return( ob );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		IntonationPlayerTwoSideEditor editor = new IntonationPlayerTwoSideEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Intonation Player Properties");
	}
	
	/**
	 * Gets the intonation for playing the ascending pitches.
	 * @return Intonation for playing the ascending pitches.
	 */
	public GIntonation getI1() {
		return i1;
	}

	/**
	 * Sets the intonation for playing the ascending pitches.
	 * @param i1 Intonation for playing the ascending pitches.
	 */
	public void setI1(GIntonation i1) {
		this.i1 = i1;
	}
	
	/**
	 * Gets the intonation for playing the descending pitches.
	 * @return Intonation for playing the descending pitches.
	 */
	public GIntonation getI2() {
		return i2;
	}

	/**
	 * Sets the intonation for playing the descending pitches.
	 * @param i2 Intonation for playing the descending pitches.
	 */
	public void setI2(GIntonation i2) {
		this.i2 = i2;
	}

	/**
	 * Gets the index on the scale at which to start playing.
	 * @return The index on the scale at which to start playing.
	 */
	public int getInd() {
		return ind;
	}

	/**
	 * Sets the index on the scale at which to start playing.
	 * @param ind The index on the scale at which to start playing.
	 */
	public void setInd(int ind) {
		this.ind = ind;
	}

	/**
	 * Gets the number of the melodic interval in which to play the intonation.
	 * @return The number of the melodic interval in which to play the intonation.
	 */
	public int getMelodicIntervalNumber() {
		return melodicIntervalNumber;
	}

	/**
	 * Sets the number of the melodic interval in which to play the intonation.
	 * @param melodicIntervalNumber The number of the melodic interval in which to play the intonation.
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

			i1 = (GIntonation)( myv.getProperty("I1") );
			i2 = (GIntonation)( myv.getProperty("I2") );
			ind = myv.getInt( "Ind" );
			melodicIntervalNumber = myv.getInt( "MelodicIntervalNumber" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

