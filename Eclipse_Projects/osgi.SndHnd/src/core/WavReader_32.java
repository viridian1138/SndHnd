




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
 * Simple class for reading 32-bit .wav files in uncompressed PCM (Pulse-Code Modulation).
 * 
 * @author thorngreen
 *
 */
public class WavReader_32 {
	
	
	/**
	 * Mask defining a single byte.
	 */
	protected static final int BYTE_MASK = 0xff;
	
	/**
	 * RandomAccessFile opened to read the binary data.
	 */
	protected RandomAccessFile bis;
	
	/**
	 * Path to the WAV file to be read.
	 */
	protected File fi;
	
	/**
	 * The sample rate (in hertz) read from the WAV file's header.
	 */
	protected int sampleRate = -1;
	
	/**
	 * The number of bits per sample read from the WAV file's header.
	 */
	protected int bitsPerSample = -1;
	
	/**
	 * The number of bytes per sample read from the WAV file's header.
	 */
	protected int bytesPerSample = -1;
	
	/**
	 * The number of channels read from the WAV file's header.
	 */
	protected int numberOfChannels = -1;
	
	/**
	 * The position in the file at which the current chunk begins.
	 */
	protected long chunkStartPosition = -1;
	
	/**
	 * The size of the current data chunk.
	 */
	protected long sampChunkSize = -1;
	
	
	
	
	/**
	 * Reads up to len bytes of data from this file into an array of bytes. This method blocks until at least one byte of input is available.
	 * @param b the buffer into which the data is read.
	 * @param off the start offset in array b at which the data is written.
	 * @param len the maximum number of bytes read.
	 * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the file has been reached.
	 * @throws IOException If the first byte cannot be read for any reason other than end of file, or if the random access file has been closed, or if some other I/O error occurs.
	 */
	public int read( final byte[] b , final int off , final int len ) throws IOException
	{
		return( bis.read(b, off, len) );
	}
	
	
	/**
	 * Reads up to b.length bytes of data from this file into an array of bytes. This method blocks until at least one byte of input is available.
	 * @param b the buffer into which the data is read.
	 * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of this file has been reached.
	 * @throws IOException  If the first byte cannot be read for any reason other than end of file, or if the random access file has been closed, or if some other I/O error occurs.
	 */
	public int read( final byte[] b ) throws IOException
	{
		return( bis.read(b) );
	}
	
	
	/**
	 * Moves the RandomAccessFile to a particular position relative to the start of the current chunk.
	 * @param chunkPosition The offset position relative to the start of the current chunk.
	 * @throws IOException
	 */
	public void chunkPosition( final long chunkPosition ) throws IOException
	{
		bis.seek( chunkPosition + chunkStartPosition );
	}
	
	
	
	
	/**
	 * Asserts that a char matches the expected char.  Otherwise throws a format mismatch exception.
	 * @param in The input char.
	 * @param test The expected char to be matched.
	 */
	protected void assertChar( char in , char test )
	{
		if( in != test )
		{
			throw( new RuntimeException( "Format Mismatch" ) );
		}
	}
	
	
	/**
	 * Asserts that a byte matches the expected byte.  Otherwise throws a format mismatch exception.
	 * @param in Integer containing the input byte.
	 * @param test Integer containing the byte to be matched.
	 */
	protected void assertInt( int in , int test )
	{
		if( ( in & BYTE_MASK ) != ( test & BYTE_MASK ) )
		{
			throw( new RuntimeException( "Format Mismatch" ) );
		}
	}
	
	
	/**
	 * Gets the sample rate (in hertz) read from the WAV file's header.
	 * @return The sample rate (in hertz) read from the WAV file's header.
	 */
	public int getSampleRate( )
	{
		return( sampleRate );
	}
	
	
	/**
	 * Gets the number of channels read from the WAV file's header.
	 * @return The number of channels read from the WAV file's header.
	 */
	public int getNumberOfChannels( )
	{
		return( numberOfChannels );
	}
	
	
	/**
	 * Gets the number of bits per sample read from the WAV file's header.
	 * @return The number of bits per sample read from the WAV file's header.
	 */
	public int getBitsPerSample( )
	{
		return( bitsPerSample );
	}
	
	
	/**
	 * Gets the number of bytes per sample read from the WAV file's header.
	 * @return The number of bytes per sample read from the WAV file's header.
	 */
	public int getBytesPerSample( )
	{
		return( bytesPerSample );
	}
	
	
	/**
	 * Gets the size of the current data chunk.
	 * @return The size of the current data chunk.
	 */
	public long getSampChunkSize()
	{
		return( sampChunkSize );
	}
	
	
	
	
	/**
	 * Constructor.
	 * @param _fi Path to the WAV file to be read.
	 * @throws IOException
	 */
	public WavReader_32( File _fi ) throws IOException
	{
		bis = new RandomAccessFile( _fi ,  "r" );
		fi = _fi;
	}
	
	
	/**
	 * Opens the RandomAccessFile and reads the WAV header blocks.
	 * @throws IOException
	 */
	public void start() throws IOException
	{
		
		assertChar( (char)( bis.read() ) , 'R' );
		assertChar( (char)( bis.read() ) , 'I' );
		assertChar( (char)( bis.read() ) , 'F' );
		assertChar( (char)( bis.read() ) , 'F' );
		
		
		// Little endian length minus 8.
		final int le0 = ( bis.read() ) & BYTE_MASK;
		final int le1 = ( bis.read() ) & BYTE_MASK;
		final int le2 = ( bis.read() ) & BYTE_MASK;
		final int le3 = ( bis.read() ) & BYTE_MASK;
		final int lenMinusEight = le0 + ( le1 << 8 ) + ( le2 << 16 ) + ( le3 << 24 );
		if( ( bis.length() ) != ( lenMinusEight + 8 ) )
		{
			throw( new RuntimeException( "Length Mismatch" ) );
		}
		
		
		assertChar( (char)( bis.read() ) , 'W' );
		assertChar( (char)( bis.read() ) , 'A' );
		assertChar( (char)( bis.read() ) , 'V' );
		assertChar( (char)( bis.read() ) , 'E' );
		
		
		readFormat();
		
		readDataChunkHeader();
		
	}
	
	
	
		
	
