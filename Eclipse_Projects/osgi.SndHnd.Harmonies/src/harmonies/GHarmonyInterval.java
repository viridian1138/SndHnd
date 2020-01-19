





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
import java.util.HashSet;
import java.util.Vector;

import verdantium.ProgramDirector;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.Harmony;


/**
 * A node representing a two-part harmony with the pitch ratio between the tonic and the upper
 * pitch at a specified interval ratio.
 * 
 * @author tgreen
 *
 */
public class GHarmonyInterval extends GHarmony implements Externalizable {
	
	/**
	 * Input intonation.
	 */
	private GIntonation i1;
	
	/**
	 * The pitch ratio for the two-part harmony.
	 */
	private double val = 1.77;
	
	/**
	 * The name to be given to the harmony pitch.
	 */
	private String valName = "A0";

	/**
	 * Constructs the node.
	 */
	public GHarmonyInterval() {
		super();
	}

	@Override
	public Harmony genHarmony(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Harmony)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		Harmony w = new HarmonyInterval( i1.genInton(s) , val , valName );
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "HarmonyInterval" );
	}
	
	@Override
	public String[] getHarmonyNames()
	{
		String[] ret = { "A00" , valName };
		
		return( ret );
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
		HarmonyIntervalEditor editor = new HarmonyIntervalEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Harmony Interval Properties");
	}
	
	/**
	 * Gets the input intonation.
	 * @return The input intonation.
	 */
	public GIntonation getI1() {
		return i1;
	}

	/**
	 * Sets the input intonation.
	 * @param i1 The input intonation.
	 */
	public void setI1(GIntonation i1) {
		this.i1 = i1;
	}

	/**
	 * Gets the pitch ratio for the two-part harmony.
	 * @return The pitch ratio for the two-part harmony.
	 */
	public double getVal() {
		return val;
	}

	/**
	 * Sets the pitch ratio for the two-part harmony.
	 * @param _val The pitch ratio for the two-part harmony.
	 */
	public void setVal(double _val) {
		this.val = _val;
	}
	
	/**
	 * Gets the name to be given to the harmony pitch.
	 * @return The name to be given to the harmony pitch.
	 */
	public String getValName() {
		return valName;
	}

	/**
	 * Sets the name to be given to the harmony pitch.
	 * @param _valName The name to be given to the harmony pitch.
	 */
	public void setValName(String _valName) {
		this.valName = _valName;
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		if( valName != null ) myv.setProperty("ValName", valName);
		myv.setDouble("Val", val);

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
			val = myv.getDouble("Val");
			valName = (String)( myv.getPropertyEx("ValName") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

