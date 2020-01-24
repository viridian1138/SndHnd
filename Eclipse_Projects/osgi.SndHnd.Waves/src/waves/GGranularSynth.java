





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
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.GAdditivePacket;

/**
 * Node representing a waveform for Granular Synthesis similar to work by Xenakis.  The class returns the composite of
 * grains from two input waveforms defined by a set of compositing parameters.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Granular_synthesis">https://en.wikipedia.org/wiki/Granular_synthesis</A>
 * 
 * @author thorngreen
 */
public class GGranularSynth extends GWaveForm implements Externalizable {
	
	/**
	 * First waveform from which to select grains.
	 */
	GWaveForm waveA;
	
	/**
	 * Second waveform from which to select grains.
	 */
	GWaveForm waveB;
	
	/**
	 * Additive packet for selecting grains from one waveform versus the other.
	 */
	GAdditivePacket drive;

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm wA = waveA.genWave(s);
		WaveForm wB = waveB.genWave(s);
		NonClampedCoefficient coeff = drive.getCoefficient().genCoeff(s);
		NonClampedCoefficient coeffCoeff = drive.getCoefficientCoefficient().genCoeff(s);
		NonClampedCoefficient paramCoeff = drive.getParameterCoefficient().genCoeff(s);
		
		GranularSynth wv = new GranularSynth( wA , wB , coeff , coeffCoeff , paramCoeff );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Object[] ob = { waveA , waveB , drive };
		return( ob );
	}

	@Override
	public String getName() {
		return("Granular Synthesis");
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GAdditivePacket ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			if( waveA == null ) waveA = (GWaveForm) in; 
				else waveB = (GWaveForm) in;
		}
		if( in instanceof GAdditivePacket )
		{
			drive = (GAdditivePacket) in;
		}
	}

	@Override
	public void removeChld() {
		waveA = null;
		waveB = null;
		drive = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param wA First waveform from which to select grains.
	 * @param wB Second waveform from which to select grains.
	 * @param d Additive packet for selecting grains from one waveform versus the other.
	 */
	public void load( GWaveForm wA , GWaveForm wB , GAdditivePacket d )
	{
		waveA = wA;
		waveB = wB;
		drive = d;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( waveA != null ) myv.setProperty("WaveA", waveA);
		if( waveB != null ) myv.setProperty("WaveB", waveB);
		if( drive != null ) myv.setProperty("Drive", drive);

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
			waveB = (GWaveForm)( myv.getProperty("WaveB") );
			drive = (GAdditivePacket)( myv.getProperty("Drive") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

