




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

import aazon.vect.AazonVect;

/**
 * Factory for generating a vector to the center location of a node.
 * 
 * @author tgreen
 *
 */
public final class AazonDefaultCenterVectLocationFactory implements
		AazonCenterVectLocationFactory {
	
	/**
	 * Singleton instance of the factory.
	 */
	private static AazonDefaultCenterVectLocationFactory tmp = null;
	
	/**
	 * Private constructor.
	 */
	private AazonDefaultCenterVectLocationFactory()
	{
	}

	/**
	 * Returns a vector to the center location of a node.
	 * @return The vector to the center location of a node.
	 */
	public AazonVect genVect(AazonVect in) {
		return( in );
	}

	/**
	 * Gets the scaling factor.
	 * @return The scaling factor.
	 */
	public double getScaleVal() {
		return( 1.0 );
	}
	
	/**
	 * Returns a singleton instance of the factory.
	 * @return The singleton instance of the factory.
	 */
	public static AazonDefaultCenterVectLocationFactory getFact()
	{
		if( tmp == null )
		{
			tmp = new AazonDefaultCenterVectLocationFactory();
		}
		return( tmp );
	}

	
}


