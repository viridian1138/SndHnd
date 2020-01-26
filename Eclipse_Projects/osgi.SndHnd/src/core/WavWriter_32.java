




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



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;



/**
 * Simple class for writing 32-bit .wav files in uncompressed PCM.
 * 
 * @author thorngreen
 *
 */
public class WavWriter_32 {
	
	
	/**
	 * The current position within the output file.
	 */
	protected long curPosition = 0;
	
	
	
	/**
	 * Writer ensuring that the current position is updated.
	 * @author tgreen
	 *
	 */
	protected class PosWriter
	{
		
		/**
		 * Writes the specified byte to this buffered output stream.
		 * @param val the byte to be written.
		 * @throws IOException if an I/O error occurs.
		 */
		public void write( int val ) throws IOException
		{
			bosa.write( val );
			curPosition++;
		}
		
		/**
		 * Writes bytes.length bytes to this output stream.
		 * @param bytes the data to be written.
		 * @throws IOException if an I/O error occurs.
		 */
		public void write( byte[] bytes ) throws IOException
		{
			final int ilen = bytes.length;
			bosa.write( bytes );
			curPosition += ilen;
		}
		
		/**
		 * Closes the output stream.
		 * @throws IOException
		 */
		public void close() throws IOException
		{
			bosa.close();
		}
		
	}
	
	
	/**
	 * Writer ensuring that the current position is updated.
	 */
	protected final PosWriter bos = new PosWriter();
	
	
	/**
	 * Mask defining a single byte.
	 */
	protected static final int BYTE_MASK = 0xff;
	
	
	/**
	 * The stream to which to write to the output file.
	 */
	protected BufferedOutputStream bosa;
	
	
	/**
	 * The path to the output file to be written.
	 */
	protected File fi;
	
	
	/**
	 * The sample rate (in hertz) to be put into the file.
	 */
	protected int sampleRate = 96000;
	
	
	/**
	 * The number of bits per sample to be put in the file.
	 */
	protected int bitsPerSample = 24;
	
	
	/**
	 * The number of channels to be put in the file.
	 */
	protected int numberOfChannels = 1;
	
	
	/**
	 * The position relative to the overall file at which to store the final sizing information.
	 */
	protected long fullPosition = -1;
	
	
	/**
	 * The position at which to write the final sizing information into the data chunk header.
	 */
	protected long chunkSizePosition = -1;
	
	
	
	
	/**
	 * Sets the sample rate (in hertz) to be put into the file.
	 * @param _sampleRate The sample rate (in hertz) to be put into the file.
	 */
	public void setSampleRate( int _sampleRate )
	{
		sampleRate = _sampleRate;
	}
	
	
	/**
	 * Sets the number of channels to be put in the file.
	 * @param _numberOfChannels The number of channels to be put in the file.
	 */
	public void setNumberOfChannels( int _numberOfChannels )
	{
		numberOfChannels = _numberOfChannels;
	}
	
	
	/**
	 * Sets the number of bits per sample to be put in the file.
	 * @param _bitsPerSample The number of bits per sample to be put in the file.
	 */
	public void setBitsPerSample( int _bitsPerSample )
	{
		bitsPerSample = _bitsPerSample;
	}
	
	
	
	
	/**
	 * Constructor.
	 * @param _fi The path to the output file to be written.
	 * @throws IOException
	 */
	public WavWriter_32( File _fi ) throws IOException
	{
		bosa = new BufferedOutputStream( new FileOutputStream( _fi ) );
		fi = _fi;
	}
	
	
	/**
	 * Writes the initial header data to the file.
	 * @throws IOException
	 */
	public void start() throws IOException
	{
		
		bos.write( 'R' );
		bos.write( 'I' );
		bos.write( 'F' );
		bos.write( 'F' );
		
		
		fullPosition = curPosition;
		
		
		bos.write( 8 ); // Little endian file size - 8.
		bos.write( 0 );
		bos.write( 0 );
		bos.write( 0 );
		
		
		bos.write( 'W' );
		bos.write( 'A' );
		bos.write( 'V' );
		bos.write( 'E' );
		
		
		writeFormat();
		
		writeDataChunkHeader();
		
	}
	
	
	
	
	/**
	 * Writes bytes.length bytes to this output stream.
	 * @param bytes the data to be written.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write( byte[] bytes ) throws IOException
	{
		bos.write( bytes );
	}
	
	
	
	
	/**
	 * Writes the specified byte to this buffered output stream.
	 * @param val the byte to be written.
	 * @throws IOException if an I/O error occurs.
	 */
	public void write( int byt ) throws IOException
	{
		bos.write( byt );
	}
	
	
	
	
	/**
	 * Closes the output file and writes the final sizing information into the header.
	 * @throws IOException
	 */
	public void close( ) throws IOException
	{
		
		final long chunkStartPosition = chunkSizePosition + 4;
		
		final long chunkSize = curPosition - chunkStartPosition;
		
		
		// Make sure that chunks are word-aligned.
		if( ( chunkSize % 2 ) != 0 )
		{
			bos.write( 0 );
		}
		
		
		bos.close( );
		
		bosa = null;
		
		
		
		final long lenMinusEight = curPosition - 8;
		
		if( lenMinusEight > ( ( 1L << 32 ) - 1 ) )
		{
			throw( new RuntimeException( "Too Large For Wav 32.  Perhaps switch to Wav 64." ) );
		}
		
		
		final RandomAccessFile ra = new RandomAccessFile( fi, "rw" );
		
		
		ra.seek( fullPosition );
		ra.write( (int)( lenMinusEight & BYTE_MASK ) );
		ra.write( (int)( ( lenMinusEight >> 8 ) & BYTE_MASK ) );
		ra.write( (int)( ( lenMinusEight >> 16 ) & BYTE_MASK ) );
		ra.write( (int)( ( lenMinusEight >> 24 ) & BYTE_MASK ) );
		
		
		ra.seek( chunkSizePosition );
		ra.write( (int)( chunkSize & BYTE_MASK ) );
		ra.write( (int)( ( chunkSize >> 8 ) & BYTE_MASK ) );
		ra.write( (int)( ( chunkSize >> 16 ) & BYTE_MASK ) );
		ra.write( (int)( ( chunkSize >> 24 ) & BYTE_MASK ) );
		
		
		ra.close();
		
	}
	
	
	
	
	/**
	 * Writes the format header to the file.
	 * @throws IOException
	 */
	protected void writeFormat() throws IOException
	{
		bos.write( 'f' );
		bos.write( 'm' );
		bos.write( 't' );
		bos.write( ' ' );
		
		
		// Little endian chunk size.
		bos.write( 16 ); 
		bos.write( 0 );
		bos.write( 0 );
		bos.write( 0 );
		
		
		// Little endian compression code = PCM.
		bos.write( 1 ); 
		bos.write( 0 );
		
		
		// Little endian number of channels
		bos.write( numberOfChannels & BYTE_MASK );
		bos.write( ( numberOfChannels >> 8 ) & BYTE_MASK );
		
		
		// Little endian sample rate
		bos.write( sampleRate & BYTE_MASK );
		bos.write( ( sampleRate >> 8 ) & BYTE_MASK );
		bos.write( ( sampleRate >> 16 ) & BYTE_MASK );
		bos.write( ( sampleRate >> 24 ) & BYTE_MASK );
		
		
		// Average bytes per second
		final int blockAlign = ( bitsPerSample / 8 ) * numberOfChannels;
		final int avgBytesPerSec = sampleRate * blockAlign;
		bos.write( avgBytesPerSec & BYTE_MASK );
		bos.write( ( avgBytesPerSec >> 8 ) & BYTE_MASK );
		bos.write( ( avgBytesPerSec >> 16 ) & BYTE_MASK );
		bos.write( ( avgBytesPerSec >> 24 ) & BYTE_MASK );
		
		
		// Block align
		bos.write( blockAlign & BYTE_MASK );
		bos.write( ( blockAlign >> 8 ) & BYTE_MASK );
		
		
		// Significant bits per sample
		bos.write( bitsPerSample & BYTE_MASK );
		bos.write( ( bitsPerSample >> 8 ) & BYTE_MASK );
		
		
		
	}
	
	
	
	/**
	 * Writes the data chunk header to the file.
	 * @throws IOException
	 */
	protected void writeDataChunkHeader() throws IOException
	{
		bos.write( 'd' );
		bos.write( 'a' );
		bos.write( 't' );
		bos.write( 'a' );
		
		
		chunkSizePosition = curPosition;
		
		
		bos.write( 8 ); // Little endian chunk size.
		bos.write( 0 );
		bos.write( 0 );
		bos.write( 0 );
		
		
	}
	
	
	
	

}




