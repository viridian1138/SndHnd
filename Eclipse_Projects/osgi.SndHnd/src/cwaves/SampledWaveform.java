




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
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;

import java.io.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * Waveform returning sampled audio data from a sound file.  Note: this isn't a true waveform. It's set arbitrarily at 1hz.
 * 
 * @author thorngreen
 * 
 */
public class SampledWaveform extends WaveForm implements Externalizable {
	
	/**
	 * Size of cache buffer used to determine the number of audio samples in the file.
	 */
	private final int EXTERNAL_BUFFER_SIZE = 512 * 1024;

	/**
	 * The sample rate of the file.
	 */
	double sampRate;

	/**
	 * Sound file to be sampled.
	 */
	File soundFile;

	/**
	 * The number of audio samples in the file.
	 */
	int sCount;

	/**
	 * The sample size in the file in bytes.
	 */
	int sampleSize = -1;
	
	/**
	 * The number of audio channels in the file.
	 */
	int numChannels = -1;

	/**
	 * whether the endian of the byte format is flipped.
	 */
	boolean endianFlip = false;
	
	
	/**
	 * SampledVirtual used to sample bytes from the audio file.
	 */
	SampledVirtual sv = null;
	

	/**
	 * Constructs the waveform.
	 * @param _soundFile Input sound file.
	 * @throws Throwable
	 */
	public SampledWaveform( File _soundFile ) throws Throwable {
		super();
		soundFile = _soundFile;
		sv = new SampledVirtual( soundFile );
		
		AudioInputStream ain = AudioSystem.getAudioInputStream( soundFile );
        try {
            
			
			AudioFormat format = ain.getFormat();
			
			sampRate = format.getSampleRate();
			endianFlip = !(!( format.isBigEndian() ));
			sampleSize = format.getSampleSizeInBits() / 8;
			numChannels = format.getChannels();
			
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
			
			final int numChan = format.getChannels();
			
			sCount = ( totBytesRead * 8 ) / ( sSizeBits * numChan );
			
			System.out.println( "*** " + sCount );
			
            
        }
        finally { // We're done with the input stream.
            ain.close( );
        }
		
	}
	
	/**
	 * Constructs the waveform.  Used for internal purposes only.  Primarily used by genClone().
	 * @param in An input SampledWaveform to clone.
	 * @throws Throwable
	 */
	private SampledWaveform( SampledWaveform in ) throws Throwable
	{
		this( in.soundFile );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		return( new SampledWaveform( this ) );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GSampledWaveForm wv = new GSampledWaveForm();
		wv.load(soundFile);
		s.put(this, wv);
		return( wv );
	}
	
	/**
	 * 
	 * Constructs the waveform.  For use by serial storage shell only.
	 *
	 */
	public SampledWaveform()
	{
		// Does nothing.
	}

