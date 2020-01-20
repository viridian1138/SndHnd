





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
 * Node for playing harmonies where an ascending and a descending scale is played versus the base of the scale.
 * The button to play the intonation is in the property editor for the node.
 * @author tgreen
 *
 */
public class GIntonationPlayerHarmony extends GNode  implements Externalizable {
	
	/**
	 * The input intonation to be played.
	 */
	private GIntonation i1;
	
	/**
	 * The index on the scale at which to start playing for the ascending scale.
	 */
	private int ind = 0;
	
	/**
	 * The number of the melodic interval in which to play the intonation for the ascending scale.
	 */
	private int melodicIntervalNumber = 3;
	
	/**
	 * The index on the scale at which to start playing for the descending scale.
	 */
	private int ind2 = 0;
	
	/**
	 * The number of the melodic interval in which to play the intonation for the descending scale.
	 */
	private int melodicIntervalNumber2 = 3;

	@Override
	public Object genObj(HashMap s) {
		throw( new RuntimeException( "Not Suppported" ) );
	}

	@Override
	public String getName() {
		return( "IntonationPlayerHarmony" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		i1 = (GIntonation)( in );
	}

	@Override
	public void removeChld() {
		i1 = null;
	}

	public Object getChldNodes() {
		return( i1 );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		IntonationPlayerHarmonyEditor editor = new IntonationPlayerHarmonyEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Intonation Player Properties");
	}
	
	/**
	 * Gets the input intonation to be played.
	 * @return The input intonation to be played.
	 */
	public GIntonation getI1() {
		return i1;
	}

	/**
	 * Sets the input intonation to be played.
	 * @param i1 The input intonation to be played.
	 */
	public void setI1(GIntonation i1) {
		this.i1 = i1;
	}

	/**
	 * Gets the index on the scale at which to start playing for the first part of the harmony.
	 * @return The index on the scale at which to start playing for the first part of the harmony.
	 */
	public int getInd() {
		return ind;
	}

	/**
	 * Sets the index on the scale at which to start playing for the first part of the harmony.
	 * @param ind The index on the scale at which to start playing for the first part of the harmony.
	 */
	public void setInd(int ind) {
		this.ind = ind;
	}

	/**
	 * Gets the number of the melodic interval in which to play the intonation for the first part of the harmony.
	 * @return The number of the melodic interval in which to play the intonation for the first part of the harmony.
	 */
	public int getMelodicIntervalNumber() {
		return melodicIntervalNumber;
	}

	/**
	 * Sets the number of the melodic interval in which to play the intonation for the first part of the harmony.
	 * @param melodicIntervalNumber The number of the melodic interval in which to play the intonation for the first part of the harmony.
	 */
	public void setMelodicIntervalNumber(int melodicIntervalNumber) {
		this.melodicIntervalNumber = melodicIntervalNumber;
	}
	
	
	/**
	 * Gets the index on the scale at which to start playing for the second part of the harmony.
	 * @return The index on the scale at which to start playing for the second part of the harmony.
	 */
	public int getInd2() {
		return ind2;
	}

	/**
	 * Sets the index on the scale at which to start playing for the second part of the harmony.
	 * @param ind The index on the scale at which to start playing for the second part of the harmony.
	 */
	public void setInd2(int ind2) {
		this.ind2 = ind2;
	}

	/**
	 * Gets the number of the melodic interval in which to play the intonation for the second part of the harmony.
	 * @return The number of the melodic interval in which to play the intonation for the second part of the harmony.
	 */
	public int getMelodicIntervalNumber2() {
		return melodicIntervalNumber2;
	}

	/**
	 * Sets the number of the melodic interval in which to play the intonation for the second part of the harmony.
	 * @param melodicIntervalNumber The number of the melodic interval in which to play the intonation for the second part of the harmony.
	 */
	public void setMelodicIntervalNumber2(int melodicIntervalNumber2) {
		this.melodicIntervalNumber2 = melodicIntervalNumber2;
	}
	

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		myv.setInt("Ind", ind);
		myv.setInt("MelodicIntervalNumber", melodicIntervalNumber);
		
		myv.setInt("Ind2", ind2);
		myv.setInt("MelodicIntervalNumber2", melodicIntervalNumber2);

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
			ind = myv.getInt( "Ind" );
			melodicIntervalNumber = myv.getInt( "MelodicIntervalNumber" );
			
			ind2 = myv.getInt( "Ind2" );
			melodicIntervalNumber2 = myv.getInt( "MelodicIntervalNumber2" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

