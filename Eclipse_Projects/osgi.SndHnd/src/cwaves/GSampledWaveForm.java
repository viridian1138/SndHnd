




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

import gredit.GZWaveBase;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import verdantium.ProgramDirector;

import meta.VersionBuffer;
import core.InstrumentTrack;
import core.WaveForm;

/**
 * Node representing a waveform returning sampled audio data from a sound file.  Note: this isn't a true waveform. It's set arbitrarily at 1hz.
 * 
 * @author thorngreen
 * 
 */
public class GSampledWaveForm extends GZWaveBase implements Externalizable {
	
	/**
	 * The audio file sampled by the waveform.
	 */
	File soundFile;

	@Override
	public WaveForm genWave(HashMap s) {
		try
		{
			if( s.get(this) != null )
			{
				return( (WaveForm)( s.get(this) ) );
			}
		
			s.put(this, new Integer(5));
		
			SampledWaveform wv = new SampledWaveform( soundFile );
			s.put(this, wv);
		
			return( wv );
		}
		catch( Throwable ex )
		{
			throw( new RuntimeException( ex ) );
		}
	}

	@Override
	public String getName() {
		return( "Sampled" );
	}
	
	/**
	 * Loads new values into the node.
	 * @param _soundFile The audio file sampled by the waveform.
	 */
	public void load( File _soundFile )
	{
		soundFile = _soundFile;
	}
	
	
	
	/**
	 * Gets the audio file sampled by the waveform.
	 * @return The audio file sampled by the waveform.
	 */
	public File getSoundFile() {
		return soundFile;
	}

	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		SampledWaveformEditor editor = new SampledWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Sampled Waveform Properties");
	}
	
	@Override
	public void writeExternal(ObjectOutput in) throws IOException { // Note: persistence doesn't function.  The only class that can persist a use of sampling is agents.SampledAgent.
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		throw( new RuntimeException( "Not Supported" ) ); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, // Note: persistence doesn't function.  The only class that can persist a use of sampling is agents.SampledAgent.
			ClassNotFoundException {		
		VersionBuffer myv = (VersionBuffer) (in.readObject());
		VersionBuffer.chkNul(myv);
		
		throw( new RuntimeException( "Not Supported" ) ); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

}

