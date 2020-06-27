package agents;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import meta.DataFormatException;
import meta.VersionBuffer;
import bezier.BezierCubicClampedCoefficient;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import core.InstrumentTrack;
import core.IntelligentAgent;
import core.NoteDesc;
import core.NoteInitializer;
import core.SongData;
import core.TrackFrame;
import core.VibratoParameters;
import core.WaveForm;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.SampledWaveform;
import java.io.*;

import javax.swing.JFileChooser;


/**
 * Agent that plays sampled sound from a .wav file.
 * 
 * @author tgreen
 *
 */
public class SampledAgent extends IntelligentAgent implements Externalizable {
	
	/**
	 * Stores each interpolated rough-draft waveform under a unique name.
	 */
	public static final HashMap<String,WaveForm> roughDraftWaveformMap = new HashMap<String,WaveForm>();
	
	/**
	 * The .wav file to be sampled.
	 */
	File fi;


	protected void processFile() throws Throwable
	{
		while( fi == null )
		{
			Frame fr = null;
			
			// FileDialog fd = new FileDialog( fr , "Save", FileDialog.SAVE);
			
			// fd.show();
			
			// if( fd.getFile() == null )
			// {
			//	return;
			// }
			
			// fi = new File( fd.getFile() );
			
			JFileChooser fc = new JFileChooser( new File( "/home/tgreen" ) );
			
			fc.showOpenDialog( fr );
			
			if( fc.getSelectedFile() == null )
			{
				return;
			 }
				
			fi = fc.getSelectedFile();
		}
	}
	
	public void processSound() throws Throwable
	{
		if( fi == null )
		{
			processFile();
		}
		
		System.out.println( fi );
		
	}
	
	/**
	 * Constructs the agent.  This is usually invoked by introspection.
	 */
	public SampledAgent() {
		super();
	}
	
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( 1.0 );
		NoteInitializer fb = new NoteInitializer( 1.0 );
		noteInitializers.clear();
		noteInitializers.add( fa );
		noteInitializers.add( fb );
	}
	
	public SampledAgent( File _fi )
	{
		this();
		fi = _fi;
	}
	
	/**
	 * Plays a .wav file for a particular note.
	 * @param fi The file to be played.
	 * @param note The note for which to play the file.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument( File fi , NoteDesc note ) throws Throwable
	{
		currentNote = note;
		
		WaveForm wave = new SampledWaveform( fi );
		
		wave = editPack1.processWave( wave );
		
		note.setWaveform( wave );		
		SongData.buildFlatInterpPoints(note.getFreqAndBend());
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}
	
	/**
	 * Plays a .wav file for a note other than the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @param note2 The next note after the note to be processed.
	 * @throws Throwable
	 */
	protected void processFirstNote( File fi , NoteDesc note1 , NoteDesc note2 ) throws Throwable
	{
		final int core = 0;
		note1.setActualEndBeatNumberValidated( note1.getEndBeatNumber() , note2.getStartBeatNumber() );
		note1.setActualStartBeatNumber( note1.getStartBeatNumber() );
		setInitialNoteEnvelope( note1 , note2 , 0.0 );
		note1.setActualNoteEnvelope( note1.getNoteEnvelope( core ) );
		note1.setWaveEnvelope(new ConstantNonClampedCoefficient(1.0));
		buildNoteInstrument( fi, note1 );
	}
	
	/**
	 * Plays a .wav file for the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @throws Throwable
	 */
	protected void processLastNote( File fi , NoteDesc note1 ) throws Throwable
	{
		final int core = 0;
		note1.setActualEndBeatNumberValidated( note1.getEndBeatNumber() , 1E+40 );
		note1.setActualStartBeatNumber( note1.getStartBeatNumber() );
		setInitialNoteEnvelope( note1 , null , 0.0 );
		note1.setActualNoteEnvelope( note1.getNoteEnvelope( core ) );
		note1.setWaveEnvelope(new ConstantNonClampedCoefficient(1.0));
		buildNoteInstrument( fi, note1 );
	}
	
	/**
	 * Produces a representation of how a musician would play an instrument on a particular track frame.
	 * @param tr The track frame.
	 * @throws Throwable
	 */
	protected void processTrackFrame(File fi , TrackFrame tr) throws Throwable {
		NoteDesc note1 = null;
		NoteDesc note2 = null;

		ArrayList<NoteDesc> notes = tr.getNotes();
		if (notes.size() > 0) {
			for( final NoteDesc note : notes ) {
				note1 = note2;
				note2 = note;
				if (note1 != null) {
					processFirstNote( fi, note1, note2);
				}
			}

			if (note2 != null) {
				processLastNote(fi, note2);
			} else {
				processLastNote(fi, note1);
			}
		}
	}

	@Override
	public void processTrack(InstrumentTrack track) {
		final int core = 0;
		try
		{
			if( fi == null )
			{
				processSound();
			}
				
			track.updateTrackFramesComp( core );
			ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
			for( final TrackFrame tr : trackFrames ) {
				processTrackFrame(fi, tr);
			}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
			throw( new RuntimeException( "Failed." ) );
		}

	}
	
	@Override 
	public void setInitialNoteEnvelope( NoteDesc nd , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		if( !( nd.isUserDefinedVibrato() ) )
		{
			nd.setVibratoParams( new VibratoParameters( vibratoParams ) );
		}
		if( !( nd.isUserDefinedNoteEnvelope() ) )
		{
			PiecewiseCubicMonotoneBezierFlat bezAC = SongData.buildConstBezier();
			nd.getVibratoParams().buildTremolo( nd , nxt , minDecayTimeBeats , bezAC.getInterpolationPoints() , core );
			bezAC.updateAll();
			BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		
			nd.setNoteEnvelope( bezA );
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("File", fi.getAbsolutePath() );

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			String path = (String)( myv.getPropertyEx( "File" ) );
			fi = new File( path );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

