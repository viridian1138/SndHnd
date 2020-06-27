package agents;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import meta.DataFormatException;
import meta.VersionBuffer;
import noise.GHashWhiteWaveForm;
import noise.GMarbleWave;
import noise.GWardNoiseWaveform;
import waves.GGroupWave;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.GBezierCubicNonClampedCoefficientFlat;
import bezier.GPiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import core.InstrumentTrack;
import core.IntelligentAgent;
import core.InterpolationPoint;
import core.NoteDesc;
import core.NoteInitializer;
import core.NoteTable;
import core.SongData;
import core.TrackFrame;
import core.WaveForm;
import cwaves.AmplitudeModulationWaveForm;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.GAdditivePacket;
import cwaves.GAdditiveWaveForm;
import cwaves.GConstantNonClampedCoefficient;
import cwaves.SineWaveform;


/**
 * 
 * Percussion agent to emulate an effect similar to the slapping of a metal plate.
 * 
 * Note: like (some) real percussive instruments, MetalPlatePercussionAgent plays on a particular key.  That is to say, the key of the percussion could match/mismatch the key of the rest of the song.
 * However the frequency of a note for the agent is a function of the underlying lattice spacing rather than the perceived audible pitch.
 * 
 * The implementation of this class was influenced by several SouundOnSound articles from roughly 2002 such as "Synthesizing Drums: The Snare Drum", SoundOnSound Magazine, March 2002.
 * 
 * @author tgreen
 *
 */
public class MetalPlatePercussionAgent extends IntelligentAgent implements Externalizable {
	
	/**
	 * Stores each interpolated rough-draft waveform under a unique name.
	 */
	public static final HashMap<String,WaveForm> roughDraftWaveformMap = new HashMap<String,WaveForm>();
	
	/**
	 * Frequency ratio of the low-pass filter applied to the noise function to model the post-strike resonance of the instrument.
	 */
	protected final double NOISE_LOW_PASS_FREQ_RATIO = 800 / 330.0;
	
	/**
	 * The number of lattice spacings over which the post-strike resonance of the instrument dissipates.
	 */
	protected final double DECAY_LENGTH = 2 * 800.0;
	
