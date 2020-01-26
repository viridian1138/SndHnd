




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



import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Calendar;
import java.util.Random;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import meta.DataFormatException;
import meta.VersionBuffer;
import java.io.*;



/**
 * Generates a normalized version of a PCM (Pulse-Code Modulation) input file.
 * 
 * This class was originally written for QTJ (QuickTime for Java) using
 * examples from the book "QuickTime for Java : A Developer's Notebook" by Chris Adamson, 
 * but has since been ported from QTJ to JavaSound.  Porting used geberal JavaSound examples from the web.
 * 
 * @author tgreen
 *
 */
public class SamplingNormalizer {
	
	/**
	 * Buffer size to use when getting the sample count from the input file.
	 */
	private final int EXTERNAL_BUFFER_SIZE = 512 * 1024;
	
	/**
	 * The number of bytes per sample of the output file.
	 */
	public static final int BYTES_PER_OUTPUT_SAMPLE = 4;
	
	/**
	 * The number of bits per sample of the output file.
	 */
	public static final int BITS_PER_OUTPUT_SAMPLE = 8 * BYTES_PER_OUTPUT_SAMPLE;
	
	/**
	 * Initial seed for random number generation.
	 */
	public static final int INITIAL_RANDSEED = 707;

	/**
	 * The sample rate of the PCM (Pulse-Code Modulation) input file in hertz.
	 */
	double sampRate;

	/**
	 * The input PCM (Pulse-Code Modulation) input file to be normalized.
	 */
	File inFile;

	/**
	 * The number of samples counted from the input file.
	 */
	int sCount;

	/**
	 * The size of each sample in bytes.
	 */
	int sampleSize = -1;

	/**
	 * Whether the input samples need to have their endians flipped.
	 */
	boolean endianFlip = false;
	
	/**
	 * The calculated value for normalizing the PCM (Pulse-Code Modulation) input file.
	 */
	protected double normalizingValue = 0.0;
	
	/**
	 * Random number generator for resolving fractions of an amplitude value.
	 */
	protected final Random rand = new Random();

	
	/**
	 * Constructor.
	 * @param _inFile The input PCM (Pulse-Code Modulation) input file to be normalized.
	 */
	public SamplingNormalizer( File _inFile ) {
		super();
		try {
			inFile = _inFile;
			
			AudioInputStream ain = AudioSystem.getAudioInputStream( inFile );
            try {
                
    			
    			AudioFormat format = ain.getFormat();
    			
    			sampRate = format.getSampleRate();
    			endianFlip = !( format.isBigEndian() );
    			sampleSize = format.getSampleSizeInBits() / 8;
    			
    			int nBytesRead = 0;
    			byte[] abData = new byte[ EXTERNAL_BUFFER_SIZE ];
    			int totBytesRead = 0;
    			
    			while( nBytesRead !=-1 )
    			{
    				nBytesRead = ain.read(abData, 0, abData.length);
    				if( nBytesRead > 0 )
    				{
    					totBytesRead += nBytesRead;
    				}
    			}
    			
    			final int sSizeBits = format.getSampleSizeInBits();
    			
    			sCount = ( totBytesRead * 8 ) / ( sSizeBits );
    			
                
            }
            finally { // We're done with the input stream.
                ain.close( );
            }
			
		} catch (Throwable ex) {
			ex.printStackTrace(System.out);
			throw (new RuntimeException("Failed."));
		}
	}

	
	/**
	 * Gets the PCM (Pulse-Code Modulation) value from a one-sample set of bytes.
	 * @param bytes A one-sample set of bytes.
	 * @return The PCM (Pulse-Code Modulation) value from a one-sample set of bytes.
	 */
	protected double getValue( byte[] bytes ) {
			double rval = 0.0;
			final int index = 0;
			switch (sampleSize) {
			case 2:
			{
					int i2 = bytes[index] & 0xff;
					int i3 = bytes[index + 1] & 0xff;
					int val = ((i2 << 16) + (i3 << 24));
					rval = ((double) val) / (0x7fffffff);
			}
				break;
			case 4:
				if (endianFlip) {
						int i0 = bytes[index + 3] & 0xff;
						int i1 = bytes[index + 2] & 0xff;
						int i2 = bytes[index + 1] & 0xff;
						int i3 = bytes[index] & 0xff;
						int val = (i0 + (i1 << 8) + (i2 << 16) + (i3 << 24));
						rval = ((double) val) / (0x7fffffff);
				} else {
						int i0 = bytes[index] & 0xff;
						int i1 = bytes[index + 1] & 0xff;
						int i2 = bytes[index + 2] & 0xff;
						int i3 = bytes[index + 3] & 0xff;
						int val = (i0 + (i1 << 8) + (i2 << 16) + (i3 << 24));
						rval = ((double) val) / (0x7fffffff);
				}
				break;
			}

			return( rval );
	}
	
	
	
