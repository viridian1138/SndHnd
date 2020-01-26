




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







package core;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.TreeMap;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFileChooser;


/**
 * Data state for the digitization (aka live recording) of audio.
 * Only one stream is recorded at a time.
 * 
 * @author tgreen
 *
 */
public class DigitizerData {
	
	/**
	 * Incrementing count used to ensure that each backing file for digitized audio has a unique name.
	 */
	protected static int digitizerCount = 0;

	/**
	 * The JavaSound audio format for the input audio stream.
	 */
	public static AudioFormat af;

	/**
	 * The reader used to assimilate PCM data from the input audio stream.
	 */
	public static PcmContentReader preader;
	
	/**
	 * The JavaSound data line for the input audio stream.
	 */
	public static TargetDataLine line;

	/**
	 * Whether audio is currently being grabbed from the input stream.
	 */
	public static boolean grabbing = true;

	/**
	 * Whether the audio digitization has been initialized.
	 */
	public static boolean initialized = false;
	
	/**
	 * The start time of the audio digitization (with respect to the elapsed time of the overall mix) in seconds.
	 */
	public static double startTimeSeconds = 0.0;
	
	/**
	 * The end time of the audio digitization (with respect to the elapsed time of the overall mix) in seconds.
	 */
	public static double endTimeSeconds = 0.0;

	
	/**
	 * Performs setup on the audio digitization stream.
	 * Configures the preader member so that data can be read.
	 * @throws Throwable
	 */
	public static void setUpAudioGrab( ) throws Throwable {
		if (!initialized) {
			final Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
			
			Mixer mixer = null;
			
			for( int cnt = 0 ; cnt < mixerInfo.length ; cnt++ )
			{
				String str = " " + ( mixerInfo[ cnt ] );
				System.out.println( str );
				//if( str.contains( "plughw:0,0" ) )
				//{
				//	mixer = AudioSystem.getMixer( mixerInfo[ cnt ] );
				//}
				mixer = AudioSystem.getMixer( mixerInfo[ 1 ] );
			}
			
		final Line.Info[] infos = mixer.getTargetLineInfo();
		
		for( int cnt = 0 ; cnt < infos.length ; cnt++ )
		{
			String str = " " + ( infos[ cnt ] );
			System.out.println( str );
		}
			
		line = (TargetDataLine)( mixer.getLine( infos[ 0 ] ) );
		
		af = line.getFormat();
		
		final int sampleSize = af.getSampleSizeInBits() / 8;
		
		final int sampleRate = (int)( af.getSampleRate() );
		
		final int slen = sampleRate * sampleSize;
		
		final byte[] buffer = new byte[ slen ];
		
		preader = new PcmContentReader()
		{
			/**
			 * Handles the reading of PCM data from the line.
			 * @param frameNumber The frame number of the audio grab.
			 * @return The data bytes read from the line.
			 */
			public byte[] handleRead( int frameNumber )
			{
				int nBytesRead = 0;
				while( nBytesRead < slen )
				{
					int v = line.read( buffer , nBytesRead , slen - nBytesRead );
					if( v < 0 )
					{
						throw( new RuntimeException( "Fail" ) );
					}
					nBytesRead += v;
				}
				return( buffer );
			}
		};
		
		
		}
		
	}
	
	
	/**
	 * Gets the next unique audio file into which to store the digitized audio.
	 * @return The next unique file.
	 */
	public static File getDigiFile()
	{
		File fi = new File( "digi" + ( digitizerCount ) + ".wav" );
		digitizerCount++;
		return( fi );
	}
	
	
	/**
	 * Builds a SampledAgent from the last digitized recording, assuming that digitization has previously completed.
	 * @return The generated SampledAgent.
	 * @throws Throwable
	 */
	public static IntelligentAgent buildSampledAgent() throws Throwable
	{
		File fi = new File( "digi" + ( digitizerCount - 1 ) + ".wav" );
			
		SamplingNormalizer normalizer = new SamplingNormalizer( fi );
		
		File fi2 = null;
		
		while( fi2 == null )
		{
			Frame fr = null;
		
			FileDialog fd = new FileDialog( fr , "Save", FileDialog.SAVE);
		
			fd.show();
		
			if( fd.getFile() != null )
			{
				fi2 = new File( fd.getFile() );
			}
			
		}

		normalizer.generateSound( fi2 );
		
		TreeMap<String, Class<? extends IntelligentAgent>> map = AgentManager.getMapClone();
		Class<? extends IntelligentAgent> clss = map.get( "Sampled Agent" );
		if( clss == null )
		{
			throw( new RuntimeException( "Fail." ) );
		}
		Class[] cclass = { File.class };
		Constructor<? extends IntelligentAgent> cns = clss.getConstructor( cclass );
		Object[] param = { fi2 };
		IntelligentAgent samp = cns.newInstance( param );
		samp.setHname( cns.getName() );
		
		return( samp );
	}
	
	
	

}

