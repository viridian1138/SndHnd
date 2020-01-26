




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


import java.io.*;
import javax.sound.sampled.*;

/**
 * Class implementing a simple paged virtual memory scheme for random access to sound samples in an audio file.
 * 
 * The underlying audio stream does not support true random access, so this class compensates using a form of virtual memory.
 * 
 * This implementation currently uses a total of three virtual memory pages.
 * 
 * See:  https://en.wikipedia.org/wiki/Virtual_memory
 * 
 * @author tgreen
 *
 */
public class SampledVirtual {
	
	/**
	 * The file for the underlying audio stream.
	 */
	File fi;
	
	/**
	 * The size of each virtual memory page in bytes.
	 */
	static final int BUF_SZ = 4092; /*4096;*/
	
	/**
	 * The index into the "buffs" array of the next page to be paged-out, where pages are paged-out in a round-robin fashion.
	 */
	int curIndex = 0;
	
	/**
	 * The page index of each virtual memory page.
	 */
	final int[] indices = { -1 , -1 , -1 };
	
	/**
	 * Array of three virtual memory pages.
	 */
	final Object[] buffs = { new byte[ BUF_SZ ] , new byte[ BUF_SZ ] , new byte[ BUF_SZ ] };
	
	/**
	 * The underlying audio stream.
	 */
	AudioInputStream stream = null;
	
	/**
	 * The current stream position of the underlying audio stream.
	 */
	int streamPos = 0;
	
	
	/**
	 * Constructs the sampler.
	 * @param _fi The file for the underlying audio stream.
	 */
	public SampledVirtual( File _fi )
	{
		fi = _fi;
	}
	
	/**
	 * Gets the byte at a particular index.
	 * @param smpl The index at which to sample.
	 * @return The value of the byte at the index.
	 * @throws Throwable
	 */
	public byte get( int smpl ) throws Throwable
	{
		int index = smpl / BUF_SZ;
		
		int cnt;
		for( cnt = 0 ; cnt < 3 ; cnt++ )
		{
			if( indices[ cnt ] == index )
			{
				byte[] buf = (byte[])( buffs[ cnt ] );
				return( buf[ smpl % BUF_SZ ] );
			}
		}
		
		byte[] bb = (byte[])( buffs[ curIndex ] );
		indices[ curIndex ] = index;
		
		if( streamPos <= ( index * BUF_SZ ) )
		{
			if( stream == null )
			{
				stream = AudioSystem.getAudioInputStream( fi );
			}
			
			stream.skip( index * BUF_SZ - streamPos );
			streamPos = index * BUF_SZ;
		}
		else
		{
			stream.close();
			stream = null;
			streamPos = 0;
		}
		
		if( stream == null )
		{
			stream = AudioSystem.getAudioInputStream( fi );
			stream.skip( index * BUF_SZ );
			streamPos = index * BUF_SZ;
		}
		
		int nBytesRead = 0;
		int lastBytesRead = 0;
		
		while( ( nBytesRead < BUF_SZ ) && ( lastBytesRead >= 0 ) )
		{
			lastBytesRead = stream.read( bb , nBytesRead , BUF_SZ - nBytesRead );
			if( lastBytesRead > 0 )
			{
				nBytesRead += lastBytesRead;
			}
			else
			{
				if( lastBytesRead < 0 )
				{
					stream.close();
				}
			}
		}
		streamPos += nBytesRead;
		
		curIndex++;
		if( curIndex > 2 ) curIndex = 0;
			
		return( bb[ smpl % BUF_SZ ] );
	}

}