	/**
	 * Closes the RandomAccessFile and disposes of it.
	 * @throws IOException
	 */
	public void close( ) throws IOException
	{
		bis.close();
		
		bis = null;	
	}
	
	
	
	
	/**
	 * Reads the WAV header block containing the format definitions.
	 * @throws IOException
	 */
	protected void readFormat() throws IOException
	{
		assertChar( (char)( bis.read() ) , 'f' );
		assertChar( (char)( bis.read() ) , 'm' );
		assertChar( (char)( bis.read() ) , 't' );
		assertChar( (char)( bis.read() ) , ' ' );
		
		
		// Little endian chunk size.
		assertInt( bis.read() , 16 ); 
		assertInt( bis.read() , 0 );
		assertInt( bis.read() , 0 );
		assertInt( bis.read() , 0 );
		
		
		// Little endian compression code = PCM.
		assertInt( bis.read() , 1 ); 
		assertInt( bis.read() , 0 );
		
		
		// Little endian number of channels
		final int nc0 = ( bis.read() ) & BYTE_MASK;
		final int nc1 = ( bis.read() ) & BYTE_MASK;
		numberOfChannels = nc0 + ( nc1 << 8 );
		
		
		// Little endian sample rate
		final int sr0 = ( bis.read() ) & BYTE_MASK;
		final int sr1 = ( bis.read() ) & BYTE_MASK;
		final int sr2 = ( bis.read() ) & BYTE_MASK;
		final int sr3 = ( bis.read() ) & BYTE_MASK;
		sampleRate = sr0 + ( sr1 << 8 ) + ( sr2 << 16 ) + ( sr3 << 24 );
		
		
		// Average bytes per second
		final int sa0 = ( bis.read() ) & BYTE_MASK;
		final int sa1 = ( bis.read() ) & BYTE_MASK;
		final int sa2 = ( bis.read() ) & BYTE_MASK;
		final int sa3 = ( bis.read() ) & BYTE_MASK;
		final int sampAvgBytesPerSec = sa0 + ( sa1 << 8 ) + ( sa2 << 16 ) + ( sa3 << 24 );
		
		
		
		// Block align
		final int ba0 = ( bis.read() ) & BYTE_MASK;
		final int ba1 = ( bis.read() ) & BYTE_MASK;
		final int sampBlockAlign = ba0 + ( ba1 << 8 );
		
		
		// Significant bits per sample
		final int bs0 = ( bis.read() ) & BYTE_MASK;
		final int bs1 = ( bis.read() ) & BYTE_MASK;
		bitsPerSample = bs0 + ( bs1 << 8 );
		bytesPerSample = bitsPerSample / 8;
		
		
		final int blockAlign = ( bitsPerSample / 8 ) * numberOfChannels;
		if( blockAlign != sampBlockAlign )
		{
			throw( new RuntimeException( "Block Align Mismatch" ) );
		}
		
		
		
		final int avgBytesPerSec = sampleRate * blockAlign;
		if( avgBytesPerSec != sampAvgBytesPerSec )
		{
			throw( new RuntimeException( "AvgBytesPerSec Mismatch" ) );
		}
		
		
	}
	
	
	
	/**
	 * Reads the header at the start of the next data chunk.
	 * @throws IOException
	 */
	protected void readDataChunkHeader() throws IOException
	{
		assertChar( (char)( bis.read() ) , 'd' );
		assertChar( (char)( bis.read() ) , 'a' );
		assertChar( (char)( bis.read() ) , 't' );
		assertChar( (char)( bis.read() ) , 'a' );
		
		
		final long chunkSizePosition = bis.getFilePointer();
		
		
		// Little endian chunk size
		final int cs0 = ( bis.read() ) & BYTE_MASK;
		final int cs1 = ( bis.read() ) & BYTE_MASK;
		final int cs2 = ( bis.read() ) & BYTE_MASK;
		final int cs3 = ( bis.read() ) & BYTE_MASK;
		sampChunkSize = cs0 + ( cs1 << 8 ) + ( cs2 << 16 ) + ( cs3 << 24 );
		
		
		chunkStartPosition = bis.getFilePointer();
		
		
		long sampChunkEnd = chunkStartPosition + sampChunkSize;
		
		
		// Make sure that chunks are word-aligned.
		if( ( sampChunkSize % 2 ) != 0 )
		{
			sampChunkEnd++;
		}
		
		
		
		if( sampChunkEnd != ( bis.length() ) )
		{
			throw( new RuntimeException( "Chunk End Mismatch" ) );
		}
		
		
		
	}
	
	
	
	

}




