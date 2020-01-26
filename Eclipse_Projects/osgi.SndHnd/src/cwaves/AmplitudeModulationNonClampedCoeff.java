




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


import gredit.GNode;
import gredit.GNonClampedCoefficient;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;

import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * 
 * Non-Clamped Coefficient performing amplitude modulation upon an input non-clamped coefficient.
 * 
 * See: https://www.soundonsound.com/techniques/amplitude-modulation
 * 
 * The modulating input can also amplify the level of the input wave for implementations of distortion and/or overdriving.
 * 
 * See: https://en.wikipedia.org/wiki/Distortion_(music)
 * 
 * @author thorngreen
 */
public class AmplitudeModulationNonClampedCoeff extends NonClampedCoefficient {
	
	/**
	 * The input coefficient.
	 */
	NonClampedCoefficient coeff1;
	
	/**
	 * The drive (amplification) function that modulates the input coefficient.
	 */
	NonClampedCoefficient coeff2;

	/**
	 * Constructs the coefficient.
	 * @param _coeff1 The input coefficient.
	 * @param _coeff2 The drive (amplification) function that modulates the input coefficient.
	 */
	public AmplitudeModulationNonClampedCoeff( NonClampedCoefficient _coeff1 , NonClampedCoefficient _coeff2 ) {
		super();
		coeff1 = _coeff1;
		coeff2 = _coeff2;
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		return( coeff1.eval( non_phase_distorted_param ) 
					* coeff2.eval( non_phase_distorted_param ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final NonClampedCoefficient c1 = coeff1.genClone();
		final NonClampedCoefficient c2 = coeff2.genClone();
		if( ( c1 == coeff1 ) && ( c2 == coeff2 ) )
		{
			return( this );
		}
		else
		{
			return( new AmplitudeModulationNonClampedCoeff( c1 , c2 ) );
		}
	}

	@Override
	public GNonClampedCoefficient genCoeff( HashMap<Object,GNode> s )
	{
		if( s.get( this ) != null )
		{
			return( (GNonClampedCoefficient)( s.get( this ) ) );
		}
		
		GAmplitudeModulationNonClampedCoeff wv = new GAmplitudeModulationNonClampedCoeff();
		s.put(this, wv);
		
		GNonClampedCoefficient c1 = coeff1.genCoeff(s);
		GNonClampedCoefficient c2 = coeff2.genCoeff(s);
		wv.load(c1,c2);
		
		return( wv );
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("coeff1", coeff1);
		myv.setProperty("coeff2", coeff2);

		out.writeObject(myv);
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coeff1 = (NonClampedCoefficient)( myv.getPropertyEx("coeff1") );
			coeff2 = (NonClampedCoefficient)( myv.getPropertyEx("coeff2") );
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

