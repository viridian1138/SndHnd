




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


import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;


/**
 * Streams PCM (Pulse Code Modulation) audio bytes from a PcmContentReader.
 * 
 * @author tgreen
 *
 */
public class PcmInputStream extends InputStream {
	
	/**
	 * The sample rate (in hertz) of the PCM (Pulse Code Modulation) audio.
	 */
	int sampleRate;
	
	/**
	 * The number of bytes per sample of the PCM (Pulse Code Modulation) audio.
	 */
	int bytesPerSample;
	
	/**
	 * The number of one-second frames in the input PCM (Pulse Code Modulation) data.
	 */
	int numFrames;
	
	
	/**
	 * The size of a one-second frame in bytes.
	 */
	int frameSize;
	
	/**
	 * The JavaSound audio format of the PCM (Pulse Code Modulation) audio data.
	 */
	AudioFormat af;
	
	/**
	 * The current position within the current one-second frame in bytes.
	 */
	int positionInFrame = 0;
	
	/**
	 * The number of the current one-second frame.
	 */
	int frameNumber = -1;
	
	/**
	 * The byte content of the current one-second frame.
	 */
	byte[] curContent = null;
	
	
	/**
	 * The reader from which to acquire the PCM (Pulse Code Modulation) audio data.
	 */
	PcmContentReader reader;
	
	
	@Override
	public int read( byte[] b , int off , int len ) throws IOException
	{

		if( len == 0 )
		{
			return( 0 );
		}
		
		if( curContent == null )
		{
			frameNumber++;
			if( frameNumber >= numFrames )
			{
				return( -1 );
			}
			
			try
			{
				curContent = handleRead( frameNumber );
				if( curContent == null )
				{
					return( -1 );
				}
			}
			catch( Throwable ex )
			{
				throw( new IOException( ex ) );
			}
			positionInFrame = 0;
		}
		
		int ret = Math.min( len , frameSize - positionInFrame );
		
		System.arraycopy( curContent , positionInFrame , b , off , ret );
		
		positionInFrame += ret;
		
		if( positionInFrame >= frameSize )
		{
			curContent = null;
		}
		
		return( ret );
		
	}

	
	/**
	 * Constructor.
	 * @param _reader The reader from which to acquire the PCM (Pulse Code Modulation) audio data.
	 * @param _sampleRate The sample rate (in hertz) of the PCM (Pulse Code Modulation) audio.
	 * @param _bytesPerSample The number of bytes per sample of the PCM (Pulse Code Modulation) audio.
	 * @param _numChannels The number of channels of PCM (Pulse Code Modulation) audio data in each sample.
	 * @param _signed Whether the PCM (Pulse Code Modulation) audio data in each channel is signed.
	 * @param _bigEndian Whether the PCM (Pulse Code Modulation) audio data in each channel is big-endian (as opposed to little-endian).
	 * @param _numFrames The number of one-second frames in the input PCM (Pulse Code Modulation) data.
	 */
	public PcmInputStream( PcmContentReader _reader ,
			int _sampleRate , int _bytesPerSample ,
			int _numChannels , boolean _signed , boolean _bigEndian , int _numFrames ) {
		reader = _reader;
		sampleRate = _sampleRate;
		bytesPerSample = _bytesPerSample;
		numFrames = _numFrames;
		af = new AudioFormat( 
				sampleRate , 8 * bytesPerSample , 
				_numChannels , _signed , _bigEndian );
		frameSize = sampleRate * bytesPerSample;
	}

	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		byte[] b = new byte[ 1 ];
		int v = read( b );
		while( v == 0 )
				v = read( b );
		if( v < 0 )
				return( v );
		if( b[ 0 ] < 0 ) return( 256 + b[ 0 ] );
		return( b[ 0 ] );
	}
	
	
	@Override
	public int available() {
		return( frameSize - positionInFrame );
	}
	
	
	/**
	 * Gets the JavaSound audio format of the PCM (Pulse Code Modulation) audio data.
	 * @return The JavaSound audio format of the PCM (Pulse Code Modulation) audio data.
	 */
	public AudioFormat getAudioFormat()
	{
		return( af );
	}
	
	
	/**
	 * Gets the number of one-second frames in the input PCM (Pulse Code Modulation) data.
	 * @return The number of one-second frames in the input PCM (Pulse Code Modulation) data.
	 */
	public int getNumFrames()
	{
		return( numFrames );
	}
	
	
	/**
	 * Gets the overall size of the stream in bytes as the number of frames times the frame size.
	 * @return The overall size of the stream in bytes as the number of frames times the frame size.
	 */
	public int getStreamSize()
	{
		return( numFrames * frameSize );
	}
	

	/**
	 * Reads a one-second frame of PCM (Pulse Code Modulation) audio data from the reader.
	 * @param frameNumber The number of the one-second frame from which to get the data.
	 * @return The bytes in the one-second frame.
	 * @throws Throwable
	 */
	protected byte[] handleRead( int frameNumber ) throws Throwable
	{
		return( reader.handleRead(frameNumber) );
	}
	
	

}

