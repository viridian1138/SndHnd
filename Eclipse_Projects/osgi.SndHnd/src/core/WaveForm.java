




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
import gredit.GWaveForm;

import java.io.Externalizable;
import java.util.HashMap;

/**
 * An abstract base class representing a waveform, which is encouraged but not required to be clamped to [-1, 1].
 * 
 * Where possible, and particularly for the typical synthesizer waveforms such as sine, square, triangle, etc., it
 * is encouraged but not required for the waveform to have a period of unity.
 * 
 * Where the waveform is a lattice noise wave for which there is no obvious repeating period, it is encouraged but
 * not required for the waveform to have a lattice spacing of unity as this often produces a wave period that
 * is within an octave of unity.
 * 
 * Where the waveform is sampled from an audio file and hence there is no obvious period, there is
 * a useAutocorrelation() method to indicate that autocorrelation should be used to determine the wave 
 * period for a particular part of the sample.
 * 
 * There are some cases where the period of the wave is not on the same octave as the audibly
 * perceived period.  This is handled on a case-by-case basis by subclasses of IntelligentAgent.
 * 
 * Class WaveForm interacts with class gredit.GWaveForm.  Class gredit.GWaveForm is single-threaded, mutable, and
 * editable, whereas WaveForm is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public abstract class WaveForm extends NonClampedCoefficient implements Externalizable {
	
	/**
	 * The recommended, but not required, period of the WaveFrom.
	 * 
	 * Where possible, and particularly for the typical synthesizer waveforms such as sine, square, triangle, etc., it
	 * is encouraged but not required for the waveform to have a period of unity, as described by this constant.
	 * 
	 * Where the waveform is a lattice noise wave for which there is no obvious repeating period, it is encouraged but
	 * not required for the waveform to have a lattice spacing of unity as this often produces a wave period that
	 * is within an octave of unity.
	 * 
	 * Where the waveform is sampled from an audio file and hence there is no obvious period, there is
	 * a useAutocorrelation() method to indicate that autocorrelation should be used to determine the wave 
	 * period for a particular part of the sample.
	 */
	public static final double WAVELENGTH = 1.0;
	
	/**
	 * Constructor for the class.
	 */
	public WaveForm()
	{
	}
	
	/**
	 * Where the waveform is sampled from an audio file and hence there is no obvious period, there is
	 * returns true to indicate that autocorrelation should be used to determine the wave 
	 * period for a particular part of the sample.
	 * @return Whether autocorrelation should be used to determine the wave period.
	 */
	public boolean useAutocorrelation()
	{
		return( false );
	}
	
	/**
	 * Generates the corresponding GWaveForm node for this WaveForm.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding GWaveForm node for this WaveForm.
	 */
	public abstract GWaveForm genWave( HashMap s );
	
	
	@Override
	public final GNonClampedCoefficient genCoeff( HashMap<Object,GNode> s )
	{
		return( genWave( s ) );
	}

	
}

