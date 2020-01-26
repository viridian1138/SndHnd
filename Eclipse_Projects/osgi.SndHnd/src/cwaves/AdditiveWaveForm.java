




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


import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;



/**
 * Waveform performing additive synthesis on a set of inputs.
 * 
 * See https://theproaudiofiles.com/what-is-additive-synthesis/
 * 
 * @author thorngreen
 *
 */
public class AdditiveWaveForm extends WaveForm implements Externalizable {
	
	/**
	 * The primary input waveform.
	 */
	WaveForm primaryWaveForm;
	
	/**
	 * The coefficient to multiply by the primary input waveform.
	 */
	NonClampedCoefficient primaryCoefficient;
	
	/**
	 * Coefficients to be added to the primary input waveform.
	 */
	ArrayList<NonClampedCoefficient> coefficients;
	
	/**
	 * The coefficients to multiply by the coefficients to be added to the primary input waveform.
	 */
	ArrayList<NonClampedCoefficient> coefficientCoefficients;
	
	/**
	 * The coefficients to multiply by the parameter for evaluating the coefficients to be added to the primary input waveform.
	 */
	ArrayList<NonClampedCoefficient> parameterCoefficients;
	
	/**
	 * Constructs the waveform.
	 * @param _primaryWaveForm The primary input waveform.
	 * @param _primaryCoefficient The coefficient to multiply by the primary input waveform.
	 * @param _coefficients Coefficients to be added to the primary input waveform.
	 * @param _coefficientCoefficients The coefficients to multiply by the coefficients to be added to the primary input waveform.
	 * @param _parameterCoefficients The coefficients to multiply by the parameter for evaluating the coefficients to be added to the primary input waveform.
	 */
	public AdditiveWaveForm( WaveForm _primaryWaveForm , 
			NonClampedCoefficient _primaryCoefficient , ArrayList<NonClampedCoefficient> _coefficients ,
			ArrayList<NonClampedCoefficient> _coefficientCoefficients , ArrayList<NonClampedCoefficient> _parameterCoefficients )
	{
		primaryWaveForm = _primaryWaveForm;
		primaryCoefficient = _primaryCoefficient;
		coefficients = _coefficients;
		coefficientCoefficients = _coefficientCoefficients;
		parameterCoefficients = _parameterCoefficients;
	}

	/**
	 * Constructor used for persistence purposes only.
	 */
	public AdditiveWaveForm() {
		super();
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GAdditiveWaveForm wv = new GAdditiveWaveForm();
		s.put(this, wv);
		
		GWaveForm pri = primaryWaveForm.genWave(s);
		GNonClampedCoefficient pric = primaryCoefficient.genCoeff(s);
		
		Vector<GAdditivePacket> pk = new Vector<GAdditivePacket>();
		
		int len = coefficients.size();
		int cnt;
		for( cnt = 0 ; cnt < len ; cnt++ )
		{
			NonClampedCoefficient cf = coefficients.get(cnt);
			NonClampedCoefficient ccf = coefficientCoefficients.get(cnt);
			NonClampedCoefficient pcf = parameterCoefficients.get(cnt);
			GAdditivePacket packet = new GAdditivePacket();
			packet.load( cf.genCoeff(s) , ccf.genCoeff(s) , pcf.genCoeff(s) );
			pk.add(packet);
		}
		
		wv.load(pri,pric,pk);
		
		return( wv );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm pri = (WaveForm)( primaryWaveForm.genClone() );
		final NonClampedCoefficient pric = primaryCoefficient.genClone();
		final int sz = coefficients.size();
		final ArrayList<NonClampedCoefficient> coeff = new ArrayList<NonClampedCoefficient>( sz );
		final ArrayList<NonClampedCoefficient> coeffCoeff = new ArrayList<NonClampedCoefficient>( sz );
		final ArrayList<NonClampedCoefficient> paramCoeff = new ArrayList<NonClampedCoefficient>( sz );
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			coeff.add( coefficients.get( count ).genClone() );
			coeffCoeff.add( coefficientCoefficients.get( count ).genClone() );
			paramCoeff.add( parameterCoefficients.get( count ).genClone() );
		}
		boolean match = true;
		count = 0;
		while( ( count < sz ) && match )
		{
			match = match && ( coefficients.get(count) == coeff.get(count) ) && 
				( coefficientCoefficients.get(count) == coeffCoeff.get(count) ) && 
				( parameterCoefficients.get(count) == paramCoeff.get(count) );
			count++;
		}
		if( match )
		{
			return( this );
		}
		else
		{
			return( new AdditiveWaveForm( pri , pric , coeff , coeffCoeff , paramCoeff ) );
		}
	}
	
	/**
	 * Gets the primary input waveform.
	 * @return The primary input waveform.
	 */
	public WaveForm getPrimaryWaveForm()
	{
		return( primaryWaveForm );
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		double e1 = primaryWaveForm.eval( non_phase_distorted_param )
			* primaryCoefficient.eval( non_phase_distorted_param );
		int count;
		int sz = coefficients.size();
		for( count = 0 ; count < sz ; count++ )
		{
			NonClampedCoefficient c1 = 
				coefficients.get( count );
			NonClampedCoefficient c2 = 
				coefficientCoefficients.get( count );
			NonClampedCoefficient p = 
				parameterCoefficients.get( count );
			e1 += 
				c1.eval( non_phase_distorted_param * p.eval( non_phase_distorted_param ) ) 
				* c2.eval( non_phase_distorted_param );
		}
		
		return( e1 );
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("PrimaryWaveForm", primaryWaveForm);
		myv.setProperty("PrimaryCoefficient", primaryCoefficient);

		myv.setInt("CoeffSize", coefficients.size());
		int slen = coefficients.size();
		int count;
		for (count = 0; count < slen; count++) {
			myv.setProperty("Coefficient_" + count, coefficients.get(count));
			myv.setProperty("CoefficientCoefficient_" + count, coefficientCoefficients.get(count));
			myv.setProperty("ParameterCoefficient_" + count, parameterCoefficients.get(count));
		}

		out.writeObject(myv);
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			primaryWaveForm = (WaveForm)( myv.getProperty("PrimaryWaveForm") );
			primaryCoefficient = (NonClampedCoefficient)( myv.getProperty("PrimaryCoefficient") );

			int slen = myv.getInt("CoeffSize");
			coefficients = new ArrayList<NonClampedCoefficient>();
			coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
			parameterCoefficients = new ArrayList<NonClampedCoefficient>();
			System.out.println( "size : " + slen );
			int count;
			for (count = 0; count < slen; count++) {
				coefficients.add((NonClampedCoefficient)(myv.getPropertyEx("Coefficient_" + count)));
				coefficientCoefficients.add((NonClampedCoefficient)(myv.getPropertyEx("CoefficientCoefficient_" + count)));
				parameterCoefficients.add((NonClampedCoefficient)(myv.getPropertyEx("ParameterCoefficient_" + count)));
			}
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

