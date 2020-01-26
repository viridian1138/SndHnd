




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







package aazon.bool;

import aazon.AazonListener;

/**
 * An Aazon mutable boolean that buffers the value of an input mutable boolean that requires more computational work to evaluate.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedMutableBool extends AazonMutableBool implements AazonListener {
	
	/**
	 * The input mutable boolean.
	 */
	protected AazonMutableBool a;
	
	/**
	 * The buffered version of the current state captured from the mutable boolean.
	 */
	protected AazonBaseImmutableBool b;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The input mutable boolean.
	 */
	private AazonBufferedMutableBool( AazonMutableBool _a )
	{
		a = _a;
		a.add( this );
	}

	@Override
	public AazonBaseImmutableBool getBool() {
		if( !enabled )
		{
			b = a.getBool();
			enabled = true;
		}
		
		return( b );
	}
	
	/**
	 * Handles a change to the input boolean by invalidating the buffer, and then firing events.
	 */
	public void handleListen()
	{
		enabled = false;
		fire();
	}
	
	/**
	 * Returns an Aazon boolean that buffers the value of an input boolean that requires more computational work to evaluate.
	 * @param _a The input boolean.
	 * @return The Aazon boolean that buffers the value of an input boolean that requires more computational work to evaluate.
	 */
	public static AazonBool construct( AazonBool _a )
	{
		if( _a instanceof AazonImmutableBool )
		{
			return( AazonBufferedImmutableBool.construct( _a ) );
		}
		
		return( new AazonBufferedMutableBool( (AazonMutableBool) _a ) );
	}

	
}

