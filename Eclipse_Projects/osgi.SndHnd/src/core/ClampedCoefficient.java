




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


import gredit.GClampedCoefficient;
import gredit.GNode;
import gredit.GNonClampedCoefficient;

import java.io.Externalizable;
import java.util.HashMap;

/**
 * An abstract base class for a clamped coefficient, which is encouraged but not required to be clamped (in amplitude) to [0, 1].
 * 
 * Class ClampedCoefficient interacts with class gredit.GClampedCoefficient.  GClampedCoefficient is single-threaded, mutable, and
 * editable, whereas ClampedCoefficient is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public abstract class ClampedCoefficient extends NonClampedCoefficient implements Externalizable {
	
	/**
	 * The recommended amplitude max of the coefficient.
	 */
	public static final double LENGTH = 1.0;
	
	/**
	 * Constructor
	 */
	public ClampedCoefficient()
	{
	}
	
	@Override
	public abstract double eval( final double non_phase_distorted_param );
	
	/**
	 * Generates the corresponding GClampedCoefficient node for this ClampedCoefficient.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding GClampedCoefficient node for this ClampedCoefficient.
	 */
	public abstract GClampedCoefficient genClamped( HashMap<Object,GNode> s );
	
	@Override
	public final GNonClampedCoefficient genCoeff( HashMap<Object,GNode> s )
	{
		return( genClamped( s ) );
	}

}

