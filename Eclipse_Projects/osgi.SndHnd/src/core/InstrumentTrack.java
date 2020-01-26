




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
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;

/**
 * An instrument track per the multitrack tape recorder metaphor described in https://en.wikipedia.org/wiki/Digital_audio_workstation#Common_functionality.
 * 
 * Note: in most other systems a MIDI track has a synthesizer, but here a track has an agent which has the equivalent of a set of synthesizers. 
 * 
 * @author tgreen
 *
 */
public class InstrumentTrack implements Externalizable {

	/**
	 * The set of frames for the track.
	 */
	protected ArrayList<TrackFrame> trackFrames = new ArrayList<TrackFrame>();

	/**
	 * The track volume (i.e. envelope) of the track.
	 */
	protected final BezierCubicNonClampedCoefficientFlat[] trackVolume = new BezierCubicNonClampedCoefficientFlat[ CpuInfo.getNumCores() ];

	/**
	 * The agent associated with the track.
	 */
	protected IntelligentAgent agent = null;
	
	/**
	 * Whether the track is turned on (i.e. audible).
	 */
	protected boolean trackOn = true;

	
	/**
	 * Constructor.
	 * @throws Throwable
	 */
	public InstrumentTrack() throws Throwable {
		super();
		PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 1.0 ) );
		bez.getInterpolationPoints().add( new InterpolationPoint( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) , 1.0 ) );
		bez.updateAll();
		setTrackVolume( new BezierCubicNonClampedCoefficientFlat( bez ) );
	}

	/**
	 * Updates the set of frames in the track in the special case where the agent is null.
	 * @param core The number of the core thread.
	 * @throws Throwable
	 */
	public void updateTrackFramesComp( final int core ) throws Throwable {

		for( final TrackFrame frame : trackFrames ) {
			for( final NoteDesc desc : frame.getNotes() ) {
				desc.setActualStartBeatNumber(desc.getStartBeatNumber());
				desc.setActualEndBeatNumber(desc.getEndBeatNumber());
				desc.setActualNoteEnvelope(desc.getNoteEnvelope( core ));
				desc.setSteadyStateWaveNum( 100 );
			}
		}

		for( final TrackFrame frame : trackFrames ) {
			TreeMap<Double,NoteDesc> tmap = new TreeMap<Double,NoteDesc>();
			for( final NoteDesc desc : frame.getNotes() ) {
				desc.setActualStartBeatNumber(desc.getStartBeatNumber());
				desc.setActualEndBeatNumber(desc.getEndBeatNumber());
				desc.setActualNoteEnvelope(desc.getNoteEnvelope( core ));
				desc.setSteadyStateWaveNum( 100 );

				Double key = new Double(desc.getStartBeatNumber());
				if (tmap.get(key) != null) {
					System.out.println("Deleting at beat number " + key);
				}
				tmap.put(key, desc);
			}

			frame.getNotes().clear();

			for ( Entry<Double,NoteDesc> e : tmap.entrySet() ) {
				Double key = e.getKey();
				NoteDesc desc = e.getValue();
				frame.getNotes().add(desc);
			}
		}

	}

	/**
	 * Updates the set of frames in the track.
	 * @param core The number of the core thread.
	 * @throws Throwable
	 */
	public void updateTrackFrames( final int core ) throws Throwable {
		if (agent != null) {
			agent.processTrack(this);
		} else {
			updateTrackFramesComp( core );
		}
	}

	/**
	 * Gets the set of frames for the track.
	 * @return The set of frames for the track.
	 */
	public ArrayList<TrackFrame> getTrackFrames() {
		return trackFrames;
	}

	/**
	 * Sets the set of frames for the track.
	 * @param trackFrames The set of frames for the track.
	 */
	public void setTrackFrames(ArrayList<TrackFrame> trackFrames) {
		this.trackFrames = trackFrames;
	}

	/**
	 * Gets the track volume (i.e. envelope) of the track.
	 * @return The track volume (i.e. envelope) of the track.
	 */
	public BezierCubicNonClampedCoefficientFlat getTrackVolume( final int core ) {
		return trackVolume[ core ];
	}

	/**
	 * Sets the track volume (i.e. envelope) of the track.
	 * @param trackVolume The track volume (i.e. envelope) of the track.
	 */
	public void setTrackVolume(BezierCubicNonClampedCoefficientFlat trackVolume) throws Throwable {
		this.trackVolume[0] = trackVolume;
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 1 ; count < max ; count++ )
		{
			this.trackVolume[ count ] = (BezierCubicNonClampedCoefficientFlat)( trackVolume.genClone() );
		}
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			int plen = myv.getInt("FrameSize");
			trackFrames = new ArrayList<TrackFrame>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				trackFrames.add((TrackFrame) (myv.getPropertyEx("Frame_"
						+ count)));
			}
			agent = (IntelligentAgent) (myv.getProperty("Agent"));
			Object ob = myv.getProperty("TrackVolume");
			if( ob != null )
			{
				setTrackVolume( (BezierCubicNonClampedCoefficientFlat) ob );
			}
			ob = myv.getProperty("TrackOn");
			if( ob != null )
			{
				trackOn = ( (Boolean) ob ).booleanValue();
			}
		} catch (Throwable ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("FrameSize", trackFrames.size());
		int plen = trackFrames.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Frame_" + count, trackFrames.get( count ));
		}
		myv.setProperty("Agent", agent);
		myv.setProperty("TrackVolume", trackVolume[0] );
		myv.setBoolean("TrackOn", trackOn);

		out.writeObject(myv);
	}

	/**
	 * Gets the agent associated with the track.
	 * @return The agent associated with the track.
	 */
	public IntelligentAgent getAgent() {
		return agent;
	}

	/**
	 * Sets the agent associated with the track.
	 * @param agent The agent associated with the track.
	 */
	public void setAgent(IntelligentAgent agent) {
		this.agent = agent;
	}

	/**
	 * Gets whether the track is turned on (i.e. audible).
	 * @return Whether the track is turned on (i.e. audible).
	 */
	public boolean isTrackOn() {
		return trackOn;
	}

	/**
	 * Sets whether the track is turned on (i.e. audible).
	 * @param trackOn Whether the track is turned on (i.e. audible).
	 */
	public void setTrackOn(boolean trackOn) {
		this.trackOn = trackOn;
	}

}

