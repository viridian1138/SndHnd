





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







package gcoeff;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import core.NonClampedCoefficientMultiCore;


/**
 * Sampler for applying a simple reverb described by a global text file "reverb.txt".
 * 
 * @author tgreen
 *
 */
public class ReverbCoefficient extends NonClampedCoefficientMultiCore {
	
	/**
	 * Map of sampling coefficients to be used in applying the reverb.
	 */
	protected final HashMap<Double,Double> reverbMap = new HashMap<Double,Double>();
	
	/**
	 * The input coefficient onto which to apply the reverb.
	 */
	protected NonClampedCoefficientMultiCore coeff;

	/**
	 * Constructs the sampler.
	 * @param _coeff  The input coefficient onto which to apply the reverb.
	 * @throws Throwable
	 */
	public ReverbCoefficient( NonClampedCoefficientMultiCore _coeff ) throws Throwable {
		super();
		coeff = _coeff;
		
		FileReader is = new FileReader( "reverb.txt" );
		LineNumberReader li = new LineNumberReader( is );
		String numL = li.readLine();
		int max = Integer.parseInt( numL );
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			String line = li.readLine();
			StringTokenizer st = new StringTokenizer( line , "," );
			String tdel = st.nextToken();
			String tval = st.nextToken();
			Double key = new Double( tdel );
			Double value = new Double( tval );
			reverbMap.put( key , value );
		}
	}

	@Override
	public double eval( double param , final int core ) {
		double sum = 0.0;
		for( Entry<Double,Double> e : reverbMap.entrySet() )
		{
			Double key = e.getKey();
			Double value = e.getValue();
			double eval = coeff.eval( param - ( key.doubleValue() ) , core );
			sum += eval * ( value.doubleValue() );
		}
		return( sum );
	}

	/**
	* Reads the node from serial storage.
	*/
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	
}

