




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


import gredit.GNode;
import gredit.GNonClampedCoefficient;

import java.io.Externalizable;
import java.util.HashMap;

/**
 * An abstract base class representing a non-clamped coefficient, i.e. one whose values are not necessarily encouraged (for the amplitude) to be in [0, 1].
 * 
 * Class gredit.GNonClampedCoefficient interacts with class cNonClampedCoefficient.  Calss gredit.GNonClampedCoefficient is single-threaded, mutable, and
 * editable, whereas NonClampedCoefficient is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public abstract class NonClampedCoefficient implements Externalizable {
	
	/**
	 * Constructs the class.
	 */
	public NonClampedCoefficient( )
	{
		super( );
	}
	
	/**
	 * Evaluates the NonClampedCoefficient.
	 * @param non_phase_distorted_param The non-phase-distorted parameter at which to evaluate.
	 * @return The result of evaluating the NonClampedCoefficient.
	 */
	public abstract double eval( final double non_phase_distorted_param );
	
	/**
	 * Produces an instance of the NonClampedCoefficient that can be safely used on another thread.  
	 * Making the NonClampedCoefficient immutable says some things about its
	 * thread-safety, but doesn't actually make it thread-safe.  Some internal state information can 
	 * change even if the object cannot be mutated through an interface.
	 * @return An instance of the NonClampedCoefficient that can be used on another thread.
	 * @throws Throwable
	 */
	public abstract NonClampedCoefficient genClone() throws Throwable;
	
	/**
	 * Generates the corresponding GNonClampedCoefficient node for this NonClampedCoefficient.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding GNonClampedCoefficient node for this NonClampedCoefficient.
	 */
	public abstract GNonClampedCoefficient genCoeff( HashMap<Object,GNode> s );

}

