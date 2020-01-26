




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







package cwaves;


import gredit.GClampedCoefficient;
import gredit.GNode;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.VersionBuffer;
import core.ClampedCoefficient;
import core.NonClampedCoefficient;

/**
 * A clamped coefficient that remodulates the parameter space of an input clamped coefficient by a simple multiplication.
 * 
 * @author tgreen
 *
 */
public class ClampedCoefficientRemodulator extends ClampedCoefficient {
	
	/**
	 * The input clamped coefficient.
	 */
	protected ClampedCoefficient coeff;
	
	/**
	 * The input parameter multiplier.
	 */
	protected double multiplier;

	/**
	 * Constructs the clamped coefficient.
	 * @param _coeff The input clamped coefficient.
	 * @param _multiplier The input parameter multiplier.
	 */
	public ClampedCoefficientRemodulator( ClampedCoefficient _coeff , double _multiplier ) {
		super();
		coeff = _coeff;
		multiplier = _multiplier;
	}
	
	/**
	 * 
	 * Constructor for serial storage purposes only.
	 *
	 */
	public ClampedCoefficientRemodulator()
	{
		super();
	}

	@Override
	public double eval( double param ) {
		return( coeff.eval( multiplier * param ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final ClampedCoefficient cl = (ClampedCoefficient)( coeff.genClone() );
		if( cl == coeff )
		{
			return( this );
		}
		else
		{
			return( new ClampedCoefficientRemodulator( cl , multiplier ) );
		}
	}
	
	@Override
	public GClampedCoefficient genClamped( HashMap<Object,GNode> s )
	{
		if( s.get( this ) != null )
		{
			return( (GClampedCoefficient)( s.get( this ) ) );
		}
		
		GClampedCoefficientRemodulator wv = new GClampedCoefficientRemodulator();
		s.put(this, wv);
		
		GClampedCoefficient w = coeff.genClamped(s);
		
		wv.load(w,multiplier);
		
		return( wv );
	}

	/**
	* Reads the node from serial storage.
	*/
	public void writeExternal(ObjectOutput in) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		myv.setProperty( "coeff" , coeff );
		myv.setDouble("multiplier",multiplier);
		
		in.writeObject(myv);
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		VersionBuffer myv = (VersionBuffer) (in.readObject());
		VersionBuffer.chkNul(myv);
		
		coeff = (ClampedCoefficient)( myv.getPropertyEx( "coeff" ) );
		multiplier = myv.getDouble("multiplier");
	}

}

