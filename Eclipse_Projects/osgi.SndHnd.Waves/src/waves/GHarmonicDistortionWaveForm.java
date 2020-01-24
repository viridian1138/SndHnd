





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







package waves;

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
 * Node representing a waveform that adds copies of an input wave at even harmonics and/or odd harmonics.
 * 
 * @author tgreen
 *
 */
public class GHarmonicDistortionWaveForm extends GWaveForm  implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	protected GWaveForm wave;

	/**
	 * The amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 */
	double firstHarmonicDistortion = 0.51;
	
	/**
	 * The maximum number of harmonics to generate.
	 */
	int maxHarmonicNum = 11;
	
	/**
	 * The amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 */
	double firstSubHarmonicDistortion = 0.51;
	
	/**
	 * The maximum number of subharmonics to generate.
	 */
	int maxSubHarmonicNum = -1;
	
	/**
	 * Whether to generate odd harmonics and/or subharmonics.
	 */
	boolean oddHarmonics = false;
	
	/**
	 * Whether to generate even harmonics and/or subharmonics.
	 */
	boolean evenHarmonics = true;
	
	/**
	 * Whether to calculate a sum or harmonic magnitudes to normalize the result.
	 */
	boolean useDivisor = false;

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		
		HarmonicDistortionWaveForm wv = new HarmonicDistortionWaveForm( w , firstHarmonicDistortion,
				maxHarmonicNum,
				firstSubHarmonicDistortion,
				maxSubHarmonicNum,
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
		return( "HarmonicDistortionWaveForm" );
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
	 * @param _firstHarmonicDistortion The amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 * @param _maxHarmonicNum The maximum number of harmonics to generate.
	 * @param _firstSubHarmonicDistortion The amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 * @param _maxSubHarmonicNum The maximum number of subharmonics to generate.
	 * @param _oddHarmonics Whether to generate odd harmonics and/or subharmonics.
	 * @param _evenHarmonics Whether to generate even harmonics and/or subharmonics.
	 * @param _useDivisor Whether to calculate a sum or harmonic magnitudes to normalize the result.
	 */
	public void load( GWaveForm w , double _firstHarmonicDistortion , int _maxHarmonicNum , 
			double _firstSubHarmonicDistortion , int _maxSubHarmonicNum ,
			boolean _oddHarmonics , boolean _evenHarmonics , boolean _useDivisor )
	{
		wave = w;
		firstHarmonicDistortion = _firstHarmonicDistortion;
		maxHarmonicNum = _maxHarmonicNum;
		firstSubHarmonicDistortion = _firstSubHarmonicDistortion;
		maxSubHarmonicNum = _maxSubHarmonicNum;
		oddHarmonics = _oddHarmonics;
		evenHarmonics = _evenHarmonics;
		useDivisor = _useDivisor;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		HarmonicDistortionEditor editor = new HarmonicDistortionEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Harmonic Distortion Properties");
	}

	/**
	 * Gets the amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 * @return The amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 */
	public double getFirstHarmonicDistortion() {
		return firstHarmonicDistortion;
	}

	/**
	 * Sets the amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 * @param firstHarmonicDistortion The amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 */
	public void setFirstHarmonicDistortion(double firstHarmonicDistortion) {
		this.firstHarmonicDistortion = firstHarmonicDistortion;
	}

	/**
	 * Gets the maximum number of harmonics to generate.
	 * @return The maximum number of harmonics to generate.
	 */
	public int getMaxHarmonicNum() {
		return maxHarmonicNum;
	}

	/**
	 * Sets the maximum number of harmonics to generate.
	 * @param maxHarmonicNum The maximum number of harmonics to generate.
	 */
	public void setMaxHarmonicNum(int maxHarmonicNum) {
		this.maxHarmonicNum = maxHarmonicNum;
	}

	/**
	 * Gets the amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 * @return The amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 */
	public double getFirstSubHarmonicDistortion() {
		return firstSubHarmonicDistortion;
	}

	/**
	 * Sets the amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 * @param firstSubHarmonicDistortion The amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 */
	public void setFirstSubHarmonicDistortion(double firstSubHarmonicDistortion) {
		this.firstSubHarmonicDistortion = firstSubHarmonicDistortion;
	}

	/**
	 * Gets the maximum number of subharmonics to generate.
	 * @return The maximum number of subharmonics to generate.
	 */
	public int getMaxSubHarmonicNum() {
		return maxSubHarmonicNum;
	}

	/**
	 * Sets the maximum number of subharmonics to generate.
	 * @param maxSubHarmonicNum The maximum number of subharmonics to generate.
	 */
	public void setMaxSubHarmonicNum(int maxSubHarmonicNum) {
		this.maxSubHarmonicNum = maxSubHarmonicNum;
	}

	/**
	 * Gets whether to generate odd harmonics and/or subharmonics.
	 * @return Whether to generate odd harmonics and/or subharmonics.
	 */
	public boolean isOddHarmonics() {
		return oddHarmonics;
	}

	/**
	 * Sets whether to generate odd harmonics and/or subharmonics.
	 * @param oddHarmonics Whether to generate odd harmonics and/or subharmonics.
	 */
	public void setOddHarmonics(boolean oddHarmonics) {
		this.oddHarmonics = oddHarmonics;
	}

	/**
	 * Gets whether to generate even harmonics and/or subharmonics.
	 * @return Whether to generate even harmonics and/or subharmonics.
	 */
	public boolean isEvenHarmonics() {
		return evenHarmonics;
	}

	/**
	 * Sets whether to generate even harmonics and/or subharmonics.
	 * @param evenHarmonics Whether to generate even harmonics and/or subharmonics.
	 */
	public void setEvenHarmonics(boolean evenHarmonics) {
		this.evenHarmonics = evenHarmonics;
	}

	/**
	 * Gets whether to calculate a sum or harmonic magnitudes to normalize the result.
	 * @return Whether to calculate a sum or harmonic magnitudes to normalize the result.
	 */
	public boolean isUseDivisor() {
		return useDivisor;
	}

	/**
	 * Sets whether to calculate a sum or harmonic magnitudes to normalize the result.
	 * @param useDivisor Whether to calculate a sum or harmonic magnitudes to normalize the result.
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
		myv.setDouble("FirstSubHarmonicDistortion", firstSubHarmonicDistortion);
		myv.setInt("MaxSubHarmonicNum", maxSubHarmonicNum);
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
			firstSubHarmonicDistortion = myv.getDouble("FirstSubHarmonicDistortion");
			maxSubHarmonicNum = myv.getInt("MaxSubHarmonicNum");
			oddHarmonics = myv.getBoolean("OddHarmonics");
			evenHarmonics = myv.getBoolean("EvenHarmonics");
			useDivisor = myv.getBoolean("UseDivisor");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

