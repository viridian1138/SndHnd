




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







package aczon;

import javax.media.j3d.Appearance;

import aazon.AazonEnt;
import aazon.AazonImmutableGroup;
import aazon.AazonSmartLine;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonMutableScalarDivideVect;
import aazon.vect.AazonVect;

/**
 * Smart creator for a Aczon line indicating a connection between two points.
 * 
 * @author tgreen
 *
 */
public class AczonSmartConnectionLine {
	
	/**
	 * Smart creator for a Aczon line indicating a connection between two points.
	 * @param a The starting point of the line.
	 * @param ap The color of the start of the line.
	 * @param b Vector from the start of the line to the end of the line.
	 * @param bp The color at the end of the line.
	 * @return The Aczon line indicating a connection between two points.
	 */
	public static AazonEnt construct( AazonVect a , Appearance ap , AazonVect b , Appearance bp )
	{
		AazonVect addVect = AazonMutableAdditiveVect.construct( a , b );
		AazonVect midpt = AazonMutableScalarDivideVect.construct( addVect , AczonConstants.TWO );
		AazonEnt line1 = AazonSmartLine.construct( a , midpt , ap , false );
		AazonEnt line2 = AazonSmartLine.construct( midpt , b , bp , false );
		AazonEnt[] ents = { line1 , line2 };
		AazonImmutableGroup grp = new AazonImmutableGroup( ents );
		return( grp );
	}

}

