





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


// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! needs editor !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!


/**
 * Node representing a waveform performing convolution reverb on an input wave.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Convolution_reverb">https://en.wikipedia.org/wiki/Convolution_reverb</A>
 * 
 * @author thorngreen
 */
public class GConvolution extends GWaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	GWaveForm waveA;
	
	/**
	 * The impulse response of the reverb.
	 */
	GNonClampedCoefficient impulseResponse;
	
	/**
	 * The length of the period over which to integrate the reverb.
	 */
	double length = 10.0;
	
	/**
	 * The bias to apply to the parameter when integrating the reverb.
	 */
	double bias = 10.0;
	
	/**
	 * The number of samples to use in integrating the reverb.
	 */
	int numSamples = 25;

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm wA = waveA.genWave(s);
		NonClampedCoefficient cf = impulseResponse.genCoeff(s);
		
		Convolution wv = new Convolution( wA , cf , length , bias , numSamples );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Object[] ob = { waveA , impulseResponse };
		return( ob );
	}

	@Override
	public String getName() {
		return("Convolution");
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GNonClampedCoefficient   ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			if( waveA == null ) waveA = (GWaveForm) in; 
				else impulseResponse = (GWaveForm) in;
		}
		if( in instanceof GNonClampedCoefficient )
		{
			impulseResponse = (GNonClampedCoefficient) in;
		}
	}

	@Override
	public void removeChld() {
		waveA = null;
		impulseResponse = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param _waveA The input waveform.
	 * @param _impulseResponse The impulse response of the reverb.
	 * @param _length The length of the period over which to integrate the reverb.
	 * @param _bias The bias to apply to the parameter when integrating the reverb.
	 * @param _numSamples The number of samples to use in integrating the reverb.
	 */
	public void load( GWaveForm _waveA, GNonClampedCoefficient _impulseResponse,
			double _length, double _bias, int _numSamples )
	{
		waveA = _waveA;
		impulseResponse = _impulseResponse;
		length = _length;
		bias = _bias;
		numSamples = _numSamples;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( waveA != null ) myv.setProperty("WaveA", waveA);
		if( impulseResponse != null ) myv.setProperty("ImpulseResponse", impulseResponse);
		myv.setDouble("Length", length);
		myv.setDouble("bias", bias);
		myv.setInt("NumSamples", numSamples);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			waveA = (GWaveForm)( myv.getProperty("WaveA") );
			impulseResponse = (GNonClampedCoefficient)( myv.getProperty("ImpulseResponse") );
			length = myv.getDouble("Length");
			bias = myv.getDouble("bias");
			numSamples = myv.getInt("NumSamples");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

