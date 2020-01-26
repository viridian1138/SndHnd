




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







package cwaves;

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.WaveForm;


/**
 * Node representing a waveform for adding harmonic distortion to an input waveform.
 * 
 * 
 * See:  https://www.soundonsound.com/techniques/analogue-warmth
 * 
 * 
 * See:  https://www.waves.com/add-harmonic-distortion-for-analog-warmth
 * 
 * 
 * @author tgreen
 *
 */
public class GHarmonicDistortionSampleWaveForm extends GWaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	protected GWaveForm wave;

	/**
	 * Amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 */
	double firstHarmonicDistortion = 0.7;
	
	/**
	 * The maximum harmonic number for which to add harmonic distortions.
	 */
	int maxHarmonicNum = 10;
	
	/**
	 * Whether to add odd harmonics.
	 */
	boolean oddHarmonics = false;
	
	/**
	 * Whether to add even harmonics.
	 */
	boolean evenHarmonics = true;
	
	/**
	 * Whether to use a divisor to normalize the amplitude of the resulting wave.
	 */
	boolean useDivisor = true;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		
		HarmonicDistortionSampleWaveForm wv = new HarmonicDistortionSampleWaveForm( w , firstHarmonicDistortion,
				maxHarmonicNum,
				oddHarmonics,
				evenHarmonics,
				useDivisor );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( wave );
	}

	@Override
	public String getName() {
		return( "HarmonicDistortionSampleWaveForm" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		wave = (GWaveForm) in;
	}

	@Override
	public void removeChld() {
		wave = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param w The input waveform.
	 * @param _firstHarmonicDistortion Amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 * @param _maxHarmonicNum The maximum harmonic number for which to add harmonic distortions.
	 * @param _oddHarmonics Whether to add odd harmonics.
	 * @param _evenHarmonics Whether to add even harmonics.
	 * @param _useDivisor Whether to use a divisor to normalize the amplitude of the resulting wave.
	 */
	public void load( GWaveForm w , double _firstHarmonicDistortion , int _maxHarmonicNum , 
			boolean _oddHarmonics , boolean _evenHarmonics , boolean _useDivisor )
	{
		wave = w;
		firstHarmonicDistortion = _firstHarmonicDistortion;
		maxHarmonicNum = _maxHarmonicNum;
		oddHarmonics = _oddHarmonics;
		evenHarmonics = _evenHarmonics;
		useDivisor = _useDivisor;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		HarmonicDistortionSampleEditor editor = new HarmonicDistortionSampleEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Harmonic Distortion Sample Properties");
	}

	/**
	 * Gets the amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 * @return The amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 */
	public double getFirstHarmonicDistortion() {
		return firstHarmonicDistortion;
	}

	/**
	 * Sets the amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 * @param firstHarmonicDistortion The amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 */
	public void setFirstHarmonicDistortion(double firstHarmonicDistortion) {
		this.firstHarmonicDistortion = firstHarmonicDistortion;
	}

	/**
	 * Gets the maximum harmonic number for which to add harmonic distortions.
	 * @return The maximum harmonic number for which to add harmonic distortions.
	 */
	public int getMaxHarmonicNum() {
		return maxHarmonicNum;
	}

	/**
	 * Sets the maximum harmonic number for which to add harmonic distortions.
	 * @param maxHarmonicNum The maximum harmonic number for which to add harmonic distortions.
	 */
	public void setMaxHarmonicNum(int maxHarmonicNum) {
		this.maxHarmonicNum = maxHarmonicNum;
	}

	/**
	 * Gets whether to add odd harmonics.
	 * @return Whether to add odd harmonics.
	 */
	public boolean isOddHarmonics() {
		return oddHarmonics;
	}

	/**
	 * Sets whether to add odd harmonics.
	 * @param oddHarmonics Whether to add odd harmonics.
	 */
	public void setOddHarmonics(boolean oddHarmonics) {
		this.oddHarmonics = oddHarmonics;
	}

	/**
	 * Gets whether to add even harmonics.
	 * @return Whether to add even harmonics.
	 */
	public boolean isEvenHarmonics() {
		return evenHarmonics;
	}

	/**
	 * Sets whether to add even harmonics.
	 * @param evenHarmonics Whether to add even harmonics.
	 */
	public void setEvenHarmonics(boolean evenHarmonics) {
		this.evenHarmonics = evenHarmonics;
	}

	/**
	 * Gets whether to use a divisor to normalize the amplitude of the resulting wave.
	 * @return Whether to use a divisor to normalize the amplitude of the resulting wave.
	 */
	public boolean isUseDivisor() {
		return useDivisor;
	}

	/**
	 * Sets whether to use a divisor to normalize the amplitude of the resulting wave.
	 * @param useDivisor Whether to use a divisor to normalize the amplitude of the resulting wave.
	 */
	public void setUseDivisor(boolean useDivisor) {
		this.useDivisor = useDivisor;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
		myv.setDouble("FirstHarmonicDistortion", firstHarmonicDistortion);
		myv.setInt("MaxHarmonicNum", maxHarmonicNum);
		myv.setBoolean("OddHarmonics", oddHarmonics);
		myv.setBoolean("EvenHarmonics", evenHarmonics);
		myv.setBoolean("UseDivisor", useDivisor);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (GWaveForm)( myv.getProperty("Wave") );
			firstHarmonicDistortion = myv.getDouble("FirstHarmonicDistortion");
			maxHarmonicNum = myv.getInt("MaxHarmonicNum");
			oddHarmonics = myv.getBoolean("OddHarmonics");
			evenHarmonics = myv.getBoolean("EvenHarmonics");
			useDivisor = myv.getBoolean("UseDivisor");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

