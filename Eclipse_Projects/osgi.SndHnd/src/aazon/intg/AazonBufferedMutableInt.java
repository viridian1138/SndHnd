




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







package aazon.intg;

import aazon.AazonListener;

/**
 * An Aazon mutable integer that buffers the value of an input mutable integer that requires more computational work to evaluate.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedMutableInt extends AazonMutableInt implements AazonListener {
	
	/**
	 * The input mutable integer.
	 */
	protected AazonMutableInt a;
	
	/**
	 * The buffered version of the current value captured from the mutable integer.
	 */
	protected int x;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The input mutable integer.
	 */
	private AazonBufferedMutableInt( AazonMutableInt _a )
	{
		a = _a;
		a.add( this );
	}

	@Override
	public int getX() {
		if( !enabled )
		{
			x = a.getX();
			enabled = true;
		}
		
		return( x );
	}
	
	/**
	 * Handles a change to the input integer by invalidating the buffer, and then firing events.
	 */
	public void handleListen()
	{
		enabled = false;
		fire();
	}
	
	/**
	 * Returns an Aazon integer that buffers the value of an input integer that requires more computational work to evaluate.
	 * @param _a The input integer.
	 * @return The Aazon integer that buffers the value of an input integer that requires more computational work to evaluate.
	 */
	public static AazonInt construct( AazonInt _a )
	{
		if( _a instanceof AazonImmutableInt )
		{
			return( AazonBufferedImmutableInt.construct( _a ) );
		}
		
		return( new AazonBufferedMutableInt( (AazonMutableInt) _a ) );
	}

	
}

