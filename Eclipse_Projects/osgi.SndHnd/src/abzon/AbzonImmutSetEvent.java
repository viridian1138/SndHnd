




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







package abzon;

import aazon.AazonListener;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutSetMutableVect;
import aazon.vect.AazonMutableVect;

/**
 * Abzon listener that upon invocation sets an output vector to the value of an input vector.
 * 
 * @author tgreen
 *
 */
public class AbzonImmutSetEvent implements AazonListener {
	
	/**
	 * The input vector.
	 */
	AazonMutableVect cin;
	
	/**
	 * The output vector.
	 */
	AazonImmutSetMutableVect cout;
	
	/**
	 * Constructs the listener.
	 * @param _cin The input vector.
	 * @param _cout The output vector.
	 */
	public AbzonImmutSetEvent( AazonMutableVect _cin , AazonImmutSetMutableVect _cout )
	{
		cin = _cin;
		cout = _cout;
		cin.add( this );
	}

	/**
	 * Handles the invocation of the listener.
	 */
	public void handleListen()
	{
		cout.setCoords( new AazonBaseImmutableVect( cin.getX() , cin.getY() ) );
	}

}

