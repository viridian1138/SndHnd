




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

/**
 * A packaged collection of coefficients describing a single contribution to additive synthesis using a GAdditiveWaveForm.
 * 
 * @author tgreen
 *
 */
public class GAdditivePacket extends GNode implements Externalizable {
	
	/**
	 * Coefficient to be added to the primary input waveform.
	 */
	GNonClampedCoefficient coefficient;
	
	/**
	 * The coefficient to multiply by the coefficient to be added to the primary input waveform.
	 */
	GNonClampedCoefficient coefficientCoefficient;
	
	/**
	 * The coefficient to multiply by the parameter for evaluating the coefficient to be added to the primary input waveform.
	 */
	GNonClampedCoefficient parameterCoefficient;
	
	
	/**
	 * Constructs the packet.
	 */
	public GAdditivePacket()
	{
	}
	
	/**
	 * Loads new values into the packet.
	 * @param _coefficient Coefficient to be added to the primary input waveform.
	 * @param _coefficientCoefficient The coefficient to multiply by the coefficient to be added to the primary input waveform.
	 * @param _parameterCoefficient The coefficient to multiply by the parameter for evaluating the coefficient to be added to the primary input waveform.
	 */
	public void load( GNonClampedCoefficient _coefficient ,
			GNonClampedCoefficient _coefficientCoefficient ,
			GNonClampedCoefficient _parameterCoefficient )
	{
		coefficient = _coefficient;
		coefficientCoefficient = _coefficientCoefficient;
		parameterCoefficient = _parameterCoefficient;
	}

	@Override
	public Object genObj(HashMap s) {
		throw( new RuntimeException( "NotSupported" ) );
	}

	public Object getChldNodes() {
		Object[] ob = { coefficient , coefficientCoefficient , parameterCoefficient };
		return(ob);
	}

	@Override
	public String getName() {
		return( "AdditivePacket" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GNonClampedCoefficient );
	}

	@Override
	public void performAssign(GNode in) {
		GNonClampedCoefficient c = (GNonClampedCoefficient) in;
		if( coefficient == null )
		{
			coefficient = c;
			return;
		}
		if( coefficientCoefficient == null )
		{
			coefficientCoefficient = c;
			return;
		}
		//if( parameterCoefficient == null )
		//{
			parameterCoefficient = c;
		//	return;
		//}

	}

	@Override
	public void removeChld() {
		coefficient = null;
		coefficientCoefficient = null;
		parameterCoefficient = null;
	}

	/**
	 * The coefficient to be added to the primary input waveform.
	 * @return The coefficient to be added to the primary input waveform.
	 */
	public GNonClampedCoefficient getCoefficient() {
		return coefficient;
	}

	/**
	 * Gets the coefficient to multiply by the coefficient to be added to the primary input waveform.
	 * @return The coefficient to multiply by the coefficient to be added to the primary input waveform.
	 */
	public GNonClampedCoefficient getCoefficientCoefficient() {
		return coefficientCoefficient;
	}

	/**
	 * Gets the coefficient to multiply by the parameter for evaluating the coefficient to be added to the primary input waveform.
	 * @return The coefficient to multiply by the parameter for evaluating the coefficient to be added to the primary input waveform.
	 */
	public GNonClampedCoefficient getParameterCoefficient() {
		return parameterCoefficient;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( coefficient != null ) myv.setProperty("Coefficient", coefficient);
		if( coefficientCoefficient != null ) myv.setProperty("CoefficientCoefficient", coefficientCoefficient);
		if( parameterCoefficient != null ) myv.setProperty("ParameterCoefficient", parameterCoefficient);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coefficient = (GNonClampedCoefficient)( myv.getProperty("Coefficient") );
			coefficientCoefficient = (GNonClampedCoefficient)( myv.getProperty("CoefficientCoefficient") );
			parameterCoefficient = (GNonClampedCoefficient)( myv.getProperty("ParameterCoefficient") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

