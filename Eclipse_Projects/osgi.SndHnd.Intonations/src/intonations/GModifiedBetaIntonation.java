





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

import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.Intonation;

/**
 * An abstract base class for a node representing a  modified approximation of the Wendy Carlos Beta intonation.  This version builds the melodic
 * interval from three applications of a 29 / 25 ratio (essentially a minor third).
 * 
 * See https://en.wikipedia.org/wiki/Beta_scale
 * 
 * @author tgreen
 *
 */
public abstract class GModifiedBetaIntonation extends GIntonation implements Externalizable {
	
	
	/**
	 * Returns the default set of names for notes on the modified Wendy Carlos beta intonation.
	 * @return The default set of names for notes on the modified Wendy Carlos beta intonation.
	 */
	private static String[] getScaleNamesStatic()
	{
		final String[] noteNames =
		{
				"A",
				"A#",
				"Bb",
				"B",
				"Cb",
				"C",
				"C#",
				"Db",
				"D",
				"D#",
				"Eb",
				"E"
		};
		return( noteNames );
	}
	
	@Override
	public String[] getScaleNames()
	{
		return( getScaleNamesStatic() );
	}
	
	@Override
	public String[] getPriScaleNames()
	{
		final String[] noteNames =
		{
			"A", "B", "C", "D", "E"
		};
		return( noteNames );
	}
	
	/**
	 * Test driver.
	 */
	public static void main( String[] in )
	{
		//final double ab = 29.0 / 25.0;
		//System.out.println( ab );
		//System.out.println( ab * ab );
		//System.out.println( ab * ab * ab );
		//final double cd = 6.0 / 5.0;
		//System.out.println( cd );
		//System.out.println( cd * cd );
		//System.out.println( cd * cd * cd );
		// System.out.println( Math.log( Math.sqrt( 1.40625 ) ) / Math.log( 2 ) );
		System.out.println( "---" );
		GJustIntonationAMinor intn = new GJustIntonationAMinor();
		String[] scn = intn.getScaleNames();
		double[] val = intn.genInton( new HashMap() ).calcIntonation();
		int count;
		for( count = 0 ; count < 12 ; count++ )
		{
			Intonation nn = new ModifiedBetaIntonationA();
			double[] dv = nn.calcIntonation();
			int valx = (int)( Math.round( 12.0 * ( Math.log( dv[ count ] ) / Math.log( 2.0 ) ) ) );
			System.out.println( scn[ valx ] );
		}
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

