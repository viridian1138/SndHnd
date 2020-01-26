




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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import meta.DataFormatException;
import meta.VersionBuffer;
import bezier.BezierCubicClampedCoefficient;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;


/**
 * A description of a musical note.
 * @author tgreen
 *
 */
public class NoteDesc implements Externalizable {
	
	/**
	 * Constructs the note.
	 * @throws Throwable
	 */
	public NoteDesc() throws Throwable
	{
	}
	
	/**
	 * The frequency and pitch-bend (including vibrato) of the note.
	 */
	protected FreqAndBend freqAndBend = new FreqAndBend();
	
	/**
	 * The vibrato parameters of the note.
	 */
	protected VibratoParameters vibratoParams = new VibratoParameters();
	
	/**
	 * Indicates whether the vibrato is user-defined (as opposed to agent-defined).
	 */
	protected boolean isUserDefinedVibrato = false;
	
	/**
	 * The portamento description of the note.
	 */
	protected PortamentoDesc portamentoDesc = null;
	
	/**
	 * Indicates whether the end of the note is user-defined (as opposed to agent-defined).
	 */
	protected boolean isUserDefinedEnd = false;
	
	
	/**
	 * The user-defined (as opposed to agent-defined) beat number for the start of the note.
	 */
	protected double startBeatNumber;
	
	/**
	 * The user-defined (as opposed to agent-defined) beat number for the end of the note.
	 */
	protected double endBeatNumber;
	
	/**
	 * The actual agent-defined (as opposed to user-defined) beat number of the start of the note, including sustain.
	 */
	protected double actualStartBeatNumber;
	
	/**
	 * The actual agent-defined (as opposed to user-defined) beat number of the end of the note, including sustain.
	 */
	protected double actualEndBeatNumber;
	
	/**
	 * Wave form to use in the song, with one copy of the wave per core thread.  One copy for each core thread.
	 */
	protected final WaveForm[] waveform = new WaveForm[ CpuInfo.getNumCores() ];
	
	/**
	 * The user-defined (as opposed to agent-defined) envelope of the note.
	 */
	protected final BezierCubicClampedCoefficient[] noteEnvelope = new BezierCubicClampedCoefficient[ CpuInfo.getNumCores() ];
	
	/**
	 * The actual envelope of the note.
	 */
	protected final ClampedCoefficient[] actualNoteEnvelope = new ClampedCoefficient[ CpuInfo.getNumCores() ];
	
	/**
	 * Total envelope mode where total envelope is not used.
	 */
	public static final int TOTAL_ENVELOPE_MODE_NONE = 0;
	
	/**
	 * Use of the total envelope in such a way that the rest of the composition is attenuated by the total envelope during a drum hit.
	 */
	public static final int TOTAL_ENVELOPE_MODE_DRUM = 1;
	
	/**
	 * Total envelope mode where the wave is multiplied by the total envelope.
	 */
	public static final int TOTAL_ENVELOPE_MODE_MULT = 2;
	
	
	/**
	 * The total envelope mode of the note.  Allowed values are TOTAL_ENVELOPE_MODE_NONE, TOTAL_ENVELOPE_MODE_DRUM, and TOTAL_ENVELOPE_MODE_MULT.
	 */
	protected int totalEnvelopeMode = TOTAL_ENVELOPE_MODE_NONE;
	
	/**
	 * The total envelope coefficient.  See the total envelope mode.
	 */
	protected double totalEnvelopeCoeff = 1.0;
	
	/**
	 * Whether to use the user-defined (as opposed to agent-defined) note envelope.
	 */
	protected boolean userDefinedNoteEnvelope = false;
	
	/**
	 * The wave envelope for the initial few wavelengths of the note's attack (this is usually agent-defined).
	 */
	protected final NonClampedCoefficient[] waveEnvelope = new NonClampedCoefficient[ CpuInfo.getNumCores() ];
	
	/**
	 * A reference to context data that the generating code can associate with the note.  Mostly not used.  See various references to "refcon" in MacOS programming.
	 */
	protected transient Object refcon;
	
	/**
	 * The estimated number of wavelengths after which the timbre becomes essentially steady-state.
	 */
	protected transient double steadyStateWaveNum = 100;
	
	
	
