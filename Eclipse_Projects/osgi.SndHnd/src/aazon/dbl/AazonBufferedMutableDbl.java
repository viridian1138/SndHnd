




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







package aazon.dbl;

import aazon.AazonListener;


/**
 * An Aazon mutable double that buffers the value of an input mutable double that requires more computational work to evaluate.
 * 
 * @author tgreen
 *
 */
public class AazonBufferedMutableDbl extends AazonMutableDbl implements AazonListener {
	
	/**
	 * The input mutable double.
	 */
	protected AazonMutableDbl a;
	
	/**
	 * The buffered version of the current value captured from the mutable double.
	 */
	protected double x;
	
	/**
	 * Whether the buffer has been enabled.
	 */
	protected boolean enabled = false;
	
	/**
	 * Private constructor.
	 * @param _a The input mutable double.
	 */
	private AazonBufferedMutableDbl( AazonMutableDbl _a )
	{
		a = _a;
		a.add( this );
	}

	@Override
	public double getX() {
		if( !enabled )
		{
			x = a.getX();
			enabled = true;
		}
		
		return( x );
	}
	
	/**
	 * Handles a change to the input double by invalidating the buffer, and then firing events.
	 */
	public void handleListen()
	{
		enabled = false;
		fire();
	}

	/**
	 * Returns an Aazon double that buffers the value of an input double that requires more computational work to evaluate.
	 * @param _a The input double.
	 * @return The Aazon double that buffers the value of an input double that requires more computational work to evaluate.
	 */
	public static AazonDbl construct( AazonDbl _a )
	{
		if( _a instanceof AazonImmutableDbl )
		{
			return( AazonBufferedImmutableDbl.construct( _a ) );
		}
		
		return( new AazonBufferedMutableDbl( (AazonMutableDbl) _a ) );
	}

	
}

