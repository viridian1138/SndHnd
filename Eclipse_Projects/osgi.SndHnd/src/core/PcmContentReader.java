




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


/**
 * Simplified interface for reading PCM (Pulse Code Modulation) audio data.
 * 
 * @author tgreen
 *
 */
public interface PcmContentReader {
	
	/**
	 * Reads a one-second frame of PCM (Pulse Code Modulation) audio data.
	 * @param frameNumber The number of the one-second frame from which to get the data.
	 * @return The bytes in the one-second frame.
	 * @throws Throwable
	 */
	public byte[] handleRead( int frameNumber ) throws Throwable;

}