	/* public double getWaveNumber( double noteU )
	{
		double multiplier = SongData.getNoteDurationSeconds( this );
		return( freqAndBend.getWaveNumber( this , noteU , multiplier ) );
	} */
	
	
	/**
	 * Gets the wave number of the note for a particular elapsed time in seconds.
	 * @param elapsedTimeSecondsGlobal The elapsed time in seconds.
	 * @param core The number of the core thread.
	 * @return The wave number.
	 */
	public double getWaveNumberElapsedTimeSeconds( double elapsedTimeSecondsGlobal , final int core )
	{
		// double multiplier = SongData.getNoteDurationSeconds( this );
		return( freqAndBend.getWaveNumber( this , elapsedTimeSecondsGlobal , core ) );
	}
	
	
	/**
	 * Updates the wave information in the note to match the current actual start beat number and actual end beat number.
	 * @param core The number of the core thread.
	 */
	public void updateWaveInfo( final int core )
	{
		// double multiplier = SongData.getNoteDurationSeconds( this );
		freqAndBend.updateWaveInfo( /* multiplier */ actualStartBeatNumber , actualEndBeatNumber , core );
	}
	
	
	/**
	 * Updates the wave information in the note to match the current actual start beat number and actual end beat number for the purposes of visual display only (doesn't produce enough information to actually play the note).
	 */
	public void updateWaveInfoDisplayOnly()
	{
		freqAndBend.updateWaveInfoDisplayOnly();
	}
	
	/**
	 * Gets the WaveForm of the note.
	 * @param core The number of the core thread.
	 * @return The WaveForm of the note.
	 */
	public WaveForm getWaveform( final int core )
	{
		return( waveform[ core ] );
	}
	
	/**
	 * Gets the copies of the WaveForm of the note for all of the core thread numbers.
	 * @return Array of copies of the WaveForm.
	 */
	public WaveForm[] getWaveformArray()
	{
		return( waveform );
	}
	
	/**
	 * Sets the WaveForm of the note.
	 * @param in The WaveForm of the note.
	 * @throws Throwable
	 */
	public void setWaveform( WaveForm in ) throws Throwable
	{
		waveform[ 0 ] = in;
		int count;
		int max = CpuInfo.getNumCores();
		if( in != null )
		{
			for( count = 1 ; count < max ; count++ )
			{
				this.waveform[ count ] = (WaveForm)( in.genClone() );
			}
		}
		else
		{
			for( count = 1 ; count < max ; count++ )
			{
				this.waveform[ count ] = null;
			}
		}
	}

	/**
	 * Gets the user-defined (as opposed to agent-defined) beat number for the end of the note.
	 * @return The user-defined (as opposed to agent-defined) beat number for the end of the note.
	 */
	public double getEndBeatNumber() {
		return endBeatNumber;
	}

	/**
	 * Sets the user-defined (as opposed to agent-defined) beat number for the end of the note.
	 * @param endBeatNumber The user-defined (as opposed to agent-defined) beat number for the end of the note.
	 */
	public void setEndBeatNumber(double endBeatNumber) {
		this.endBeatNumber = endBeatNumber;
		freqAndBend.setWaveInfoDirty( true );
	}

	/**
	 * Sets the frequency of the note in hertz.
	 * @param The frequency of the note in hertz.
	 */
	public void setFrequency(double frequency) {
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = freqAndBend.getBendPerNoteU();
		ArrayList<InterpolationPoint> intp = bez.getInterpolationPoints();
		intp.clear();
		intp.add( new InterpolationPoint( 0.0 , 1.0 ) );
		intp.add( new InterpolationPoint( 0.5 , 1.0 /* 1.5 */ ) );
		intp.add( new InterpolationPoint( 1.0 , 1.0 ) );
		freqAndBend.setBaseFreq( frequency );
		freqAndBend.setWaveInfoDirty( true );
	}

	/**
	 * Gets the user-defined (as opposed to agent-defined) beat number for the start of the note.
	 * @return The user-defined (as opposed to agent-defined) beat number for the start of the note.
	 */
	public double getStartBeatNumber() {
		return startBeatNumber;
	}

	/**
	 * Sets the user-defined (as opposed to agent-defined) beat number for the start of the note.
	 * @param startBeatNumber The user-defined (as opposed to agent-defined) beat number for the start of the note.
	 */
	public void setStartBeatNumber(double startBeatNumber) {
		this.startBeatNumber = startBeatNumber;
		freqAndBend.setWaveInfoDirty( true );
	}

