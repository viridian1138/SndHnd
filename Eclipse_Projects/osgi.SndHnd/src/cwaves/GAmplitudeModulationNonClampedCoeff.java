




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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;


/**
 * 
 * Node representing a non-Clamped Coefficient performing amplitude modulation upon an input non-clamped coefficient.
 * 
 * See: https://www.soundonsound.com/techniques/amplitude-modulation
 * 
 * The modulating input can also amplify the level of the input wave for implementations of distortion and/or overdriving.
 * 
 * See: https://en.wikipedia.org/wiki/Distortion_(music)
 * 
 * @author thorngreen
 */
public class GAmplitudeModulationNonClampedCoeff extends
		GNonClampedCoefficient implements Externalizable {
	
	/**
	 * The input coefficient.
	 */
	GNonClampedCoefficient coeff1;
	
	/**
	 * The drive (amplification) function that modulates the input coefficient.
	 */
	GNonClampedCoefficient coeff2;

	@Override
	public NonClampedCoefficient genCoeff(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		NonClampedCoefficient c1 = coeff1.genCoeff(s);
		NonClampedCoefficient c2 = coeff2.genCoeff(s);
		
		AmplitudeModulationNonClampedCoeff wv = new AmplitudeModulationNonClampedCoeff( c1 , c2 );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Object[] ob = { coeff1 , coeff2 };
		return( ob );
	}
	
	/**
	 * Loads new values into the node.
	 * @param c1 The input coefficient.
	 * @param c2 The drive (amplification) function that modulates the input coefficient.
	 */
	public void load( GNonClampedCoefficient c1 , GNonClampedCoefficient c2 )
	{
		coeff1 = c1;
		coeff2 = c2;
	}

	@Override
	public String getName() {
		return( "AmplitudeModulationNonClamped" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GNonClampedCoefficient );
	}

	public void performAssign(GNode in) {
		GNonClampedCoefficient cf = (GNonClampedCoefficient) in;
		if( coeff1 == null )
		{
			coeff1 = cf;
		}
		coeff2 = cf;
	}

	@Override
	public void removeChld() {
		coeff1 = null;
		coeff2 = null;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( coeff1 != null ) myv.setProperty("Coeff1", coeff1);
		if( coeff2 != null ) myv.setProperty("Coeff2", coeff2);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coeff1 = (GNonClampedCoefficient)( myv.getProperty("Coeff1") );
			coeff2 = (GNonClampedCoefficient)( myv.getProperty("Coeff2") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
