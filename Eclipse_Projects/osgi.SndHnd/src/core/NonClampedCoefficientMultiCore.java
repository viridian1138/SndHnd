




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


import java.io.Externalizable;


/**
 * Base class for a global over-song evaluation coefficient where the normal rules
 * of a NonClampedCoefficient being thread-safe and core-thread-agnostic don't apply
 * because the class needs access to per-core-thread resources in the core
 * song generation system.
 * 
 * @author thorngreen
 *
 */
public abstract class NonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * Constructor.
	 */
	public NonClampedCoefficientMultiCore( )
	{
		super( );
	}
	
	/**
	 * Evaluates the coefficient.
	 * @param non_phase_distorted_param The non-phase-distorted parameter at which to evaluate.
	 * @param core The number of the core thread.
	 * @return The result of the evaluation.
	 */
	public abstract double eval( final double non_phase_distorted_param , final int core );

}