	/**
	 * Temporary array used in the eval() method.
	 */
	final byte[] tmp = new byte[ 4 ];

	
	/**
	 * Gets the next PCM (Pulse-Code Modulation) value from the input stream.
	 * @param bin The input stream.
	 * @return The next PCM (Pulse-Code Modulation) value from the input stream.
	 * @throws Throwable
	 */
	protected double eval( BufferedInputStream bin ) throws Throwable {
		int bytesRead = 0;
		while( bytesRead < sampleSize )
		{
			int v = bin.read( tmp , bytesRead , sampleSize - bytesRead );
			if( v < 0 )
			{
				throw( new RuntimeException( "Fail" ) );
			}
			bytesRead += v;
		}
		
		return( getValue( tmp ) );
	}
	
	
	/**
	 * Reads the input audio file and calculates the normalizing value.
	 * @return The normalizing value.
	 * @throws Throwable
	 */
	public double calcNormalizingValue() throws Throwable
	{
		AudioInputStream ain = AudioSystem.getAudioInputStream( inFile );
		BufferedInputStream bin = new BufferedInputStream( ain );
		
		double ret = 0.0;
		int count;
		for( count = 0 ; count < sCount ; count++ )
		{
			ret = Math.max( ret , Math.abs( eval( bin ) ) );
		}
		if( ret < 0.0000001 )
		{
			ret = 0.0000001;
		}
		normalizingValue = ret;
		
		bin.close();
		
		System.out.println( ret );
		return( ret );
	}
	
	
	/**
	 * Gets the normalized version of the next PCM (Pulse-Code Modulation) value from the input stream.
	 * @param bin The input stream.
	 * @return The normalized version of the next PCM (Pulse-Code Modulation) value from the input stream.
	 * @throws Throwable
	 */
	public double calcNormalized( BufferedInputStream bin ) throws Throwable 
	{
		return( eval( bin ) / normalizingValue );
	}
	
	
	/**
	 * Gets the normalized version of the next chunk of PCM (Pulse-Code Modulation) values from the input stream.
	 * @param inTime The current sample number in the stream.
	 * @param sampling The upper limit for the number of samples to normalize.
	 * @param bin The input stream.
	 * @return The normalized version of the next chunk of PCM (Pulse-Code Modulation) values from the input stream.
	 * @throws Throwable
	 */
	public byte[] buildNormalizedSample(int inTime, int sampling, BufferedInputStream bin ) throws Throwable {
		final int sampNum = Math.min( sCount - inTime , sampling );
		final int len = BYTES_PER_OUTPUT_SAMPLE * sampNum;
		final byte[] sample = new byte[ len ];

		int i;
		for (i = 0; i < len; i += BYTES_PER_OUTPUT_SAMPLE ) {

			double sampleVal = calcNormalized( bin );

			// double heightD = ( sampleVal ) * (0x7fffffff / 2);
			double heightD = (sampleVal) * (0x7fffffff);
			long heightLng = (long) heightD;
			double htRng = heightD - heightLng;
			htRng = Math.max(htRng, 0.0);
			htRng = Math.min(htRng, 1.0);
			long height = heightLng;
			if (htRng > rand.nextDouble()) {
				height = height + 1;
			}

			sample[i] = (byte) ((height & 0xff000000) >> 24);
			sample[i + 1] = (byte) ((height & 0xff0000) >> 16);
			sample[i + 2] = (byte) ((height & 0xff00) >> 8);
			sample[i + 3] = (byte) (height & 0xff);
		}

		return ( sample );
	}
	
	
	/**
	 * Generates a normalized version of the sampled file.
	 * @param fi The output file to which to write the normalized audio.
	 * @return The file to which the normalized audio was written.
	 * @throws Throwable
	 */
	public File generateSound( File fi ) throws Throwable {

		System.out.println("Created Movie.");
		
		final int sampling = (int) sampRate;

		System.out
				.println("Data Set Constructed.  Started Sampling Waveforms.");

		System.out.println("Started " + (Calendar.getInstance()));

		System.out.println(SongData.instrumentTracks.size());
		InstrumentTrack itrack = SongData.instrumentTracks.get(0);
		System.out.println(itrack.getTrackFrames().size());

		rand.setSeed(INITIAL_RANDSEED);
		
		calcNormalizingValue();
		
		final AudioInputStream ain = AudioSystem.getAudioInputStream( inFile );
		
		final BufferedInputStream bin = new BufferedInputStream( ain );

		PcmContentReader preader = new PcmContentReader()
		{
			/**
			 * Reads a one-second frame of PCM (Pulse Code Modulation) audio data.
			 * @param frameNumber The number of the one-second frame from which to get the data.
			 * @return The bytes in the one-second frame.
			 * @throws Throwable
			 */
			public byte[] handleRead( int frameNumber ) throws Throwable
			{
				int i = frameNumber * sampling;
				return( buildNormalizedSample( i , sampling , bin ) );
			}
		};
		
		final PcmInputStream ti = new PcmInputStream( preader , 
				sampling , BITS_PER_OUTPUT_SAMPLE / 8 , 1 , true , true , sCount /sampling );
		
		System.out.println( ti.getAudioFormat() );
		
		final AudioInputStream stream = new AudioInputStream( ti , ti.getAudioFormat() , ti.getStreamSize() );
		
		AudioSystem.write( stream , 
				AudioFileFormat.Type.WAVE , new File( "/home/tgreen/out.wav" ) ); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
		
		bin.close();

		System.out.println("Done.");

		return ( fi );
	}


}

