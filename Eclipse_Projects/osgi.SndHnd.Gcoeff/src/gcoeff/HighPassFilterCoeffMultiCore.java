





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







package gcoeff;

import core.NonClampedCoefficientMultiCore;

/**
 * A simple (not necessarily good) high-pass filter.
 * 
 * The evaluation interval is basically the inverse of the cutoff frequency for the filter.
 * 
 * @author tgreen
 *
 */
public class HighPassFilterCoeffMultiCore extends LowPassFilterCoeffMultiCore {

	/**
	 * Constructs the filter.
	 * @param _wave The wave to be filtered.
	 * @param _intervalHalfLength The evaluation interval half-length.
	 * @param _sampleLen The number of sampling points to use in the filtering.
	 */
	public HighPassFilterCoeffMultiCore(NonClampedCoefficientMultiCore _wave, double _intervalHalfLength,
			int _sampleLen) {
		super(_wave, _intervalHalfLength, _sampleLen);
	}

	/**
	 * Default constructor.
	 */
	public HighPassFilterCoeffMultiCore() {
		super();
	}

	@Override
	public double eval(double param, final int core) {
		double lowPass = super.eval( param , core);
		double orig = wave.eval( param , core );
		return( orig - lowPass );
	}

	
} 

