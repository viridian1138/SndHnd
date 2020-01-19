





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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.Harmony;

/**
 * Node representing a harmony that is a merge of the pitches from two input harmonies.
 * 
 * @author tgreen
 *
 */
public class GHarmonyMerge extends GHarmony implements Externalizable {
	
	/**
	 * Input harmony to be merged.
	 */
	private GHarmony i1;
	
	/**
	 * Input harmony to be merged.
	 */
	private GHarmony i2;

	/**
	 * Constructs the node.
	 */
	public GHarmonyMerge() {
		super();
	}

	@Override
	public Harmony genHarmony(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Harmony)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		Harmony w = new HarmonyMerge( i1.genHarmony(s) , i2.genHarmony(s) );
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "HarmonyMerge" );
	}
	
	@Override
	public String[] getHarmonyNames()
	{
		double[] h1 = i1.genHarmony(new HashMap()).calcHarmony();
		double[] h2 = i2.genHarmony(new HashMap()).calcHarmony();
		String[] h1s = i1.getHarmonyNames();
		String[] h2s = i2.getHarmonyNames();
		HashSet<Double> cache = new HashSet<Double>();
		
		Vector<String> vct = new Vector<String>();
		int count;
		for( count = 0 ; count < h1.length ; count++ )
		{
			vct.add( h1s[ count ] );
			cache.add( h1[ count ] );
		}
		
		
		for( count = 1 ; count < h2.length ; count++ )
		{
			if( !( cache.contains( h2[ count ] ) ) )
			{
				vct.add( h2s[ count ] );
			}
		}
		
		
		String[] ret = new String[ vct.size() ];
		for( count = 0 ; count < vct.size() ; count++ )
		{
			ret[ count ] = vct.elementAt( count );
		}
		
		return( ret );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GHarmony );
	}

	@Override
	public void performAssign(GNode in) {
		if( i1 == null )
		{
			i1 = (GHarmony) in;
		}
		else
		{
			i2 = (GHarmony) in;
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
	
	/**
	 * Gets the input harmony to be merged.
	 * @return The input harmony to be merged.
	 */
	public GHarmony getI1() {
		return i1;
	}

	/**
	 * Sets the input harmony to be merged.
	 * @param i1 The input harmony to be merged.
	 */
	public void setI1(GHarmony i1) {
		this.i1 = i1;
	}

	/**
	 * Gets the input harmony to be merged.
	 * @return The input harmony to be merged.
	 */
	public GHarmony getI2() {
		return i2;
	}

	/**
	 * Sets the input harmony to be merged.
	 * @param i2 The input harmony to be merged.
	 */
	public void setI2(GHarmony i2) {
		this.i2 = i2;
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		if( i2 != null ) myv.setProperty("I2", i2);

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
			i2 = (GHarmony)( myv.getProperty("I2") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

