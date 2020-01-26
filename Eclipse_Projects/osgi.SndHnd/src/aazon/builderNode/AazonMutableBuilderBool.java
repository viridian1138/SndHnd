




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







package aazon.builderNode;

import aazon.AazonListener;
import aazon.bool.AazonBaseImmutableBool;
import aazon.bool.AazonBool;
import aazon.bool.AazonMutableBool;


/**
 * An Aazon mutable boolean returning whether the position of a builder node is non-null.
 * 
 * @author tgreen
 *
 */
public class AazonMutableBuilderBool extends AazonMutableBool implements AazonListener {
	
	/**
	 * The universe ID.
	 */
	Object univ;
	
	/**
	 * The builder node to check.
	 */
	protected AazonMutableBuilderNode a;
	
	/**
	 * Factory for generating vectors to the center point of a node.
	 */
	protected AazonCenterVectLocationFactory fact;
	
	/**
	 * Private constructor.
	 * @param _univ The universe ID.
	 * @param _a The builder node to check.
	 * @param _fact Factory for generating vectors to the center point of a node.
	 */
	private AazonMutableBuilderBool( Object _univ , AazonMutableBuilderNode _a , AazonCenterVectLocationFactory _fact )
	{
		univ = _univ;
		a = _a;
		fact = _fact;
		a.add( this );
	}
	
	/**
	 * Returns an Aazon boolean returning whether the position of a builder node is non-null.
	 * @param univ The universe ID.
	 * @param _a The builder node to check.
	 * @param fact Factory for generating vectors to the center point of a node.
	 * @return Aazon boolean returning whether the position of a builder node is non-null.
	 */
	public static AazonBool construct( Object univ , AazonBuilderNode _a , AazonCenterVectLocationFactory fact )
	{
		if( _a instanceof AazonImmutableBuilderNode )
		{
			return( AazonImmutableBuilderBool.construct( univ , (AazonImmutableBuilderNode) _a , fact ) );
		}
		return( new AazonMutableBuilderBool( univ , (AazonMutableBuilderNode) _a , fact ) );
	}

	@Override
	public AazonBaseImmutableBool getBool() {
		return( AazonBaseImmutableBool.construct( a.getX(univ, fact) != null ) );
	}
	
	/**
	 * Handles a change to the builder node by firing events.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

