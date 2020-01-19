





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
import java.util.StringTokenizer;
import java.util.Vector;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.Harmony;

/**
 * Node representing a harmony for removing some of the pitches from an input harmony.
 * 
 * @author tgreen
 *
 */
public class GHarmonyCutter extends GHarmony implements Externalizable {
	
	/**
	 * The input harmony.
	 */
	private GHarmony i1;
	
	/**
	 * String indicating which pitches are to be removed.
	 */
	private String ind = "0 1";

	/**
	 * Constructs the node.
	 */
	public GHarmonyCutter() {
		super();
	}

	@Override
	public Harmony genHarmony(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Harmony)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		String[] h1 = i1.getHarmonyNames();
		boolean[] cuts = genCuts( ind , h1.length );
		
		Harmony w = new HarmonyCutter( i1.genHarmony(s) , ind , cuts );
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "Harmony Cutter" );
	}
	
	@Override
	public String[] getHarmonyNames()
	{
		try
		{
			String[] h1 = i1.getHarmonyNames();
			boolean[] cuts = genCuts( ind , h1.length );
			Vector<String> vct = new Vector<String>();
			int count;
			for( count = 0 ; count < cuts.length ; count++ )
			{
				if( cuts[ count ] )
				{
					vct.add( h1[ count ] );
				}
			}
			String[] ret = new String[ vct.size() ];
			for( count = 0 ; count < vct.size() ; count++ )
			{
				ret[ count ] = vct.elementAt( count );
			}
			return( ret );
		}
		catch( Throwable th )
		{
			String[] h1 = null;
			th.printStackTrace( System.out );
			return( h1 );
		}
	}
	
	/**
	 * Determines which pitches are to be removed.
	 * @param s String indicating which pitches are to be removed.
	 * @param len The number of pitches in the input harmony.
	 * @return Array of booleans indicating which pitches are to be removed.
	 */
	public boolean[] genCuts( String s , int len )
	{
		boolean[] ret = new boolean[ len ];
		if( ret.length > 0 )
		{
			ret[ 0 ] = true;
		}
		StringTokenizer st = new StringTokenizer( s );
		while( st.hasMoreTokens() )
		{
			int nt = Integer.parseInt( st.nextToken() );
			ret[ nt ] = true;
		}
		return( ret );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GHarmony );
	}

	@Override
	public void performAssign(GNode in) {
		i1 = (GHarmony) in;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		HarmonyCutterEditor editor = new HarmonyCutterEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Harmony Cutter Properties");
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
	 * Gets the input harmony.
	 * @return The input harmony.
	 */
	public GHarmony getI1() {
		return i1;
	}

	/**
	 * Sets the input harmony.
	 * @param i1 The input harmony.
	 */
	public void setI1(GHarmony i1) {
		this.i1 = i1;
	}

	/**
	 * Gets the string indicating which pitches are to be removed.
	 * @return String indicating which pitches are to be removed.
	 */
	public String getInd() {
		return ind;
	}

	/**
	 * Sets the string indicating which pitches are to be removed.
	 * @param ind String indicating which pitches are to be removed.
	 */
	public void setInd(String ind) {
		this.ind = ind;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		myv.setProperty("Ind", ind);

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
			ind = (String)( myv.getProperty( "Ind" ) );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

