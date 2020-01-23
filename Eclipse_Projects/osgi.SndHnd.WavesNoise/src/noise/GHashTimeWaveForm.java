





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
 * Node representing a waveform that perturbs an input wave by applying an input noise function to the time parameter.
 * Using a GHashWhiteWaveForm as the noise parameter and a ramp wave as the waveform effectively produces time-hashed noise.
 * @author tgreen
 *
 */
public class GHashTimeWaveForm extends GWaveForm implements Externalizable {

	/**
	 * Input waveform.
	 */
	private GWaveForm chldA;
	
	/**
	 * Input noise function.
	 */
	private GWaveForm chldB;
	
	/**
	 * Parameter controlling how much of the input wave allow to pass through unchanged.
	 */
	private double passThrough = 0.5;
	
	/**
	 * Constructs the node.
	 */
	public GHashTimeWaveForm()
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
		
		WaveForm wv = gen( wA , wB , passThrough );
		s.put(this, wv);
		
		return( wv );
	}

	/**
	 * Generates the associated waveform.
	 * @param _wave  Input waveform.
	 * @param _noise Input noise function.
	 * @param _passThrough Parameter controlling how much of the input wave allow to pass through unchanged.
	 * @return The associated waveform.
	 */
	public static WaveForm gen( WaveForm _wave , WaveForm _noise , double _passThrough )
	{
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		ConstantNonClampedCoefficient pa = new ConstantNonClampedCoefficient(_passThrough);
		ConstantNonClampedCoefficient pb = new ConstantNonClampedCoefficient(1.0 - _passThrough);
		
		ConstantNonClampedCoefficient whole = new ConstantNonClampedCoefficient(1.0);
		
		coefficients.add( _wave );
		coefficientCoefficients.add( pb );
		parameterCoefficients.add( whole );
		
		AdditiveWaveForm addi = new AdditiveWaveForm( _noise , 
				pa , coefficients ,
				coefficientCoefficients , parameterCoefficients );
		return( addi );
	}

	public Object getChldNodes() {
		Object[] ob = { chldA , chldB };
		return( ob );
	}

	@Override
	public String getName() {
		return( "Noise -- Hash Time" );
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
		HashTimeWaveFormEditor editor = new HashTimeWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Hash Time Wave Form Properties");
	}

	/**
	 * Gets the parameter controlling how much of the input wave allow to pass through unchanged.
	 * @return Parameter controlling how much of the input wave allow to pass through unchanged.
	 */
	public double getPassThrough() {
		return passThrough;
	}

	/**
	 * Sets the parameter controlling how much of the input wave allow to pass through unchanged.
	 * @param passThrough Parameter controlling how much of the input wave allow to pass through unchanged.
	 */
	public void setPassThrough(double passThrough) {
		this.passThrough = passThrough;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chldA != null ) myv.setProperty("ChldA", chldA);
		if( chldB != null ) myv.setProperty("ChldB", chldB);
		myv.setDouble("PassThrough", passThrough);

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
			passThrough = myv.getDouble("PassThrough");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
