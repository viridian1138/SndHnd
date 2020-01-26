




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







package greditharmon;



import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import meta.DataFormatException;
import meta.VersionBuffer;
import aazon.builderNode.AazonTransChld;
import aazon.builderNode.BuilderNode;
import aczon.AczonUnivAllocator;
import core.Harmony;


/**
 * An editing pack for a group of nodes representing a musical harmony with a certain number of parts (two-part harmony, three-part harmony, etc.).
 * 
 * See https://en.wikipedia.org/wiki/Harmony
 * 
 * @author tgreen
 *
 */
public class EditPackHarmony implements Externalizable {
	
	/**
	 * The set of BuilderNode instances edited using the EditPack.
	 */
	private final HashSet<BuilderNode> elem = new HashSet<BuilderNode>();
	
	/**
	 * The input harmony of the EditPack.
	 */
	private GHarmonyIn harmonyIn = new GHarmonyIn();
	
	/**
	 * The output harmony of the EditPack.
	 */
	private GHarmonyOut harmonyOut = new GHarmonyOut();
	
	/**
	 * Constructs the EditPack.
	 */
	public EditPackHarmony()
	{
		harmonyOut.performAssign( harmonyIn );
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, harmonyOut);
	}
	
	/**
	 * Generates an instance of the harmony represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the harmony represented by this node.
	 */
	public Harmony processHarmony( HashMap s )
	{
		Harmony out = harmonyOut.genHarmony(s);
		return( out );
	}
	
	/**
	 * Gets human-readable names for the different parts of the harmony.
	 * @return Human-readable names for the different parts of the harmony.
	 */
	public String[] getHarmonyNames( )
	{
		return( harmonyOut.getHarmonyNames() );
	}
	
	/**
	 * Gets the set of BuilderNode instances edited using the EditPack.
	 * @return The set of BuilderNode instances edited using the EditPack.
	 */
	public HashSet<BuilderNode> getElem()
	{
		return( elem );
	}


	/**
	 * Gets the input harmony of the EditPack.
	 * @return The input harmony of the EditPack.
	 */
	public GHarmonyIn getHarmonyIn() {
		return harmonyIn;
	}


	/**
	 * Gets the output harmony of the EditPack.
	 * @return The output harmony of the EditPack.
	 */
	public GHarmonyOut getHarmonyOut() {
		return harmonyOut;
	}
	
	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		int sz = elem.size();
		int count;
		myv.setInt( "sz", sz);
		Iterator<BuilderNode> it = elem.iterator();
		for( count = 0 ; count < sz ; count++ )
		{
			BuilderNode ob = it.next();
			myv.setProperty(  "index_" + count , ob );
		}
		myv.setProperty("HarmonyIn", harmonyIn);
		myv.setProperty("HarmonyOut", harmonyOut);

		out.writeObject(myv);
	}

	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			elem.clear();
			int sz = myv.getInt( "sz");
			int count;
			for( count = 0 ; count < sz ; count++ )
			{
				BuilderNode el = (BuilderNode)( myv.getProperty(  "index_" + count ) );
				elem.add( el );
			}
			harmonyIn = (GHarmonyIn)( myv.getPropertyEx("HarmonyIn") );
			harmonyOut = (GHarmonyOut)( myv.getPropertyEx("HarmonyOut") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	

}


