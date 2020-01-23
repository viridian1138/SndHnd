





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







package noise;

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
 * Node representing a waveform for Value-Gradient Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GVGNoiseWaveForm extends GWaveForm implements Externalizable {

	/**
	 * Value-Noise Component of Value-Gradient Noise
	 */
	private GWaveForm chldA;
	
	/**
	 * Gradient-Noise Component of Value-Gradient Noise
	 */
	private GWaveForm chldB;
	
	/**
	 * Coefficient for mixing the noise components.
	 */
	private double sz = 0.5;
	
	/**
	 * Constructs the node.
	 */
	public GVGNoiseWaveForm()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm wA = chldA.genWave(s);
		WaveForm wB = chldB.genWave(s);
		
		WaveForm wv = gen( wA , wB , sz );
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Generates the waveform for the Value-Gradient noise.
	 * @param _inoise1  Value-Noise Component of Value-Gradient Noise
	 * @param _inoise2  Gradient-Noise Component of Value-Gradient Noise
	 * @param _sz  Coefficient for mixing the noise components.
	 * @return Waveform for the Value-Gradient noise.
	 */
	public static WaveForm gen( WaveForm _inoise1 , WaveForm _inoise2 , double _sz )
	{
		WaveForm vn = new VnoiseWaveform(_inoise1,_sz);
		WaveForm gn = new GnoiseWaveform(_inoise2,_sz);
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		ConstantNonClampedCoefficient half = new ConstantNonClampedCoefficient(0.5);
		ConstantNonClampedCoefficient whole = new ConstantNonClampedCoefficient(1.0);
		
		coefficients.add( gn );
		coefficientCoefficients.add( half );
		parameterCoefficients.add( whole );
		
		AdditiveWaveForm addi = new AdditiveWaveForm( vn , 
				half , coefficients ,
				coefficientCoefficients , parameterCoefficients );
		return( addi );
	}

	public Object getChldNodes() {
		Object[] ob = { chldA , chldB };
		return( ob );
	}

	@Override
	public String getName() {
		return( "Noise -- VG Noise" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		GWaveForm c = (GWaveForm) in;
		if( chldA == null )
		{
			chldA = c;
			return;
		}
		chldB = c;

	}

	@Override
	public void removeChld() {
		chldA = null;
		chldB = null;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		VGNoiseWaveFormEditor editor = new VGNoiseWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"VG Noise Wave Form Properties");
	}

	/**
	 * Gets the coefficient for mixing the noise components.
	 * @return Coefficient for mixing the noise components.
	 */
	public double getSz() {
		return sz;
	}

	/**
	 * Sets the coefficient for mixing the noise components.
	 * @param sz Coefficient for mixing the noise components.
	 */
	public void setSz(double sz) {
		this.sz = sz;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chldA != null ) myv.setProperty("ChldA", chldA);
		if( chldB != null ) myv.setProperty("ChldB", chldB);
		myv.setDouble("Sz", sz);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			chldA = (GWaveForm)( myv.getProperty("ChldA") );
			chldB = (GWaveForm)( myv.getProperty("ChldB") );
			sz = myv.getDouble("Sz");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