	/**
	 * The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	protected double minDecayTimeBeats = 4.0;
	
	
	/**
	 * Builds a waveform group for a quickly decaying sine wave.  This models part of the initial strike of the instrument, as opposed to the subsequent resonance of the instrument.
	 * @return The waveform group for the quickly decaying sine wave.
	 */
	protected static GGroupWave buildTopGroup( )
	{
		GGroupWave waveGrp = new GGroupWave();
		
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , -10 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 1.0 , -5 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 2.0 , -2.5 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 3.0 , -1.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 4.0 , -0.50125 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.0 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 6.0 , 0.0 ) );
		bezAC.updateAll();
		
		BezierCubicNonClampedCoefficientFlat coeff = new BezierCubicNonClampedCoefficientFlat( bezAC );
		
		AmplitudeModulationWaveForm wave =
			new AmplitudeModulationWaveForm( new SineWaveform() , coeff );
		
		waveGrp.getEdit().getWaveOut().performAssign( wave.genWave( new HashMap() ) );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),waveGrp.getEdit().getElem(), null, waveGrp.getEdit().getWaveOut());
		return( waveGrp );
	}
	
	
	
	/**
	 * Builds the waveform for the precussive timbre.
	 * @return The waveform for the percussive timbre.
	 */
	protected WaveForm buildSnareWaveForm( )
	{	
		WaveForm wave = getEditPack2().processWave( new SineWaveform() );
		
		wave = editPack1.processWave( wave );
		
		return( wave );
	}

	/**
	 * Constructs the agent.  This is usually invoked by introspection.
	 */
	public MetalPlatePercussionAgent() {
		super();
		
		GGroupWave hz180Grp = buildTopGroup();
		{
			hz180Grp.setName( "hz180" );
		}
		
		GGroupWave hz330Grp = buildTopGroup();
		{
			hz330Grp.setName( "hz330" );
		}
		
		GGroupWave noiseGrp = new GGroupWave();
		{
			noiseGrp.setName( "NoiseGroup" );
			GHashWhiteWaveForm white = new GHashWhiteWaveForm();
			white.load(1E+5,0.53147638582379454239574 / 0.5, 0.637724401110901,65599);
			GWardNoiseWaveform vg = new GWardNoiseWaveform();
			vg.load( white , 0.5 );
			GMarbleWave gm = new GMarbleWave();
			gm.load( vg );
			noiseGrp.getEdit().getWaveOut().performAssign( gm );
			AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),noiseGrp.getEdit().getElem(), null, noiseGrp.getEdit().getWaveOut());
		}
		
		final double DIV = 5.22;
		
		PiecewiseCubicMonotoneBezierFlat ibez = SongData.buildGradualDecayBezier( 25 , 
				0.0 , DECAY_LENGTH , 0.38 / DIV );
		ibez.updateAll();
		GPiecewiseCubicMonotoneBezierFlat bez = ibez.genBez( new HashMap() );
		
		
		GAdditiveWaveForm addi = new GAdditiveWaveForm();
		{
			Vector<GAdditivePacket> packets = new Vector<GAdditivePacket>();
			double freqRatio = 180.0 / 330.0;
			GConstantNonClampedCoefficient mult2 = new GConstantNonClampedCoefficient( freqRatio );
			GConstantNonClampedCoefficient mult3 = new GConstantNonClampedCoefficient( 1.0 / NOISE_LOW_PASS_FREQ_RATIO );
			GAdditivePacket addp0 = new GAdditivePacket();
			GAdditivePacket addp1 = new GAdditivePacket();
			GBezierCubicNonClampedCoefficientFlat gbez = new GBezierCubicNonClampedCoefficientFlat();
			gbez.load( bez );
			addp0.load( hz180Grp , new GConstantNonClampedCoefficient( 0.3333333 / DIV ) , mult2 );
			addp1.load( noiseGrp , gbez , mult3 );
			packets.add( addp0 );
			packets.add( addp1 );
			addi.load( hz330Grp , new GConstantNonClampedCoefficient( 0.333333333 / DIV ) , packets );
		}
		
		getEditPack2().getWaveOut().performAssign( addi );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack2().getElem(), null, getEditPack2().getWaveOut());
		
		applyStdAmplRoll( getEditPack1().getWaveIn() , getEditPack1().getWaveOut() , getEditPack1() );
	}
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(5,
				NoteTable.STEPS_A) );
		NoteInitializer fb = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(5,
				NoteTable.STEPS_A) );
		noteInitializers.clear();
		noteInitializers.add( fa );
		noteInitializers.add( fb );
	}

	@Override
	public void processTrack(InstrumentTrack track) throws Throwable {
		final int core = 0;
		
		track.updateTrackFramesComp( core );
		
		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		for( final TrackFrame tr : trackFrames ) {
			processTrackFrame( tr );
		}

	}
	
	
	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument( NoteDesc note ) throws Throwable
	{
		currentNote = note;
		final int core = 0;
		WaveForm wave = buildSnareWaveForm( );
		note.setWaveform( wave );
		double decayTimeBeats = note.getActualEndBeatNumber() - note.getActualStartBeatNumber();
		note.setSteadyStateWaveNum( 2.0 );
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}
	
	
	/**
	 * Produces a representation of how a musician would play an instrument for a note other than the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @param note2 The next note after the note to be processed.
	 * @throws Throwable
	 */
	protected void processFirstNote( NoteDesc note1 , NoteDesc note2 ) throws Throwable
	{
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, note2.getStartBeatNumber());
		double decayTimeBeatsFinal = note1.getActualEndBeatNumber() - note1.getStartBeatNumber();
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , note2 , minDecayTimeBeats );
		note1.setActualNoteEnvelope( note1.getNoteEnvelope( core ) );
		note1.setWaveEnvelope( new ConstantNonClampedCoefficient( 1.0 ) );
		buildNoteInstrument( note1 );
		SongData.buildBendInterpPoints(note1,note2,10,decayTimeBeats,true,core);
	}
	
	/**
	 * Produces a representation of how a musician would play an instrument for the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @throws Throwable
	 */
	protected void processLastNote( NoteDesc note1 ) throws Throwable
	{
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, 1E+40);
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , null , minDecayTimeBeats );
		note1.setActualNoteEnvelope( note1.getNoteEnvelope( core ) );
		note1.setWaveEnvelope( new ConstantNonClampedCoefficient( 1.0 ) );
		buildNoteInstrument( note1 );
		SongData.buildBendInterpPoints(note1,null,10,decayTimeBeats,true,core);
	}
	
	/**
	 * Produces a representation of how a musician would play an instrument on a particular track frame.
	 * @param tr The track frame.
	 * @throws Throwable
	 */
	protected void processTrackFrame( TrackFrame tr ) throws Throwable
	{
		NoteDesc note1 = null;
		NoteDesc note2 = null;
		
		ArrayList<NoteDesc> notes = tr.getNotes();
		if (notes.size() > 0) {
		for( final NoteDesc note : notes )
		{
			note1 = note2;
			note2 = note;
			if( note1 != null )
			{
				processFirstNote( note1 , note2 );
			}
		}
		
		if( note2 != null )
		{
			processLastNote( note2 );
		}
		else
		{
			processLastNote( note1 );
		}
		}
	}

	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		myv.setDouble("MinDecayTimeBeats", minDecayTimeBeats);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			minDecayTimeBeats = myv.getDouble("MinDecayTimeBeats");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	

}