	/**
	 * Gets the sample value from the audio file at a particular sample index.
	 * @param index The sample index.
	 * @return The sample value from the audio file at the sample index, normalized to [-1.0, 1.0]
	 */
	protected double getValue( int index ) {
		int maxSamples = 0;
		try {
			
			double ret = 0.0;
			switch (sampleSize) {
			case 2:
				{
//					int i2 = sv.get(index) & 0xff;
//					int i3 = sv.get(index + 1) & 0xff;
//					int val = ((i2 << 16) + (i3 << 24));
//					ret = ((double) val) / (0x7fffffff);
					if (endianFlip) {
						{
							int i2 = sv.get(index + 1) & 0xff;
							int i3 = sv.get(index) & 0xff;
							int val = ((i2 << 16) + (i3 << 24));
							ret = ((double) val) / (0x7fffffff);
						}
					} else {
						{
							int i2 = sv.get(index) & 0xff;
							int i3 = sv.get(index + 1) & 0xff;
							int val = ((i2 << 16) + (i3 << 24));
							ret = ((double) val) / (0x7fffffff);
						}
					}
				}
				break;
			case 3:
				if (endianFlip) {
					{
						int i1 = sv.get(index + 2) & 0xff;
						int i2 = sv.get(index + 1) & 0xff;
						int i3 = sv.get(index) & 0xff;
						int val = ((i1 << 8) + (i2 << 16) + (i3 << 24));
						ret = ((double) val) / (0x7fffffff);
					}
				} else {
					{
						int i1 = sv.get(index) & 0xff;
						int i2 = sv.get(index + 1) & 0xff;
						int i3 = sv.get(index + 2) & 0xff;
						int val = ((i1 << 8) + (i2 << 16) + (i3 << 24));
						ret = ((double) val) / (0x7fffffff);
					}
				}
				break;
			case 4:
				if (endianFlip) {
					{
						int i0 = sv.get(index + 3) & 0xff;
						int i1 = sv.get(index + 2) & 0xff;
						int i2 = sv.get(index + 1) & 0xff;
						int i3 = sv.get(index) & 0xff;
						int val = (i0 + (i1 << 8) + (i2 << 16) + (i3 << 24));
						ret = ((double) val) / (0x7fffffff);
					}
				} else {
					{
						int i0 = sv.get(index) & 0xff;
						int i1 = sv.get(index + 1) & 0xff;
						int i2 = sv.get(index + 2) & 0xff;
						int i3 = sv.get(index + 3) & 0xff;
						int val = (i0 + (i1 << 8) + (i2 << 16) + (i3 << 24));
						ret = ((double) val) / (0x7fffffff);
					}
				}
				break;
			}

			return ( ret );
		} catch (Throwable ex) {
			ex.printStackTrace( System.out );
			// System.out.println( "*** " + rIndex );
			// System.out.println( maxSamples );
			return( 0.0 );
		}
	}

	@Override
	public double eval(double param) {
		int rSampNum = (int) (param * sampRate);

		if ((rSampNum < 0) || (rSampNum >= sCount)) {
			return (0.0);
		}

		int sampleIndex = rSampNum * ( sampleSize * numChannels );

		return( getValue( sampleIndex ) );
	}

	/*
	 * public double eval( double param ) { try { int sampleNumber = (int)(
	 * param * sampRate ) + 1; if( ( sampleNumber < 1 ) || ( sampleNumber >
	 * sCount ) ) { return( 0.0 ); } TimeInfo timeo =
	 * smedia.sampleNumToMediaTime(sampleNumber); // Num 0 is invalid. int
	 * timeval = timeo.time; if( sampleSize < 0 ) { SampleInfo sample =
	 * smedia.getSampleReference(timeval,1); sampleSize = sample.size; }
	 * MediaSample ms = smedia.getSample(sampleSize,timeval,1); QTHandle sdata =
	 * ms.data; byte[] bytes = sdata.getBytes(); int len = bytes.length; double
	 * dval = 0.0; switch( len ) { case 2: int i2 = bytes[ 0 ] & 0xff; int i3 =
	 * bytes[ 1 ] & 0xff; int val = ( ( i2 << 16 ) + ( i3 << 24 ) ); dval = (
	 * (double) val ) / ( 0x7fffffff ); break; case 4: int i0 = bytes[ 0 ] &
	 * 0xff; int i1 = bytes[ 1 ] & 0xff; i2 = bytes[ 2 ] & 0xff; i3 = bytes[ 3 ] &
	 * 0xff; val = ( i0 + ( i1 << 8 ) + ( i2 << 16 ) + ( i3 << 24 ) ); dval = (
	 * (double) val ) / ( 0x7fffffff ); break; }
	 * 
	 * return( dval ); } catch( QTException ex ) { ex.printStackTrace(
	 * System.out ); throw( new RuntimeException( "Failed." ) ); } }
	 */

	@Override
	public boolean useAutocorrelation() {
		return (true);
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			// Only repersist as a shell and let the agent handle things.
			
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		// Only repersist as a shell and let the agent handle things.

		out.writeObject(myv);
	}

}
