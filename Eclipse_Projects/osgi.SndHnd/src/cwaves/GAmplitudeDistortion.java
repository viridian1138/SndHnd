




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

import gredit.GClampedCoefficient;
import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.ClampedCoefficient;
import core.WaveForm;

/**
 * 
 * Node representing a waveform performing amplitude modulation synthesis upon an input wave.
 * 
 * See: https://www.soundonsound.com/techniques/amplitude-modulation
 * 
 * The modulating input can also amplify the level of the input wave for implementations of distortion and/or overdriving.
 * 
 * See: https://en.wikipedia.org/wiki/Distortion_(music)
 * 
 * The primary difference between this and class GAmplitudeModulationWaveform is that the modulation is clamped.  Distortion systems tend to have some clamp (often a clamp that produces some form of clipping) on the end of the normal amplification range.
 * 
 * @author thorngreen
 */
public class GAmplitudeDistortion extends GWaveForm implements Externalizable {
	
	
	/**
	 * The input waveform.
	 */
	GWaveForm wave;
	
	/**
	 * The drive (amplification) function that modulates the input waveform.
	 */
	GClampedCoefficient drive;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		ClampedCoefficient c = drive.genClamped(s);
		
		AmplitudeDistortion wv = new AmplitudeDistortion( w , c );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Object[] ob = { wave , drive };
		return( ob );
	}

	@Override
	public String getName() {
		return("AmplitudeDistortion");
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GClampedCoefficient ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			wave = (GWaveForm) in;
		}
		if( in instanceof GClampedCoefficient )
		{
			drive = (GClampedCoefficient) in;
		}
	}

	@Override
	public void removeChld() {
		wave = null;
		drive = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param w The input waveform.
	 * @param d The drive (amplification) function that modulates the input waveform.
	 */
	public void load( GWaveForm w , GClampedCoefficient d )
	{
		wave = w;
		drive = d;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
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

			wave = (GWaveForm)( myv.getProperty("Wave") );
			drive = (GClampedCoefficient)( myv.getProperty("Drive") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