	/**
	 * Gets the user-defined (as opposed to agent-defined) envelope of the note.
	 * @return The user-defined (as opposed to agent-defined) envelope of the note.
	 */
	public BezierCubicClampedCoefficient getNoteEnvelope( final int core ) {
		return noteEnvelope[ core ];
	}

	/**
	 * Sets the user-defined (as opposed to agent-defined) envelope of the note.
	 * @param noteEnvelope The user-defined (as opposed to agent-defined) envelope of the note.
	 */
	public void setNoteEnvelope(BezierCubicClampedCoefficient noteEnvelope) throws Throwable {
		this.noteEnvelope[ 0 ] = noteEnvelope;
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 1 ; count < max ; count++ )
		{
			this.noteEnvelope[ count ] = (BezierCubicClampedCoefficient)( noteEnvelope.genClone() );
		}
	}

	/**
	 * Gets the wave envelope for the initial few wavelengths of the note's attack (this is usually agent-defined).
	 * @aram core The number of the core thread.
	 * @return The wave envelope for the initial few wavelengths of the note's attack (this is usually agent-defined).
	 */
	public NonClampedCoefficient getWaveEnvelope( final int core ) {
		return waveEnvelope[ core ];
	}

	/**
	 * Sets the wave envelope for the initial few wavelengths of the note's attack (this is usually agent-defined).
	 * @param waveEnvelope The wave envelope for the initial few wavelengths of the note's attack (this is usually agent-defined).
	 */
	public void setWaveEnvelope(NonClampedCoefficient waveEnvelope) throws Throwable {
		this.waveEnvelope[ 0 ] = waveEnvelope;
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 1 ; count < max ; count++ )
		{
			this.waveEnvelope[ count ] = waveEnvelope.genClone();
		}
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			
			boolean userDef = myv.getBoolean("UserDefinedBend");
			if( userDef )
			{
				PiecewiseCubicMonotoneBezierFlatMultiCore f = (PiecewiseCubicMonotoneBezierFlatMultiCore)( myv.getProperty( "Bend" ) );
				freqAndBend.setBendPerNoteU( f );
			}
			double baseFreq = myv.getDouble("BaseFreq");
			freqAndBend.setBaseFreq( baseFreq );
			freqAndBend.setUserDefinedBend(userDef);
			freqAndBend.setWaveInfoDirty( true );
			
			startBeatNumber = myv.getDouble("StartBeatNumber");
			endBeatNumber = myv.getDouble("EndBeatNumber");
			actualStartBeatNumber = myv.getDouble("ActualStartBeatNumber");
			actualEndBeatNumber = myv.getDouble("ActualEndBeatNumber");
			// waveform = (WaveForm)( myv.getPropertyEx( "WaveForm" ) );
			userDefinedNoteEnvelope = myv.getBoolean( "UserDefinedEnvelope" );
			if( userDefinedNoteEnvelope )
			{
				setNoteEnvelope( (BezierCubicClampedCoefficient)( myv.getPropertyEx("NoteEnvelope")) );
				setActualNoteEnvelope( (ClampedCoefficient)( myv.getPropertyEx("ActualNoteEnvelope"))  );
			}
			// waveEnvelope = (NonClampedCoefficient)(myv.getPropertyEx("WaveEnvelope"));
			totalEnvelopeMode = myv.getInt("TotalEnvelopeMode");
			totalEnvelopeCoeff = myv.getDouble("TotalEnvelopeCoeff");
			isUserDefinedVibrato = myv.getBoolean( "UserDefinedVibrato" );
			if( isUserDefinedVibrato )
			{
				vibratoParams = (VibratoParameters)( myv.getPropertyEx( "VibratoParams" ) );
			}
			if( myv.getProperty( "UserDefinedEnd" ) != null )
			{
				isUserDefinedEnd = myv.getBoolean( "UserDefinedEnd" );
			}
			portamentoDesc = (PortamentoDesc)( myv.getProperty( "PortamentoDesc" ) );
			
		}
		catch (Throwable ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setBoolean("UserDefinedBend", freqAndBend.isUserDefinedBend() );
		if( freqAndBend.isUserDefinedBend() )
		{
			myv.setProperty( "Bend" , freqAndBend.getBendPerNoteU() );
		}
		myv.setDouble("BaseFreq", freqAndBend.getBaseFreq() );
		myv.setDouble("StartBeatNumber",startBeatNumber);
		myv.setDouble("EndBeatNumber",endBeatNumber);
		myv.setDouble("ActualStartBeatNumber",actualStartBeatNumber);
		myv.setDouble("ActualEndBeatNumber",actualEndBeatNumber);
		// myv.setProperty( "WaveForm" , waveform );
		myv.setBoolean("UserDefinedEnvelope", userDefinedNoteEnvelope);
		if( userDefinedNoteEnvelope )
		{
			myv.setProperty("NoteEnvelope",noteEnvelope[0]);
			myv.setProperty("ActualNoteEnvelope",actualNoteEnvelope[0]);
		}
		// myv.setProperty("WaveEnvelope",waveEnvelope);
		myv.setInt("TotalEnvelopeMode",totalEnvelopeMode);
		myv.setDouble("TotalEnvelopeCoeff",totalEnvelopeCoeff);
		myv.setBoolean( "UserDefinedVibrato" , isUserDefinedVibrato );
		myv.setBoolean( "UserDefinedEnd" , isUserDefinedEnd );
		if( isUserDefinedVibrato )
		{
			myv.setProperty("VibratoParams",vibratoParams);
		}
		if( portamentoDesc != null )
		{
			myv.setProperty( "PortamentoDesc" , portamentoDesc );
		}

		out.writeObject(myv);
	}

	/**
	 * Gets the actual agent-defined (as opposed to user-defined) beat number of the end of the note, including sustain.
	 * @return The actual agent-defined (as opposed to user-defined) beat number of the end of the note, including sustain
	 */
	public double getActualEndBeatNumber() {
		return actualEndBeatNumber;
	}

	/**
	 * Sets the actual agent-defined (as opposed to user-defined) beat number of the end of the note, including sustain
	 * @param actualEndBeatNumber The actual agent-defined (as opposed to user-defined) beat number of the end of the note, including sustain
	 */
	public void setActualEndBeatNumber(double actualEndBeatNumber) {
		this.actualEndBeatNumber = actualEndBeatNumber;
		freqAndBend.setWaveInfoDirty( true );
	}
	
	
	/**
	 * Sets the actual agent-defined (as opposed to user-defined) beat number of the end of the note, including sustain.
	 * 	Validates that the end of the current note happens before the start of the next note.
	 * @param _actualEndBeatNumber The actual agent-defined (as opposed to user-defined) beat number of the end of the note, including sustain.
	 * @param _nextStartBeatNumber The beat number for the start of the next note.
	 */
	public void setActualEndBeatNumberValidated( double _actualEndBeatNumber , double _nextStartBeatNumber )
	{
		if( isUserDefinedEnd )
		{
			if( endBeatNumber > _nextStartBeatNumber )
			{
				setActualEndBeatNumber( _nextStartBeatNumber );
				return;
			}
			
			setActualEndBeatNumber( endBeatNumber );
			return;
		}
		
		if( _actualEndBeatNumber > _nextStartBeatNumber )
		{
			setActualEndBeatNumber( _nextStartBeatNumber );
			return;
		}
		
		setActualEndBeatNumber( _actualEndBeatNumber );
	}

	/**
	 * Gets the actual agent-defined (as opposed to user-defined) beat number of the start of the note, including sustain
	 * @return The actual agent-defined (as opposed to user-defined) beat number of the start of the note, including sustain
	 */
	public double getActualStartBeatNumber() {
		return actualStartBeatNumber;
	}

	/**
	 * Sets the actual agent-defined (as opposed to user-defined) beat number of the start of the note, including sustain
	 * @param actualStartBeatNumber The actual agent-defined (as opposed to user-defined) beat number of the start of the note, including sustain
	 */
	public void setActualStartBeatNumber(double actualStartBeatNumber) {
		this.actualStartBeatNumber = actualStartBeatNumber;
		freqAndBend.setWaveInfoDirty( true );
	}

	/**
	 * Gets the actual envelope of the note.
	 * @return The actual envelope of the note.
	 */
	public ClampedCoefficient getActualNoteEnvelope( final int core ) {
		return actualNoteEnvelope[ core ];
	}

	/**
	 * Sets the actual envelope of the note.
	 * @param actualNoteEnvelope The actual envelope of the note.
	 */
	public void setActualNoteEnvelope(ClampedCoefficient actualNoteEnvelope) throws Throwable {
		this.actualNoteEnvelope[ 0 ] = actualNoteEnvelope;
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 1 ; count < max ; count++ )
		{
			if( actualNoteEnvelope != null )
			{
				this.actualNoteEnvelope[ count ] = (ClampedCoefficient)( actualNoteEnvelope.genClone() );
			}
			else
			{
				this.actualNoteEnvelope[ count ] = null;
			}
		}
	}

	/**
	 * Gets the frequency and pitch-bend (including vibrato) of the note.
	 * @return The frequency and pitch-bend (including vibrato) of the note.
	 */
	public FreqAndBend getFreqAndBend() {
		return freqAndBend;
	}

	/**
	 * Sets the frequency and pitch-bend (including vibrato) of the note.
	 * @param freqAndBend The frequency and pitch-bend (including vibrato) of the note.
	 */
	public void setFreqAndBend(FreqAndBend freqAndBend) {
		this.freqAndBend = freqAndBend;
		freqAndBend.setWaveInfoDirty( true );
	}


	/**
	 * Gets a reference to context data that the generating code can associate with the note.
	 * @return The reference to context data that the generating code can associate with the note.
	 */
	public Object getRefcon() {
		return refcon;
	}


	/**
	 * Sets a reference to context data that the generating code can associate with the note.
	 * @param refcon The reference to context data that the generating code can associate with the note.
	 */
	public void setRefcon(Object refcon) {
		this.refcon = refcon;
	}


	/**
	 * Gets the estimated number of wavelengths after which the timbre becomes essentially steady-state.
	 * @return The estimated number of wavelengths after which the timbre becomes essentially steady-state.
	 */
	public double getSteadyStateWaveNum() {
		return steadyStateWaveNum;
	}


	/**
	 * Sets the estimated number of wavelengths after which the timbre becomes essentially steady-state.
	 * @param steadyStateWaveNum The estimated number of wavelengths after which the timbre becomes essentially steady-state.
	 */
	public void setSteadyStateWaveNum(double steadyStateWaveNum) {
		this.steadyStateWaveNum = steadyStateWaveNum;
	}


	/**
	 * Gets the total envelope mode of the note.  Allowed values are TOTAL_ENVELOPE_MODE_NONE, TOTAL_ENVELOPE_MODE_DRUM, and TOTAL_ENVELOPE_MODE_MULT.
	 * @return The total envelope mode of the note.  Allowed values are TOTAL_ENVELOPE_MODE_NONE, TOTAL_ENVELOPE_MODE_DRUM, and TOTAL_ENVELOPE_MODE_MULT.
	 */
	public int getTotalEnvelopeMode() {
		return totalEnvelopeMode;
	}


	/**
	 * Sets the total envelope mode of the note.  Allowed values are TOTAL_ENVELOPE_MODE_NONE, TOTAL_ENVELOPE_MODE_DRUM, and TOTAL_ENVELOPE_MODE_MULT.
	 * @param totalEnvelopeCoeff The total envelope mode of the note.  Allowed values are TOTAL_ENVELOPE_MODE_NONE, TOTAL_ENVELOPE_MODE_DRUM, and TOTAL_ENVELOPE_MODE_MULT.
	 */
	public void setTotalEnvelopeMode(int totalEnvelopeMode) {
		this.totalEnvelopeMode = totalEnvelopeMode;
	}


	/**
	 * Gets the total envelope coefficient.  See the total envelope mode.
	 * @return The total envelope coefficient.
	 */
	public double getTotalEnvelopeCoeff() {
		return totalEnvelopeCoeff;
	}


	/**
	 * Sets the total envelope coefficient.  See the total envelope mode.
	 * @param totalEnvelopeCoeff The total envelope coefficient.
	 */
	public void setTotalEnvelopeCoeff(double totalEnvelopeCoeff) {
		this.totalEnvelopeCoeff = totalEnvelopeCoeff;
	}
	
	/**
	 * Gets the minimum value of the envelope for the note.
	 * @param core The number of the core thread.
	 * @return The minimum value of the envelope for the note.
	 */
	public double getMinNoteEnvelope( final int core )
	{
		int count;
		ArrayList<InterpolationPoint> interpolationPoints = getNoteEnvelope( core ).getBez().getInterpolationPoints();
		int sz = interpolationPoints.size();
		InterpolationPoint pt = interpolationPoints.get( 0 );
		double env = pt.getValue();
		for( count = 1 ; count < sz ; count++ )
		{
			pt = interpolationPoints.get( count );
			env = Math.min( env , pt.getValue() );
		}
		return( env );
	}
	
	/**
	 * Gets the maximum value of the envelope for the note.
	 * @param core The number of the core thread.
	 * @return The maximum value of the envelope for the note.
	 */
	public double getMaxNoteEnvelope( final int core )
	{
		int count;
		ArrayList<InterpolationPoint> interpolationPoints = getNoteEnvelope( core ).getBez().getInterpolationPoints();
		int sz = interpolationPoints.size();
		InterpolationPoint pt = interpolationPoints.get( 0 );
		double env = pt.getValue();
		for( count = 1 ; count < sz ; count++ )
		{
			pt = interpolationPoints.get( count );
			env = Math.max( env , pt.getValue() );
		}
		return( env );
	}
	
	/**
	 * Gets the ending wave number of the note.
	 * @param core The number of the core thread.
	 * @return The ending wave number of the note.
	 */
	public double getEndWaveNumber( final int core )
	{
		return( freqAndBend.getEndWaveNumber( this , core ) );
	}


	/**
	 * Gets whether to use the user-defined (as opposed to agent-defined) note envelope.
	 * @return Whether to use the user-defined (as opposed to agent-defined) note envelope.
	 */
	public boolean isUserDefinedNoteEnvelope() {
		return userDefinedNoteEnvelope;
	}


	/**
	 * Sets whether to use the user-defined (as opposed to agent-defined) note envelope.
	 * @param userDefinedNoteEnvelope Whether to use the user-defined (as opposed to agent-defined) note envelope.
	 */
	public void setUserDefinedNoteEnvelope(boolean userDefinedNoteEnvelope) {
		this.userDefinedNoteEnvelope = userDefinedNoteEnvelope;
	}


	/**
	 * Gets the vibrato parameters of the note.
	 * @return The vibrato parameters of the note.
	 */
	public VibratoParameters getVibratoParams() {
		return vibratoParams;
	}


	/**
	 * Sets the vibrato parameters of the note.
	 * @param vibratoParams The vibrato parameters of the note.
	 */
	public void setVibratoParams(VibratoParameters vibratoParams) {
		this.vibratoParams = vibratoParams;
	}
	
	
	/**
	 * Gets whether the end of the note is user-defined (as opposed to agent-defined).
	 * @return Whether the end of the note is user-defined (as opposed to agent-defined).
	 */
	public boolean isUserDefinedEnd() {
		return isUserDefinedEnd;
	}


	/**
	 * Sets whether the end of the note is user-defined (as opposed to agent-defined).
	 * @param isUserDefinedEnd Whether the end of the note is user-defined (as opposed to agent-defined).
	 */
	public void setUserDefinedEnd(boolean isUserDefinedEnd) {
		this.isUserDefinedEnd = isUserDefinedEnd;
	}


	/**
	 * Gets whether the vibrato is user-defined (as opposed to agent-defined).
	 * @return Whether the vibrato is user-defined (as opposed to agent-defined).
	 */
	public boolean isUserDefinedVibrato() {
		return isUserDefinedVibrato;
	}


	/**
	 * Sets whether the vibrato is user-defined (as opposed to agent-defined).
	 * @param isUserDefinedVibrato Whether the vibrato is user-defined (as opposed to agent-defined).
	 */
	public void setUserDefinedVibrato(boolean isUserDefinedVibrato) {
		this.isUserDefinedVibrato = isUserDefinedVibrato;
	}


	/**
	 * Gets the portamento description of the note.
	 * @return The portamento description of the note.
	 */
	public PortamentoDesc getPortamentoDesc() {
		return portamentoDesc;
	}


	/**
	 * Sets the portamento description of the note.
	 * @param portamentoDesc The portamento description of the note.
	 */
	public void setPortamentoDesc(PortamentoDesc portamentoDesc) {
		this.portamentoDesc = portamentoDesc;
	}

	
}

