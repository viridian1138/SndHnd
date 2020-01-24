





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







package waves;


import gredit.GNode;
import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

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
 * A node representing a wave of the basic form ln( e^c1 + e^c2 )
 * 
 * @author tgreen
 *
 */
public class GAmplitudeLnWaveForm extends GWaveForm implements Externalizable {
	
	/**
	 * The c1 component of the function ln( e^c1 + e^c2 )
	 */
	GWaveForm coeff1;
	
	/**
	 * The c2 component of the function ln( e^c1 + e^c2 )
	 */
	GNonClampedCoefficient coeff2;

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm a1 = coeff1.genWave(s);
		NonClampedCoefficient a2 = coeff2.genCoeff(s);
		
		AmplitudeLnWaveForm wv = new AmplitudeLnWaveForm( a1 , a2 );
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param a  The c1 component of the function ln( e^c1 + e^c2 )
	 * @param b  The c2 component of the function ln( e^c1 + e^c2 )
	 */
	public void load( GWaveForm a , GNonClampedCoefficient b )
	{
		coeff1 = a;
		coeff2 = b;
	}

	public Object getChldNodes() {
		Object[] ob = { coeff1 , coeff2 };
		return( ob );
	}

	@Override
	public String getName() {
		return( "AmplitudeLnWaveForm" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GNonClampedCoefficient ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			if( coeff1 == null )
			{
				coeff1 = (GWaveForm) in;
				return;
			}
			coeff2 = (GWaveForm) in;
			return;
		}
		if( in instanceof GNonClampedCoefficient )
		{
			coeff2 = (GNonClampedCoefficient) in;
		}
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

			coeff1 = (GWaveForm)( myv.getProperty("Coeff1") );
			coeff2 = (GNonClampedCoefficient)( myv.getProperty("Coeff2") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

