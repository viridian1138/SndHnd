





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
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.ConstantNonClampedCoefficient;

/**
 * Node representing a waveform that adds presence to an input waveform 
 * by adding harmonics that are up to seven octaves below the original tonic.
 * 
 * @author tgreen
 *
 */
public class GOmniPresent extends GWaveForm  implements Externalizable {
	
	/**
	 * The contribution at the tonic.
	 */
	protected double a1 = 1.0;
	
	/**
	 * Contribution at 1 / 2 (i.e. one octave down).
	 */
	protected double ao2 = 0.0;
	
	/**
	 * Contribution at 1 / 4 (i.e. two octaves down).
	 */
	protected double ao4 = 0.0;
	
	/**
	 * Contribution at 1 / 8 (i.e. three octaves down).
	 */
	protected double ao8 = 0.0;
	
	/**
	 * Contribution at 1 / 16 (i.e. four octaves down).
	 */
	protected double ao16 = 0.0;
	
	/**
	 * Contribution at 1 / 32 (i.e. five octaves down).
	 */
	protected double ao32 = 0.0;
	
	/**
	 * Contribution at 1 / 64 (i.e. six octaves down).
	 */
	protected double ao64 = 0.0;
	
	/**
	 * Contribution at 1 / 128 (i.e. seven octaves down).
	 */
	protected double ao128 = 0.0;

	/**
	 * The input waveform.
	 */
	private GWaveForm chld;
	
	/**
	 * Constructs the node.
	 */
	public GOmniPresent()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = chld.genWave(s);
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		if( Math.abs( ao2 ) > 1E-30 )
		{
			coefficients.add( w );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( ao2 ));
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / 2.0 ));
		}
		
		if( Math.abs( ao4 ) > 1E-30 )
		{
			coefficients.add( w );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( ao4 ));
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / 4.0 ));
		}
		
		if( Math.abs( ao8 ) > 1E-30 )
		{
			coefficients.add( w );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( ao8 ));
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / 8.0 ));
		}
		
		if( Math.abs( ao16 ) > 1E-30 )
		{
			coefficients.add( w );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( ao16 ));
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / 16.0 ));
		}
		
		if( Math.abs( ao32 ) > 1E-30 )
		{
			coefficients.add( w );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( ao32 ));
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / 32.0 ));
		}
		
		if( Math.abs( ao64 ) > 1E-30 )
		{
			coefficients.add( w );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( ao64 ));
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / 64.0 ));
		}
		
		if( Math.abs( ao128 ) > 1E-30 )
		{
			coefficients.add( w );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( ao128 ));
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / 128.0 ));
		}
		
		
		WaveForm wave3 = new AdditiveWaveForm( w , new ConstantNonClampedCoefficient( a1 ) ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		s.put(this, wave3);
		
		return( wave3 );
	}
	
   /**
    * Loads new values into the node.
    * @param in The input waveform.
    */
	public void load( GWaveForm in )
	{
		chld = in;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		OmniPresentEditor editor = new OmniPresentEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"OmniPresent Properties");
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "OmniPresent" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		chld = (GWaveForm) in;

	}

	@Override
	public void removeChld() {
		chld = null;
	}

	
	/**
	 * Gets the contribution at the tonic.
	 * @return The contribution at the tonic.
	 */
	public double getA1() {
		return a1;
	}

	/**
	 * Sets the contribution at the tonic.
	 * @param a1 The  contribution at the tonic.
	 */
	public void setA1(double a1) {
		this.a1 = a1;
	}

	/**
	 * Gets the contribution at 1 / 2 (i.e. one octave down).
	 * @return The contribution at 1 / 2 (i.e. one octave down).
	 */
	public double getAo2() {
		return ao2;
	}

	/**
	 * Sets the contribution at 1 / 2 (i.e. one octave down).
	 * @param ao2 The contribution at 1 / 2 (i.e. one octave down).
	 */
	public void setAo2(double ao2) {
		this.ao2 = ao2;
	}

	/**
	 * Gets the contribution at 1 / 4 (i.e. two octaves down).
	 * @return The contribution at 1 / 4 (i.e. two octaves down).
	 */
	public double getAo4() {
		return ao4;
	}

	/**
	 * Sets the contribution at 1 / 4 (i.e. two octaves down).
	 * @param ao4 The contribution at 1 / 4 (i.e. two octaves down).
	 */
	public void setAo4(double ao4) {
		this.ao4 = ao4;
	}

	/**
	 * Gets the contribution at 1 / 8 (i.e. three octaves down).
	 * @return The contribution at 1 / 8 (i.e. three octaves down).
	 */
	public double getAo8() {
		return ao8;
	}

	/**
	 * Sets the contribution at 1 / 8 (i.e. three octaves down).
	 * @param ao8 The contribution at 1 / 8 (i.e. three octaves down).
	 */
	public void setAo8(double ao8) {
		this.ao8 = ao8;
	}

	/**
	 * Gets the contribution at 1 / 16 (i.e. four octaves down).
	 * @return The contribution at 1 / 16 (i.e. four octaves down).
	 */
	public double getAo16() {
		return ao16;
	}

	/**
	 * Sets the contribution at 1 / 16 (i.e. four octaves down).
	 * @param ao16 The contribution at 1 / 16 (i.e. four octaves down).
	 */
	public void setAo16(double ao16) {
		this.ao16 = ao16;
	}

	/**
	 * Gets the contribution at 1 / 32 (i.e. five octaves down).
	 * @return The contribution at 1 / 32 (i.e. five octaves down).
	 */
	public double getAo32() {
		return ao32;
	}

	/**
	 * Sets the contribution at 1 / 32 (i.e. five octaves down).
	 * @param ao32 The contribution at 1 / 32 (i.e. five octaves down).
	 */
	public void setAo32(double ao32) {
		this.ao32 = ao32;
	}
	
	/**
	 * Gets the contribution at 1 / 64 (i.e. six octaves down).
	 * @return The contribution at 1 / 64 (i.e. six octaves down).
	 */
	public double getAo64() {
		return ao64;
	}

	/**
	 * Sets the contribution at 1 / 64 (i.e. six octaves down).
	 * @param ao64 The contribution at 1 / 64 (i.e. six octaves down).
	 */
	public void setAo64(double ao64) {
		this.ao64 = ao64;
	}
	
	/**
	 * Gets the contribution at 1 / 128 (i.e. seven octaves down).
	 * @return The contribution at 1 / 128 (i.e. seven octaves down).
	 */
	public double getAo128() {
		return ao128;
	}

	/**
	 * Sets the contribution at 1 / 128 (i.e. seven octaves down).
	 * @param ao128 The contribution at 1 / 128 (i.e. seven octaves down).
	 */
	public void setAo128(double ao128) {
		this.ao128 = ao128;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("A1", a1);
		myv.setDouble("Ao2", ao2);
		myv.setDouble("Ao4", ao4);
		myv.setDouble("Ao8", ao8);
		myv.setDouble("Ao16", ao16);
		myv.setDouble("Ao32", ao32);
		myv.setDouble("Ao64", ao64);
		myv.setDouble("Ao128", ao128);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			chld = (GWaveForm)( myv.getProperty("Chld") );
			a1 = myv.getDouble("A1");
			ao2 = myv.getDouble("Ao2");
			ao4 = myv.getDouble("Ao4");
			ao8 = myv.getDouble("Ao8");
			ao16 = myv.getDouble("Ao16");
			ao32 = myv.getDouble("Ao32");
			ao64 = myv.getDouble("Ao64");
			ao128 = myv.getDouble("Ao128");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
