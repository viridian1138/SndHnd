





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
import core.Intonation;

/**
 * A node representing an intonation that given two input intonations (Intonation #1 and Intonation #2 )
 * returns a version of Intonation #1  where the key has been transposed to a 
 * designated pitch on the scale of Intonation #2.  As a practical matter, this means
 * that an intonation doesn't need a separate class declaration for every possible
 * key.  Instead, an intonation in the key of A can be conflated to any other key
 * (regardless of whether that key comes from a just intonation, a diatonic intonation,
 * or something else).
 * @author tgreen
 *
 */
public class GConflate extends GIntonation implements Externalizable {
	
	/**
	 * Intonation #1
	 */
	private GIntonation i1;
	
	/**
	 * Intonation #2
	 */
	private GIntonation i2;
	
	/**
	 * The index into the scale of Intonation #2  that defines the key.
	 */
	private int ind = 3;

	/**
	 * Constructs the node.
	 */
	public GConflate() {
		super();
	}

	@Override
	public Intonation genInton(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Intonation)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		Intonation w = new Conflate( i1.genInton(s) , i2.genInton(s) , ind );
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "Conflate" );
	}
	
	@Override
	public String[] getScaleNames()
	{
		return( i1.getScaleNames() );
	}
	
	@Override
	public String[] getPriScaleNames()
	{
		return( i1.getPriScaleNames() );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		if( i1 == null )
		{
			i1 = (GIntonation) in;
		}
		else
		{
			i2 = (GIntonation) in;
		}

	}
	
	@Override
	public GIntonation getBaseIntonation()
	{
		return( i1.getBaseIntonation() );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		ConflateEditor editor = new ConflateEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Conflate Properties");
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
	
	/**
	 * Gets Intonation #1
	 * @return Intonation #1
	 */
	public GIntonation getI1() {
		return i1;
	}

	/**
	 * Sets Intonation #1
	 * @param i1 Intonation #1
	 */
	public void setI1(GIntonation i1) {
		this.i1 = i1;
	}

	/**
	 * Gets Intonation #2
	 * @return Intonation #2
	 */
	public GIntonation getI2() {
		return i2;
	}

	/**
	 * Sets Intonation #2
	 * @param i2 Intonation #2
	 */
	public void setI2(GIntonation i2) {
		this.i2 = i2;
	}

	/**
	 * Gets the index into the scale of Intonation #2  that defines the key.
	 * @return The index into the scale of Intonation #2  that defines the key.
	 */
	public int getInd() {
		return ind;
	}

	/**
	 * Sets the index into the scale of Intonation #2  that defines the key.
	 * @param ind The index into the scale of Intonation #2  that defines the key.
	 */
	public void setInd(int ind) {
		this.ind = ind;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		if( i2 != null ) myv.setProperty("I2", i2);
		myv.setInt("Ind", ind);

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

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

