





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
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import core.Harmony;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;

/**
 * A node representing a harmony that stacks pitches from an input harmony upward to a 
 * successive melodic interval.  An example of this would be
 * octave stacking on a typical intonation using octaves.
 * 
 * @author tgreen
 *
 */
public class GHarmonyMelodicIntervalDoubling extends GHarmony  implements Externalizable {
	
	/**
	 * The input harmony to be stacked.
	 */
	private GHarmony i1;
	
	/**
	 * The input intonation from which to get the melodic interval ratio.
	 */
	private GIntonation i2;
	
	/**
	 * String listing which indices of the harmony should be used.
	 */
	private String ind = "0 1";
	
	/**
	 * The number of melodic intervals to jump upward when stacking.
	 */
	private int noteMelodicInterval = 1;
	
	/**
	 * Constructs the node.
	 */
	public GHarmonyMelodicIntervalDoubling() {
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
		boolean[] mults = genMults( ind , h1.length );
		
		Harmony w = new HarmonyMelodicIntervalDoubling( i1.genHarmony(s) , i2.genInton(s) ,  ind , noteMelodicInterval , mults );
		
		s.put(this, w);
		
		return( w );
	}
	
	@Override
	public String[] getHarmonyNames()
	{
		String[] h1n = i1.getHarmonyNames();
		boolean[] mults = genMults( ind , h1n.length );
		double[] h1 = i1.genHarmony(new HashMap()).calcHarmony();
		final double melr = i2.genInton(new HashMap()).getMelodicIntervalRatio();
		final double mult = Math.pow( melr , noteMelodicInterval );
		HashSet<Double> cache = new HashSet<Double>();
		
		Vector<String> vct = new Vector<String>();
		int count;
		for( count = 0 ; count < h1.length ; count++ )
		{
			vct.add( h1n[ count ] );
			cache.add( h1[ count ] );
		}
		
		
		for( count = 0 ; count < mults.length ; count++ )
		{
			if( mults[ count ] )
			{
				final double hm = h1[ count ] * mult;
				if( !( cache.contains( hm ) ) )
				{
					vct.add( h1n[ count ] + "+" + noteMelodicInterval );
				}
			}
		}
		
		
		String[] ret = new String[ vct.size() ];
		for( count = 0 ; count < vct.size() ; count++ )
		{
			ret[ count ] = vct.elementAt( count );
		}
		
		return( ret );
	}
	
	/**
	 * Determines which pitches from the input harmony are to be stacked.
	 * @param s String indicating which pitches are to be stacked.
	 * @param len The number of pitches in the input harmony.
	 * @return Array of booleans indicating which pitches are to participate in the stacking.
	 */
	public boolean[] genMults( String s , int len )
	{
		boolean[] ret = new boolean[ len ];
		StringTokenizer st = new StringTokenizer( s );
		while( st.hasMoreTokens() )
		{
			int nt = Integer.parseInt( st.nextToken() );
			ret[ nt ] = true;
		}
		return( ret );
	}

	@Override
	public String getName() {
		return( "HarmonyMelodicIntervalDoubling" );
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

	public Object getChldNodes() {
		Object[] ob = { i1 , i2 };
		return( ob );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		HarmonyMelodicIntervalDoublingEditor editor = new HarmonyMelodicIntervalDoublingEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Harmony MelodicIntervalDoubling Properties");
	}
	
	/**
	 * Gets the input harmony to be stacked.
	 * @return The input harmony to be stacked.
	 */
	public GHarmony getI1() {
		return i1;
	}

	/**
	 * Sets the input harmony to be stacked.
	 * @param i1 The input harmony to be stacked.
	 */
	public void setI1(GHarmony i1) {
		this.i1 = i1;
	}
	
	/**
	 * Gets the input intonation from which to get the melodic interval ratio.
	 * @return The input intonation from which to get the melodic interval ratio.
	 */
	public GIntonation getI2() {
		return i2;
	}

	/**
	 * Sets the input intonation from which to get the melodic interval ratio.
	 * @param i2 The input intonation from which to get the melodic interval ratio.
	 */
	public void setI2(GIntonation i2) {
		this.i2 = i2;
	}

	/**
	 * Gets the string listing which indices of the harmony should be used.
	 * @return String listing which indices of the harmony should be used.
	 */
	public String getInd() {
		return ind;
	}

	/**
	 * Sets the string listing which indices of the harmony should be used.
	 * @param ind String listing which indices of the harmony should be used.
	 */
	public void setInd(String ind) {
		this.ind = ind;
	}

	/**
	 * Gets the number of melodic intervals to jump upward when stacking.
	 * @return The number of melodic intervals to jump upward when stacking.
	 */
	public int getNoteMelodicInterval() {
		return noteMelodicInterval;
	}

	/**
	 * Sets the number of melodic intervals to jump upward when stacking.
	 * @param noteMelodicInterval The number of melodic intervals to jump upward when stacking.
	 */
	public void setNoteMelodicInterval(int noteMelodicInterval) {
		this.noteMelodicInterval = noteMelodicInterval;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		if( i2 != null ) myv.setProperty("I2", i2);
		myv.setProperty("Ind", ind);
		myv.setInt("NoteMelodicInterval", noteMelodicInterval);

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
			ind = (String)( myv.getProperty( "Ind" ) );
			noteMelodicInterval = myv.getInt( "NoteMelodicInterval" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

